package org.complitex.keconnection.heatmeater.service;

import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeater.entity.HeatmeaterPeriod;

import javax.ejb.Stateless;
import java.util.HashMap;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.09.12 15:12
 */
@XmlMapper
@Stateless
public class HeatmeaterPeriodBean extends AbstractBean{
    public void save(HeatmeaterPeriod heatmeaterPeriod){
        if (heatmeaterPeriod.getId() == null){
            sqlSession().insert("insertHeatmeaterPeriod", heatmeaterPeriod);
        }else {
            sqlSession().update("updateHeatmeaterPeriod", heatmeaterPeriod);
        }
    }

    public void updateParent(final Long id, final Long parentId){
        sqlSession().update("updateHeatmeaterPeriodParent", new HashMap<String, Long>(){{
            put("id", id);
            put("parentId", parentId);
        }});
    }

    public HeatmeaterPeriod getHeatmeaterPeriod(Long id){
        return sqlSession().selectOne("selectHeatmeaterPeriod", id);
    }
}
