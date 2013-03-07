package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriod;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;

import java.util.Date;
import java.util.List;

import static org.complitex.dictionary.util.DateUtil.isSameMonth;
import static org.complitex.dictionary.util.DateUtil.previousMonth;
import static org.complitex.dictionary.util.IdListUtil.getDiff;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.09.12 15:12
 */
@XmlMapper
public abstract class HeatmeterPeriodBean<T extends HeatmeterPeriod> extends AbstractBean {
    public abstract HeatmeterPeriodType getType();

    public abstract List<T> getList(Long heatmeterId, Date om);

    @Transactional
    public void save(T object) {
        if (object.getId() == null) {
            sqlSession().insert("insertHeatmeterPeriod", object);
            insertAdditionalInfo(object);
        } else {
            sqlSession().update("updateHeatmeterPeriod", object);
            updateAdditionalInfo(object);
        }
    }

    @Transactional
    public void insertAdditionalInfo(T info) {
    }

    @Transactional
    public void updateAdditionalInfo(T info) {
    }

    @Transactional
    public void delete(Long id) {
        sqlSession().delete("deleteHeatmeterPeriod", id);
    }

    @Transactional
    public void save(Long heatmeterId, Date om, List<T> list) {
        List<T> db = getList(heatmeterId, om);

        //remove or fix end om
        for (T o : getDiff(db, list)) {
            if (isSameMonth(om, o.getBeginOm())) {
                delete(o.getId());
            }else {
                o.setEndOm(previousMonth(om));
                list.add(o);
            }
        }

        //save
        for (T o : list) {
            o.setHeatmeterId(heatmeterId);

            //changed
            for (T d : db){
                if (d.getId().equals(o.getId()) && !isSameMonth(om, d.getBeginOm()) && !o.isSame(d)){
                    d.setEndOm(previousMonth(om));
                    save(d);

                    o.setId(null);
                    o.setBeginOm(om);
                }
            }

            save(o);
        }
    }
}
