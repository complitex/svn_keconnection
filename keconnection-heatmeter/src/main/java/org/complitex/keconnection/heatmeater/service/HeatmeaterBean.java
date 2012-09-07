package org.complitex.keconnection.heatmeater.service;

import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeater.entity.Heatmeater;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 05.09.12 17:14
 */
@XmlMapper
@Stateless
public class HeatmeaterBean extends AbstractBean{
    public void save(Heatmeater heatmeater){
        if (heatmeater.getId() == null){
            sqlSession().insert("insertHeatmeater", heatmeater);
        }else {
            sqlSession().update("updateHeatmeater", heatmeater);
        }
    }

    public Heatmeater getHeatmeater(Long id){
        return sqlSession().selectOne("selectHeatmeater", id);
    }

    public List<Heatmeater> getHeatmeaters(FilterWrapper<Heatmeater> filterWrapper){
        return sqlSession().selectList("selectHeatmeaters", filterWrapper);
    }

    public int getHeatmeaterCount(FilterWrapper<Heatmeater> filterWrapper){
        return sqlSession().selectOne("selectHeatmeatersCount", filterWrapper);
    }
}
