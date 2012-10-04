package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.HeatmeterCode;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.10.12 16:37
 */
@XmlMapper
@Stateless
public class HeatmeterCodeBean extends AbstractBean{
    public void save(HeatmeterCode heatmeterCode){
        if (heatmeterCode.getId() == null){
            sqlSession().insert("insertHeatmeterCode", heatmeterCode);
        }else {
            sqlSession().update("updateHeatmeterCode", heatmeterCode);
        }
    }

    public List<HeatmeterCode> getHeatmeterCodes(Long heatmeterId){
        return sqlSession().selectList("selectHeatmeterCodesByHeatmeterId", heatmeterId);
    }

    public void delete(Long id){
        sqlSession().delete("deleteHeatmeterCode", id);
    }
}
