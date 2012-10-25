package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConsumption;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 18:50
 */
@XmlMapper
@Stateless
public class HeatmeterConsumptionBean extends AbstractHeatmeterEntityBean<HeatmeterConsumption>{

    @Override
    public HeatmeterConsumption get(Long id) {
        return null;
    }

    @Override
    public void save(HeatmeterConsumption consumption) {
        if (consumption.getId() == null){
            sqlSession().insert("insertHeatmeterConsumption", consumption);
        }else {
            sqlSession().update("updateHeatmeterConsumption", consumption);
        }
    }

    @Override
    public void delete(Long id) {
        sqlSession().delete("deleteHeatmeterConsumption", id);
    }

    @Override
    public List<HeatmeterConsumption> getList(Long heatmeterId) {
        return sqlSession().selectList("selectHeatmeterConsumptionsByHeatmeterId", heatmeterId);
    }

    @Override
    public List<HeatmeterConsumption> getList(FilterWrapper<HeatmeterConsumption> filterWrapper) {
        return null;
    }

    @Override
    public Integer getCount(FilterWrapper<HeatmeterConsumption> filterWrapper) {
        return null;
    }
}
