package org.complitex.keconnection.heatmeter.service;

import java.util.Date;
import java.util.List;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;

import javax.ejb.Stateless;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.keconnection.heatmeter.entity.HeatmeterOperation;

import static com.google.common.collect.ImmutableMap.of;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.11.12 16:27
 */
@Stateless
@XmlMapper
public class HeatmeterOperationBean extends HeatmeterPeriodBean<HeatmeterOperation> {

    @Override
    public HeatmeterPeriodType getType() {
        return HeatmeterPeriodType.OPERATION;
    }

    public List<HeatmeterOperation> getHeatmeterOperations(Long heatmeterId, Date om) {
        return sqlSession().selectList("selectHeatmeterOperationsByOm", of("heatmeterId", heatmeterId, "om", om));
    }
}
