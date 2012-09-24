package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 05.09.12 17:14
 */
@XmlMapper
@Stateless
public class HeatmeterBean extends AbstractBean{
    public void save(Heatmeter heatmeter){
        if (heatmeter.getId() == null){
            sqlSession().insert("insertHeatmeter", heatmeter);
        }else {
            sqlSession().update("updateHeatmeter", heatmeter);
        }
    }

    public Heatmeter getHeatmeter(Long id){
        return sqlSession().selectOne("selectHeatmeter", id);
    }

    public List<Heatmeter> getHeatmeters(FilterWrapper<Heatmeter> filterWrapper){
        return sqlSession().selectList("selectHeatmeters", filterWrapper);
    }

    public int getHeatmeterCount(FilterWrapper<Heatmeter> filterWrapper){
        return sqlSession().selectOne("selectHeatmetersCount", filterWrapper);
    }

    public void delete(Long id){
        sqlSession().delete("deleteHeatmeter", id);
    }

    public boolean isExist(Heatmeter heatmeter){
        return sqlSession().selectOne("isExistHeatmeter", heatmeter);
    }
}
