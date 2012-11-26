package org.complitex.keconnection.heatmeter.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.11.12 16:24
 */
public class HeatmeterOperation extends HeatmeterAttribute{
    public HeatmeterOperation() {
    }

    public HeatmeterOperation(HeatmeterPeriod period) {
        setPeriod(period);
    }


    @Override
    public HeatmeterPeriodType getType() {
        return HeatmeterPeriodType.OPERATION;
    }

    @Override
    public Long getId() {
        HeatmeterPeriod period = getPeriod();

        return period != null ? period.getId() : null;
    }
}
