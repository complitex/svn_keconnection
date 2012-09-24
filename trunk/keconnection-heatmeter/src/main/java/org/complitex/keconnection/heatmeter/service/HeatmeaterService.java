package org.complitex.keconnection.heatmeater.service;

import au.com.bytecode.opencsv.CSVReader;
import org.complitex.dictionary.service.IProcessListener;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.keconnection.address.strategy.building.KeConnectionBuildingStrategy;
import org.complitex.keconnection.heatmeater.entity.*;
import org.complitex.keconnection.heatmeater.service.exception.BuildingNotFoundException;
import org.complitex.keconnection.heatmeater.service.exception.OrganizationNotFoundException;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.09.12 17:53
 */
@Stateless
public class HeatmeaterService {
    private final static Logger log = LoggerFactory.getLogger(HeatmeaterService.class);

    @EJB
    private HeatmeaterBean heatmeaterBean;

    @EJB
    private HeatmeaterPeriodBean heatmeaterPeriodBean;

    @EJB
    private IKeConnectionOrganizationStrategy organizationStrategy;

    @EJB
    private KeConnectionBuildingStrategy buildingStrategy;

    private final Date DEFAULT_BEGIN_DATE = DateUtil.newDate(1, 10, 2012);

    @Asynchronous
    public void uploadHeatmeaters(InputStream inputStream, IProcessListener<HeatmeaterWrapper> listener){
        HeatmeaterWrapper heatmeaterWrapper = null;

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

                    heatmeaterWrapper = new HeatmeaterWrapper(lineNum, line[0], line[1], ls);

                    //check duplicates
                    if (heatmeaterBean.isExist(heatmeaterWrapper.getHeatmeater())){
                        listener.skip(heatmeaterWrapper);
                        continue;
                    }

                    //create heatmeater
                    try {
                        createHeatmeater(heatmeaterWrapper);

                        listener.processed(heatmeaterWrapper);
                    } catch (BuildingNotFoundException | OrganizationNotFoundException e) {
                        listener.error(heatmeaterWrapper, e);
                    }
                }
            }

            //manual close stream
            bufferedReader.close();

            //done
            listener.done();
        } catch (Exception e) {
            listener.error(heatmeaterWrapper, e);
            listener.done();

            log.error("Ошибка импорта счетчика {} ", heatmeaterWrapper, e);
        }
    }

    public void createHeatmeater(HeatmeaterWrapper heatmeaterWrapper) throws BuildingNotFoundException,
            OrganizationNotFoundException {
        //find organization
        Long organizationId = organizationStrategy.getObjectId(heatmeaterWrapper.getOrganizationCode());

        if (organizationId == null){
            throw new OrganizationNotFoundException(heatmeaterWrapper);
        }

        //find building code
        Long buildingCodeId = buildingStrategy.getBuildingCodeId(organizationId, heatmeaterWrapper.getBuildingCode());

        if (buildingCodeId == null){
            throw new BuildingNotFoundException(heatmeaterWrapper);
        }

        //save
        Heatmeater heatmeater = heatmeaterWrapper.getHeatmeater();
        heatmeater.setBuildingCodeId(buildingCodeId);
        heatmeater.setType(HeatmeterType.HEATING_AND_WATER);

        heatmeaterBean.save(heatmeater);

        //create period
        HeatmeaterPeriod period = new HeatmeaterPeriod();
        period.setHeatmeaterId(heatmeater.getId());
        period.setType(HeatmeaterPeriodType.OPERATION);
        period.setBeginDate(DEFAULT_BEGIN_DATE);
        period.setOperatingMonth(DEFAULT_BEGIN_DATE);

        heatmeaterPeriodBean.save(period);
        heatmeaterPeriodBean.updateParent(period.getId(), period.getId());
    }
}
