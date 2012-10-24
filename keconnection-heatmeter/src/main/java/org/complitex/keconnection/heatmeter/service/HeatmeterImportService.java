package org.complitex.keconnection.heatmeter.service;

import au.com.bytecode.opencsv.CSVReader;
import org.complitex.dictionary.entity.IImportFile;
import org.complitex.dictionary.service.AbstractImportService;
import org.complitex.dictionary.service.ConfigBean;
import org.complitex.dictionary.service.IImportListener;
import org.complitex.dictionary.service.IProcessListener;
import org.complitex.dictionary.service.exception.ImportFileNotFoundException;
import org.complitex.dictionary.service.exception.ImportFileReadException;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.keconnection.address.strategy.building.KeConnectionBuildingStrategy;
import org.complitex.keconnection.heatmeter.entity.*;
import org.complitex.keconnection.heatmeter.service.exception.*;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy.KE_ORGANIZATION_OBJECT_ID;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.09.12 17:53
 */
@Stateless
public class HeatmeterImportService extends AbstractImportService{
    private final static Logger log = LoggerFactory.getLogger(HeatmeterImportService.class);

    @EJB
    private ConfigBean configBean;

    @EJB
    private HeatmeterBean heatmeterBean;

    @EJB
    private HeatmeterConnectionBean heatmeterConnectionBean;

    @EJB
    private HeatmeterPeriodBean heatmeterPeriodBean;

    @EJB
    private IKeConnectionOrganizationStrategy organizationStrategy;

    @EJB
    private KeConnectionBuildingStrategy buildingStrategy;

    public final static Date DEFAULT_BEGIN_DATE = DateUtil.newDate(1, 10, 2012);

    @Asynchronous
    public void asyncUploadHeatmeters(String fileName, InputStream inputStream, IProcessListener<HeatmeterWrapper> listener){
        uploadHeatmeters(fileName, inputStream, listener);
    }

    public void process(final IImportFile importFile, final IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException{
        int size = getRecordCount(importFile);

        listener.beginImport(importFile, size);

        uploadHeatmeters(importFile.getFileName(), getInputStream(importFile.getFileName()), new IProcessListener<HeatmeterWrapper>() {
            int index = 0;
            int processed = 0;

            @Override
            public void processed(HeatmeterWrapper object) {
                index++;
                processed++;

                listener.recordProcessed(importFile, index);
            }

            @Override
            public void skip(HeatmeterWrapper object) {
                index++;
            }

            @Override
            public void error(HeatmeterWrapper object, Exception e) {
                listener.warn(importFile, e.getMessage());
            }

            @Override
            public void done() {
                listener.completeImport(importFile, processed);
            }
        });
    }

    private void uploadHeatmeters(String fileName, InputStream inputStream, IProcessListener<HeatmeterWrapper> listener){
        HeatmeterWrapper heatmeaterWrapper = null;

        int lineNum = 0;

        try  {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            CSVReader reader = new CSVReader(bufferedReader, ';');

            String[] line;

            //skip first line
            reader.readNext();
            lineNum++;


            //"GEK";"DOM";"UL";"NDOM";"LOTOP0";"LOTOP1";"LOTOP2";"LOTOP3";"LOTOP4"
            while ((line = reader.readNext()) != null){
                lineNum++;

                for (int i = 0; i < 5; ++i){
                    String ls = line[4 + i];

                    //skip zero or empty lotop
                    if (ls.isEmpty() || ls.equals("0")){
                        continue;
                    }

                    String orgCode = line[0];
                    //organization leading zero
                    if (orgCode.length() < 4 && StringUtil.isNumeric(orgCode)){
                        orgCode = String.format("%04d", StringUtil.parseInt(orgCode));
                    }

                    heatmeaterWrapper = new HeatmeterWrapper(lineNum);

                    heatmeaterWrapper.setOrganizationCode(orgCode);
                    heatmeaterWrapper.setBuildingCode(line[1]);
                    heatmeaterWrapper.setLs(ls);
                    heatmeaterWrapper.setAddress(line[2] + " " + line[3]);
                    heatmeaterWrapper.setFileName(fileName);

                    //create heatmeter
                    try {
                        createHeatmeter(heatmeaterWrapper);

                        listener.processed(heatmeaterWrapper);
                    } catch (BuildingNotFoundException | OrganizationNotFoundException | NumberLsException e) {
                        listener.error(heatmeaterWrapper, e);
                    }catch (DuplicateException e){
                        listener.skip(heatmeaterWrapper);
                    }
                }
            }

            //manual close stream
            bufferedReader.close();

            //done
            listener.done();
        } catch (Exception e) {
            listener.error(heatmeaterWrapper, new CriticalHeatmeaterImportException(e, heatmeaterWrapper));
            listener.done();

            log.error("Ошибка импорта счетчика {} ", heatmeaterWrapper, e);
        }
    }

    public void createHeatmeter(HeatmeterWrapper heatmeaterWrapper) throws BuildingNotFoundException,
            OrganizationNotFoundException, DuplicateException, NumberLsException {
        //find organization by code
        Long organizationId = organizationStrategy.getObjectId(heatmeaterWrapper.getOrganizationCode());

        if (organizationId == null){
            throw new OrganizationNotFoundException(heatmeaterWrapper);
        }

        //find building code
        Long buildingCodeId = buildingStrategy.getBuildingCodeId(organizationId, heatmeaterWrapper.getBuildingCode());

        if (buildingCodeId == null){
            throw new BuildingNotFoundException(heatmeaterWrapper);
        }

        //ls
        Integer ls;
        try {
            ls = Integer.parseInt(heatmeaterWrapper.getLs());
        } catch (NumberFormatException e) {
            throw new NumberLsException(heatmeaterWrapper);
        }

        //check duplicates
        if (heatmeterBean.isExist(ls, buildingCodeId, organizationId)){
            throw new DuplicateException();
        }

        //heatmeter
        Heatmeter heatmeter = heatmeterBean.getHeatmeterByLs(ls, KE_ORGANIZATION_OBJECT_ID);

        if (heatmeter == null){
            heatmeter = new Heatmeter();
            heatmeter.setLs(ls);

            //default type
            heatmeter.setType(HeatmeterType.HEATING_AND_WATER);

            //default organization
            heatmeter.setOrganizationId(KE_ORGANIZATION_OBJECT_ID);

            //default calculating
            heatmeter.setCalculating(true);

            //save
            heatmeterBean.save(heatmeter);

            //create period
            HeatmeterPeriod period = new HeatmeterPeriod();
            period.setHeatmeterId(heatmeter.getId());
            period.setType(HeatmeterPeriodType.OPERATION);
            period.setBeginDate(DEFAULT_BEGIN_DATE);
            period.setOperatingMonth(DEFAULT_BEGIN_DATE);

            heatmeterPeriodBean.save(period);
            heatmeterPeriodBean.updateParent(period.getId(), period.getId());
        }

        //create heatmeter code
        HeatmeterConnection heatmeterConnection = new HeatmeterConnection();
        heatmeterConnection.setHeatmeterId(heatmeter.getId());
        heatmeterConnection.setBuildingCodeId(buildingCodeId);
        heatmeterConnection.setBeginDate(DEFAULT_BEGIN_DATE);
        heatmeterConnection.setOperatingMonth(DEFAULT_BEGIN_DATE);
        heatmeterConnectionBean.save(heatmeterConnection);
    }

    public List<HeatmeterImportFile> getHeatmeterImportFiles(){
        List<HeatmeterImportFile> importFiles = new ArrayList<>();

        String[] names = getFileList(getDir(), "csv");

        if (names != null) {
            for (String name : names){
                importFiles.add(new HeatmeterImportFile(name));
            }
        }

        return importFiles;
    }

    @Override
    protected String getDir() {
        return configBean.getString(HeatmeterConfig.IMPORT_HEATMETER_DIR, true);
    }
}
