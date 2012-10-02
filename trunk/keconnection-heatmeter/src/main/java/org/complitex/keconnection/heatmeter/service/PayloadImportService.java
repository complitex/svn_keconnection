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
import org.complitex.keconnection.heatmeter.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy.KE_ORGANIZATION_OBJECT_ID;

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

    public void process(IImportFile importFile, IImportListener listener) throws ImportFileNotFoundException,
            ImportFileReadException {
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

            Heatmeter heatmeter = heatmeterBean.getHeatmeterByLs(ls, KE_ORGANIZATION_OBJECT_ID);

            if (heatmeter == null){
                listener.warn(importFile, "Счетчик не найден по л/с " + ls);
                continue;
            }

            if (payloadBean.isExist(heatmeter.getId())){
                continue;
            }

            payload.setHeatmeterId(heatmeter.getId());

            payload.setPayload1((BigDecimal) record.getNumberValue("PR_T1"));
            payload.setPayload2((BigDecimal) record.getNumberValue("PR_T2"));
            payload.setPayload3((BigDecimal) record.getNumberValue("PR_T3"));

            payload.setOperatingMonth(DEFAULT_BEGIN_DATE);
            payload.setBeginDate(DEFAULT_BEGIN_DATE);

            //save payload
            payloadBean.save(payload);

            //update heatmeter type
            heatmeterBean.updateHeatmeterType(heatmeter.getId(), HeatmeterType.HEATING);

            processed++;

            listener.recordProcessed(importFile, index);
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
