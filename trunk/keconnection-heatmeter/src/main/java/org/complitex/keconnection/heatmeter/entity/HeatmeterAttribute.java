package org.complitex.keconnection.heatmeter.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.11.12 14:23
 */
public abstract class HeatmeterAttribute implements IHeatmeterAttribute {
    private HeatmeterPeriod period;

    public HeatmeterPeriod getPeriod() {
        return period;
    }

    public void setPeriod(HeatmeterPeriod period) {
        this.period = period;
    }
}
