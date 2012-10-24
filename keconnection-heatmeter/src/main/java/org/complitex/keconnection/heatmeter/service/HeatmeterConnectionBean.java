package org.complitex.keconnection.heatmeter.service;

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
    public void save(HeatmeterConnection heatmeterConnection){
        if (heatmeterConnection.getId() == null){
            sqlSession().insert("insertHeatmeterConnection", heatmeterConnection);
        }else {
            sqlSession().update("updateHeatmeterConnection", heatmeterConnection);
        }
    }

    public List<HeatmeterConnection> getHeatmeterConnections(Long heatmeterId){
        return sqlSession().selectList("selectHeatmeterConnectionsByHeatmeterId", heatmeterId);
    }

    public void delete(Long id){
        sqlSession().delete("deleteHeatmeterConnection", id);
    }
}
