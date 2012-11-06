package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriod;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import org.complitex.dictionary.mybatis.Transactional;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.09.12 15:12
 */
@XmlMapper
@Stateless
public class HeatmeterPeriodBean extends AbstractHeatmeterEntityBean<HeatmeterPeriod>{
    @Override
    public HeatmeterPeriod get(Long id) {
        return sqlSession().selectOne("selectHeatmeterPeriod", id);
    }

    @Transactional
    public void save(HeatmeterPeriod heatmeterPeriod){
        if (heatmeterPeriod.getId() == null){
            sqlSession().insert("insertHeatmeterPeriod", heatmeterPeriod);
        }else {
            sqlSession().update("updateHeatmeterPeriod", heatmeterPeriod);
        }
    }

    @Override
    public void delete(Long id) {
        sqlSession().delete("deleteHeatmeterPeriod", id);
    }

    public void updateParent(final Long id, final Long parentId){
        sqlSession().update("updateHeatmeterPeriodParent", new HashMap<String, Long>(){{
            put("id", id);
            put("parentId", parentId);
        }});
    }

    @Override
    public List<HeatmeterPeriod> getList(Long heatmeterId) {
        return sqlSession().selectList("selectHeatmeterPeriodsByHeatmeterId", heatmeterId);
    }

    @Override
    public List<HeatmeterPeriod> getList(FilterWrapper<HeatmeterPeriod> filterWrapper) {
        return null;
    }

    @Override
    public Integer getCount(FilterWrapper<HeatmeterPeriod> filterWrapper) {
        return null;
    }
}
