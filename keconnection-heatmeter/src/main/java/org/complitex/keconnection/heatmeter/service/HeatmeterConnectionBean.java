package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConnection;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.10.12 16:37
 */
@XmlMapper
@Stateless
public class HeatmeterConnectionBean extends HeatmeterAttributeBean<HeatmeterConnection>{
    @EJB
    private HeatmeterPeriodBean heatmeterPeriodBean;

    public HeatmeterConnection get(Long id) {
        return null;
    }

    public void save(HeatmeterConnection heatmeterConnection){
        if (heatmeterConnection.getId() == null){
            sqlSession().insert("insertHeatmeterConnection", heatmeterConnection);
        }else {
            sqlSession().update("updateHeatmeterConnection", heatmeterConnection);
        }

        heatmeterConnection.getPeriod().setAttributeId(heatmeterConnection.getId());
        heatmeterPeriodBean.save(heatmeterConnection.getPeriod());
    }

    public void delete(Long id){
        sqlSession().delete("deleteHeatmeterConnection", id);
    }

    public List<HeatmeterConnection> getActualList(Long heatmeterId) {
        return sqlSession().selectList("selectActualHeatmeterConnections", heatmeterId);
    }

    public List<HeatmeterConnection> getList(Long heatmeterId, Date om){
        return sqlSession().selectList("selectHeatmeterConnectionsByOm", of("heatmeterId", heatmeterId, "om", om));
    }

    public List<HeatmeterConnection> getList(FilterWrapper<HeatmeterConnection> filterWrapper) {
        return null;
    }

    public Integer getCount(FilterWrapper<HeatmeterConnection> filterWrapper) {
        return null;
    }
}
