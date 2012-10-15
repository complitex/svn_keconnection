package org.complitex.keconnection.heatmeter.service;

import nl.knaw.dans.common.dbflib.Record;
import nl.knaw.dans.common.dbflib.Table;
import org.complitex.dictionary.entity.IImportFile;
import org.complitex.dictionary.service.AbstractImportService;
import org.complitex.dictionary.service.ConfigBean;
import org.complitex.dictionary.service.IImportListener;
import org.complitex.dictionary.service.exception.ImportFileNotFoundException;
import org.complitex.dictionary.service.exception.ImportFileReadException;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConfig;
import org.complitex.keconnection.heatmeter.entity.PayloadImportFile;
import org.complitex.keconnection.heatmeter.entity.Tablegram;
import org.complitex.keconnection.heatmeter.entity.TablegramRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.complitex.keconnection.heatmeter.entity.TablegramRecordStatus.LOADED;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.09.12 15:17
 */
@Stateless
public class TablegramImportService extends AbstractImportService{
    private static final Logger log = LoggerFactory.getLogger(TablegramImportService.class);

    private final Date DEFAULT_BEGIN_DATE = DateUtil.newDate(1, 10, 2012);

    @EJB
    private ConfigBean configBean;

    @EJB
    private HeatmeterBean heatmeterBean;

    @EJB
    private TablegramBean tablegramBean;

    @EJB
    private TablegramRecordBean tablegramRecordBean;

    public void process(IImportFile importFile, IImportListener listener) throws ImportFileNotFoundException,
            ImportFileReadException {
        Table table = getDbfTable(importFile.getFileName());

        //begin
        listener.beginImport(importFile, table.getRecordCount());

        //create tablegram file
        Tablegram tablegram = new Tablegram();

        tablegram.setFileName(importFile.getFileName());
        tablegram.setOperatingMonth(DEFAULT_BEGIN_DATE);

        int processed = 0;

        if (!tablegramBean.isExist(tablegram)){
            //save tablegram
            tablegram.setCount(table.getRecordCount());
            tablegramBean.save(tablegram);

            int index = 0;

            //process
            for (Iterator<Record> it = table.recordIterator(); it.hasNext();){
                index++;

                Record record = it.next();

                TablegramRecord tablegramRecord = new TablegramRecord();

                tablegramRecord.setTablegramId(tablegram.getId());

                tablegramRecord.setLs(record.getNumberValue("L_S").intValue());
                tablegramRecord.setName(record.getStringValue("NAM_AB"));
                tablegramRecord.setAddress(record.getStringValue("ADR_AB"));
                tablegramRecord.setPayload1((BigDecimal) record.getNumberValue("PR_T1"));
                tablegramRecord.setPayload2((BigDecimal) record.getNumberValue("PR_T2"));
                tablegramRecord.setPayload3((BigDecimal) record.getNumberValue("PR_T3"));

                tablegramRecord.setStatus(LOADED);

                //save payload
                tablegramRecordBean.save(tablegramRecord);

                processed++;

                listener.recordProcessed(importFile, index);
            }
        }

        listener.completeImport(importFile, processed);
    }

    public List<PayloadImportFile> getPayloadImportFiles(){
        List<PayloadImportFile> payloadImportFiles = new ArrayList<>();

        String[] names = getFileList(getDir(), "dbf");

        if (names != null) {
            for (String name : names){
                payloadImportFiles.add(new PayloadImportFile(name));
            }
        }

        return payloadImportFiles;
    }

    @Override
    protected String getDir() {
        return configBean.getString(HeatmeterConfig.IMPORT_PAYLOAD_DIR, true);
    }
}
