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
import org.complitex.keconnection.heatmeter.entity.Payload;
import org.complitex.keconnection.heatmeter.entity.PayloadImportFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.09.12 15:17
 */
@Stateless
public class PayloadImportService extends AbstractImportService{
    private static final Logger log = LoggerFactory.getLogger(PayloadImportService.class);

    private final Date DEFAULT_BEGIN_DATE = DateUtil.newDate(1, 10, 2012);

    @EJB
    private ConfigBean configBean;

    @EJB
    private HeatmeterBean heatmeterBean;

    @EJB
    private PayloadBean payloadBean;

    public void process(IImportFile importFile, IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException {
        Table table = getDbfTable(importFile);

        //begin
        listener.beginImport(importFile, table.getRecordCount());

        int index = 0;
        int processed = 0;

        //process
        for (Iterator<Record> it = table.recordIterator(); it.hasNext();){
            index++;

            Record record = it.next();

            Payload payload = new Payload();

            Integer ls = (Integer) record.getNumberValue("L_S");
            Long heatmeterId = heatmeterBean.getIdByLs(ls);

            if (heatmeterId == null){
                listener.warn(importFile, "Счетчик не найден по л/с " + ls);
                continue;
            }

            payload.setHeatmeterId(heatmeterId);

            payload.setPayload1((BigDecimal) record.getNumberValue("PR_T1"));
            payload.setPayload2((BigDecimal) record.getNumberValue("PR_T2"));
            payload.setPayload3((BigDecimal) record.getNumberValue("PR_T3"));

            payload.setOperatingMonth(DEFAULT_BEGIN_DATE);
            payload.setBeginDate(DEFAULT_BEGIN_DATE);

            payloadBean.save(payload);

            processed++;

            listener.recordProcessed(importFile, index);
        }

        listener.completeImport(importFile, processed);
    }

    public List<PayloadImportFile> getPayloadImportFiles(){
        List<PayloadImportFile> payloadImportFiles = new ArrayList<>();

        String[] names = getFileList(getDir(), "dbf");

        for (String name : names){
            payloadImportFiles.add(new PayloadImportFile(name));
        }

        return payloadImportFiles;
    }

    @Override
    protected String getDir() {
        return configBean.getString(HeatmeterConfig.IMPORT_PAYLOAD_DIR, true);
    }
}
