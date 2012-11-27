package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConnection;

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
public class HeatmeterConnectionBean extends HeatmeterPeriodBean<HeatmeterConnection> {
    public List<HeatmeterConnection> getActualList(Long heatmeterId) {
        return sqlSession().selectList("selectActualHeatmeterConnections", heatmeterId);
    }

    public List<HeatmeterConnection> getList(Long heatmeterId, Date om){
        return sqlSession().selectList("selectHeatmeterConnectionsByOm", of("heatmeterId", heatmeterId, "om", om));
    }
}
