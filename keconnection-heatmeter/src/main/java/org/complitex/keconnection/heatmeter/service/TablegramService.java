package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.service.IProcessListener;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.Payload;
import org.complitex.keconnection.heatmeter.entity.Tablegram;
import org.complitex.keconnection.heatmeter.service.exception.HeatmeterNotFoundException;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

import static org.complitex.keconnection.heatmeter.entity.HeatmeterType.HEATING;
import static org.complitex.keconnection.heatmeter.entity.PayloadStatus.HEATMETER_NOT_FOUND;
import static org.complitex.keconnection.heatmeter.entity.PayloadStatus.LINKED;
import static org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy.KE_ORGANIZATION_OBJECT_ID;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.10.12 12:42
 */
@Stateless
public class TablegramService {
    @EJB
    private TablegramBean tablegramBean;

    @EJB
    private PayloadBean payloadBean;

    @EJB
    private HeatmeterBean heatmeterBean;

    @Asynchronous
    public void asyncLink(List<Tablegram> tablegrams, IProcessListener<Tablegram> listener){
        for (Tablegram tablegram : tablegrams){
            try {
                link(tablegram, listener);

                listener.processed(tablegram);
            } catch (Exception e) {
                listener.error(tablegram, e);
            }
        }

        listener.done();
    }

    public void link(Tablegram tablegram, IProcessListener<Tablegram> listener){
        List<Payload> payloads = payloadBean.getPayloads(tablegram.getId());

        for (Payload payload : payloads){
            Heatmeter heatmeter = heatmeterBean.getHeatmeterByLs(payload.getLs(), KE_ORGANIZATION_OBJECT_ID);

            if (heatmeter == null){
                payload.setStatus(HEATMETER_NOT_FOUND);

                listener.error(tablegram, new HeatmeterNotFoundException(payload.getLs()));
            }else {
                payload.setHeatmeterId(heatmeter.getId());

                payload.setStatus(LINKED);

                //update heatmeter type
                heatmeterBean.updateHeatmeterType(heatmeter.getId(), HEATING);
            }

            payloadBean.save(payload);
        }
    }
}
