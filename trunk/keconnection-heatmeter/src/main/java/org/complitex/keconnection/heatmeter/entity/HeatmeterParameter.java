package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.ILongId;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.11.12 12:25
 */
public abstract class HeatmeterParameter implements ILongId {
    private HeatmeterPeriod period;
    private HeatmeterDataset dataset;

    public HeatmeterPeriod getPeriod() {
        return period;
    }

    public void setPeriod(HeatmeterPeriod period) {
        this.period = period;
    }

    public HeatmeterDataset getDataset() {
        return dataset;
    }

    public void setDataset(HeatmeterDataset dataset) {
        this.dataset = dataset;
    }
}
