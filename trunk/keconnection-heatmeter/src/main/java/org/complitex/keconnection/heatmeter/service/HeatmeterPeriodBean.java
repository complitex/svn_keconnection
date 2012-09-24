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
            sqlSession().insert("insertHeatmeterPeriod", heatmeterPeriod);
        }else {
            sqlSession().update("updateHeatmeterPeriod", heatmeterPeriod);
        }
    }

    public void updateParent(final Long id, final Long parentId){
        sqlSession().update("updateHeatmeterPeriodParent", new HashMap<String, Long>(){{
            put("id", id);
            put("parentId", parentId);
        }});
    }

    public HeatmeterPeriod getHeatmeterPeriod(Long id){
        return sqlSession().selectOne("selectHeatmeterPeriod", id);
    }
}
