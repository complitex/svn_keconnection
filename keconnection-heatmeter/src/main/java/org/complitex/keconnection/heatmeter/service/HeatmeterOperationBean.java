package org.complitex.keconnection.heatmeter.service;

import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;

import javax.ejb.Stateless;
import org.complitex.dictionary.mybatis.XmlMapper;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.11.12 16:27
 */
@Stateless
@XmlMapper
public class HeatmeterOperationBean extends HeatmeterPeriodBean {
    @Override
    public HeatmeterPeriodType getType() {
        return HeatmeterPeriodType.OPERATION;
    }
}
