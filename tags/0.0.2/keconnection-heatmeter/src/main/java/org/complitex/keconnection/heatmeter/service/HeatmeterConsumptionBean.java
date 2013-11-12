package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConsumption;

import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 18:50
 */
@XmlMapper
@Stateless
public class HeatmeterConsumptionBean extends AbstractBean{
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

    public List<HeatmeterConsumption> getList(Long heatmeterInputId, Date om){
        return sqlSession().selectList("selectHeatmeterConsumptionsByOm", of("inputId", heatmeterInputId, "om", om));
    }
}
