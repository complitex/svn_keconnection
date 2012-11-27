package org.complitex.keconnection.heatmeter.service;

import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;

import javax.ejb.Stateless;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.11.12 16:27
 */
@Stateless
public class HeatmeterOperationBean extends HeatmeterPeriodBean {
    @Override
    public HeatmeterPeriodType getType() {
        return HeatmeterPeriodType.OPERATION;
    }
}
