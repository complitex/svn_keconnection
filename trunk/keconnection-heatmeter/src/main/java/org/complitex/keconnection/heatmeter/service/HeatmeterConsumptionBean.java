package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConsumption;

import javax.ejb.Stateless;
import java.util.List;
import org.complitex.dictionary.mybatis.Transactional;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 18:50
 */
@XmlMapper
@Stateless
public class HeatmeterConsumptionBean extends AbstractBean{

    public HeatmeterConsumption get(Long id) {
        return null;
    }

    @Transactional
    public void save(HeatmeterConsumption consumption) {
        if (consumption.getId() == null){
            sqlSession().insert("insertHeatmeterConsumption", consumption);
        }else {
            sqlSession().update("updateHeatmeterConsumption", consumption);
        }
    }

    public void delete(Long id) {
        sqlSession().delete("deleteHeatmeterConsumption", id);
    }

    public List<HeatmeterConsumption> getList(Long heatmeterId) {
        return sqlSession().selectList("selectHeatmeterConsumptionsByHeatmeterId", heatmeterId);
    }

    public List<HeatmeterConsumption> getList(FilterWrapper<HeatmeterConsumption> filterWrapper) {
        return null;
    }

    public Integer getCount(FilterWrapper<HeatmeterConsumption> filterWrapper) {
        return null;
    }
}
