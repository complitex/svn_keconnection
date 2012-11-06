package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.mybatis.FixedIdTypeHandler;
import org.complitex.dictionary.mybatis.IFixedIdType;
import org.complitex.keconnection.heatmeter.strategy.HeatmeterPeriodTypeStrategy;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 19:25
 */
@FixedIdTypeHandler
public enum HeatmeterPeriodType implements IFixedIdType{
    OPERATION(HeatmeterPeriodTypeStrategy.OPERATION), ADJUSTMENT(HeatmeterPeriodTypeStrategy.ADJUSTMENT);

    private Long id;

    private HeatmeterPeriodType(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
