package org.complitex.keconnection.heatmeter.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.complitex.keconnection.heatmeter.entity.HeatmeterOperation;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriod;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.11.12 16:27
 */
public class HeatmeterOperationBean extends HeatmeterAttributeBean<HeatmeterOperation> {
    @EJB
    private HeatmeterPeriodBean periodBean;

    @Override
    public void save(HeatmeterOperation object) {
        periodBean.save(object.getPeriod());
    }

    @Override
    public List<HeatmeterOperation> getList(Long heatmeterId, Date om) {
        return Lists.transform(periodBean.getList(heatmeterId, om, HeatmeterPeriodType.OPERATION),
                new Function<HeatmeterPeriod, HeatmeterOperation>() {
                    @Override
                    public HeatmeterOperation apply(HeatmeterPeriod period) {
                        return new HeatmeterOperation(period);
                    }
                });
    }

    @Override
    public void delete(Long id) {
        //todo
    }

//    public void save(Long heatmeterId, Date om, List<HeatmeterPeriod> list){
//        if (heatmeterId != null) {
//            List<HeatmeterPeriod> db = getList(heatmeterId, om);
//
//            for (HeatmeterPeriod p : IdListUtil.getDiff(db, list)) {
//                delete(p.getId());
//            }
//        }
//
//        for (HeatmeterPeriod p : list) {
//            p.setHeatmeterId(heatmeterId);
//
//            save(p);
//        }
//    }
}
