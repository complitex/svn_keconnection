package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.keconnection.heatmeter.entity.HeatmeterOperation;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;

import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

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

    public List<HeatmeterOperation> getList(Long heatmeterId, Date om) {
        return sqlSession().selectList("selectHeatmeterOperationsByOm", of("heatmeterId", heatmeterId, "om", om));
    }
}
