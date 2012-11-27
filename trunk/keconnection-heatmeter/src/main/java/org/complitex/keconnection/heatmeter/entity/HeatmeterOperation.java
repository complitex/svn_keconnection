package org.complitex.keconnection.heatmeter.entity;

import java.util.Date;

import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.OPERATION;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.11.12 16:24
 */
public class HeatmeterOperation extends HeatmeterPeriod{
    public HeatmeterOperation() {
        super(OPERATION);
    }

    public HeatmeterOperation(Long heatmeterId, Date beginOm, HeatmeterPeriodSubType subType) {
        super(OPERATION);

        setHeatmeterId(heatmeterId);
        setBeginOm(beginOm);
        setSubType(subType);
    }
}
