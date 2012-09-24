package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriod;

import javax.ejb.Stateless;
import java.util.HashMap;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.09.12 15:12
 */
@XmlMapper
@Stateless
public class HeatmeterPeriodBean extends AbstractBean{
    public void save(HeatmeterPeriod heatmeterPeriod){
        if (heatmeterPeriod.getId() == null){
            sqlSession().insert("insertHeatmeaterPeriod", heatmeterPeriod);
        }else {
            sqlSession().update("updateHeatmeaterPeriod", heatmeterPeriod);
        }
    }

    public void updateParent(final Long id, final Long parentId){
        sqlSession().update("updateHeatmeaterPeriodParent", new HashMap<String, Long>(){{
            put("id", id);
            put("parentId", parentId);
        }});
    }

    public HeatmeterPeriod getHeatmeaterPeriod(Long id){
        return sqlSession().selectOne("selectHeatmeaterPeriod", id);
    }
}
