package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.service.IProcessListener;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.keconnection.heatmeter.entity.*;
import org.complitex.keconnection.heatmeter.service.exception.HeatmeterNotFoundException;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

import static org.complitex.keconnection.heatmeter.entity.HeatmeterType.HEATING;
import static org.complitex.keconnection.heatmeter.entity.TablegramRecordStatus.HEATMETER_NOT_FOUND;
import static org.complitex.keconnection.heatmeter.entity.TablegramRecordStatus.PROCESSED;
import static org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy.KE_ORGANIZATION_OBJECT_ID;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.10.12 12:42
 */
@Stateless
public class TablegramService {
    private final Date DEFAULT_BEGIN_DATE = DateUtil.newDate(1, 10, 2012);

    @EJB
    private TablegramBean tablegramBean;

    @EJB
    private TablegramRecordBean tablegramRecordBean;

    @EJB
    private HeatmeterBean heatmeterBean;

    @EJB
    private PayloadBean payloadBean;

    @Asynchronous
    public void asyncLink(List<Tablegram> tablegrams, IProcessListener<Tablegram> listener){
        for (Tablegram tablegram : tablegrams){
            try {
                process(tablegram, listener);

                listener.processed(tablegram);
            } catch (Exception e) {
                listener.error(tablegram, e);
            }
        }

        listener.done();
    }

    public void process(Tablegram tablegram, IProcessListener<Tablegram> listener){
        List<TablegramRecord> tablegramRecords = tablegramRecordBean.getTablegramRecords(tablegram.getId());

        for (TablegramRecord tablegramRecord : tablegramRecords){
            //skip processed
            if (PROCESSED.equals(tablegramRecord.getStatus())){
                continue;
            }

            Heatmeter heatmeter = heatmeterBean.getHeatmeterByLs(tablegramRecord.getLs(), KE_ORGANIZATION_OBJECT_ID);

            if (heatmeter == null){
                tablegramRecord.setStatus(HEATMETER_NOT_FOUND);

                listener.error(tablegram, new HeatmeterNotFoundException(tablegramRecord.getLs()));
            }else {
                //create payload
                Payload payload = new Payload();

                payload.setTablegramRecordId(tablegramRecord.getId());

                payload.setBeginDate(DEFAULT_BEGIN_DATE);
                payload.setOperatingMonth(DEFAULT_BEGIN_DATE);

                payload.setHeatmeterId(heatmeter.getId());
                payload.setPayload1(tablegramRecord.getPayload1());
                payload.setPayload2(tablegramRecord.getPayload2());
                payload.setPayload3(tablegramRecord.getPayload3());

                payloadBean.save(payload);

                //update table record
                tablegramRecord.setHeatmeterId(heatmeter.getId());
                tablegramRecord.setStatus(PROCESSED);

                //update heatmeter type
                heatmeterBean.updateHeatmeterType(heatmeter.getId(), HEATING);
            }

            tablegramRecordBean.save(tablegramRecord);
        }
    }
}
