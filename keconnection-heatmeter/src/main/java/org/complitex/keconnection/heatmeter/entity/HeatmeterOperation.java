package org.complitex.keconnection.heatmeter.entity;

import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.OPERATION;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.11.12 16:24
 */
public class HeatmeterOperation extends HeatmeterPeriod{
    public HeatmeterOperation() {
        super(OPERATION);
    }
}
