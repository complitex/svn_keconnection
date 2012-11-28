package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.util.IdListUtil;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriod;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;

import java.util.Date;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.09.12 15:12
 */
@XmlMapper
public abstract class HeatmeterPeriodBean<T extends HeatmeterPeriod> extends AbstractBean {
    public abstract HeatmeterPeriodType getType();

    public List<Long> getIdList(Long heatmeterId, Date om){
        return sqlSession().selectList("selectHeatmeterPeriodIds",
                of("type", getType(), "heatmeterId", heatmeterId, "om", om));
    }

    @Transactional
    public void save(HeatmeterPeriod heatmeterPeriod) {
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
        List<Long> db = getIdList(heatmeterId, om);
        List<Long> remove = IdListUtil.getIdDiff(db, list);

        for (Long id : remove) {
            delete(id);
        }

        for (HeatmeterPeriod object : list) {
            object.setHeatmeterId(heatmeterId);

            save(object);
        }
    }
}
