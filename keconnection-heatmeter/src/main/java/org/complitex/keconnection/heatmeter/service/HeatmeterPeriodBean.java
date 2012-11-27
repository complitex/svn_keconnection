package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.util.IdListUtil;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriod;

import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.09.12 15:12
 */
@XmlMapper
public abstract class HeatmeterPeriodBean<T extends HeatmeterPeriod> extends AbstractBean {
    public abstract List<T> getList(Long heatmeterId, Date om);

    @Transactional
    public void save(T heatmeterPeriod) {
        if (heatmeterPeriod.getId() == null) {
            sqlSession().insert("insertHeatmeterPeriod", heatmeterPeriod);
        } else {
            sqlSession().update("updateHeatmeterPeriod", heatmeterPeriod);
        }
    }

    @Transactional
    public void delete(Long id) {
        sqlSession().delete("deleteHeatmeterPeriod", id);
    }

    @Transactional
    public void save(Long heatmeterId, Date om, List<T> list){
        if (heatmeterId != null) {
            List<T> db = getList(heatmeterId, om);

            for (T object : IdListUtil.getDiff(db, list)) {
                delete(object.getId());
            }
        }

        for (T object : list) {
            object.setHeatmeterId(heatmeterId);

            save(object);
        }
    }
}
