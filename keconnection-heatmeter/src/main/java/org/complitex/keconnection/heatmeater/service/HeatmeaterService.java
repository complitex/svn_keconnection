package org.complitex.keconnection.heatmeater.service;

import au.com.bytecode.opencsv.CSVReader;
import org.complitex.dictionary.service.IProcessListener;
import org.complitex.keconnection.address.strategy.building.KeConnectionBuildingStrategy;
import org.complitex.keconnection.heatmeater.entity.Heatmeater;
import org.complitex.keconnection.heatmeater.entity.HeatmeaterWrapper;
import org.complitex.keconnection.heatmeater.entity.HeatmeterType;
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
    private IKeConnectionOrganizationStrategy organizationStrategy;

    @EJB
    private KeConnectionBuildingStrategy buildingStrategy;

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
                    createHeatmeater(heatmeaterWrapper);

                    //processed
                    listener.processed(heatmeaterWrapper);
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
        Long organizationId = organizationStrategy.getObjectId(heatmeaterWrapper.getBuildingCode());

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
    }
}
