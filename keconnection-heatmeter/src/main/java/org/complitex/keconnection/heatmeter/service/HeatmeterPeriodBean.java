package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriod;

import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.09.12 15:12
 */
@XmlMapper
@Stateless
public class HeatmeterPeriodBean extends AbstractBean {

    public HeatmeterPeriod get(Long id) {
        return sqlSession().selectOne("selectHeatmeterPeriod", id);
    }

    @Transactional
    public void save(HeatmeterPeriod heatmeterPeriod) {
        if (heatmeterPeriod.getId() == null) {
            sqlSession().insert("insertHeatmeterPeriod", heatmeterPeriod);
        } else {
            sqlSession().update("updateHeatmeterPeriod", heatmeterPeriod);
        }
    }

    public void delete(Long id) {
        sqlSession().delete("deleteHeatmeterPeriod", id);
    }

    public List<HeatmeterPeriod> getList(long heatmeterId, Date om) {
        return sqlSession().selectList("selectHeatmeterPeriodsByOm", of("heatmeterId", heatmeterId, "om", om));
    }

    public List<HeatmeterPeriod> getList(FilterWrapper<HeatmeterPeriod> filterWrapper) {
        return null;
    }

    public Integer getCount(FilterWrapper<HeatmeterPeriod> filterWrapper) {
        return null;
    }
}
