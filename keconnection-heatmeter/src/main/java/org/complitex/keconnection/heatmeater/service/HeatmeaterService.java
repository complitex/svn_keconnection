package org.complitex.keconnection.heatmeater.service;

import au.com.bytecode.opencsv.CSVReader;
import org.complitex.dictionary.service.IProcessListener;
import org.complitex.keconnection.heatmeater.entity.Heatmeater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.complitex.dictionary.util.StringUtil.parseInt;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.09.12 17:53
 */
@Stateless
public class HeatmeaterService {
    private final static Logger log = LoggerFactory.getLogger(HeatmeaterService.class);

    @EJB
    private HeatmeaterBean heatmeaterBean;

    @Asynchronous
    public void uploadHeatmeaters(InputStream inputStream, IProcessListener<Heatmeater> listener){
        Heatmeater heatmeater = null;

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

                heatmeater = new Heatmeater();

                //todo

                if (heatmeaterBean.isExist(heatmeater)){
                    listener.skip(heatmeater);
                    continue;
                }

                heatmeaterBean.save(heatmeater);

                //processed
                listener.processed(heatmeater);
            }

            //manual close stream
            bufferedReader.close();

            //done
            listener.done();
        } catch (Exception e) {
            listener.error(heatmeater, e);

            log.error("Ошибка импорта счетчика. Строка: {} ", lineNum, e);
        }
    }
}
