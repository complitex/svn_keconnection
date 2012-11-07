package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConnection;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.10.12 16:37
 */
@XmlMapper
@Stateless
public class HeatmeterConnectionBean extends AbstractBean{
    public HeatmeterConnection get(Long id) {
        return null;
    }

    public void save(HeatmeterConnection heatmeterConnection){
        if (heatmeterConnection.getId() == null){
            sqlSession().insert("insertHeatmeterConnection", heatmeterConnection);
        }else {
            sqlSession().update("updateHeatmeterConnection", heatmeterConnection);
        }
    }

    public void delete(Long id){
        sqlSession().delete("deleteHeatmeterConnection", id);
    }

    public List<HeatmeterConnection> getList(Long heatmeterId) {
        return sqlSession().selectList("selectHeatmeterConnectionsByHeatmeterId", heatmeterId);
    }

    public List<HeatmeterConnection> getList(FilterWrapper<HeatmeterConnection> filterWrapper) {
        return null;
    }

    public Integer getCount(FilterWrapper<HeatmeterConnection> filterWrapper) {
        return null;
    }
}
