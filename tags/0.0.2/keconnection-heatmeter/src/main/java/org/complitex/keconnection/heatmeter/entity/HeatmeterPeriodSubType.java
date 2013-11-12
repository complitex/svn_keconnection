package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.mybatis.FixedIdTypeHandler;
import org.complitex.dictionary.mybatis.IFixedIdType;
import org.complitex.keconnection.heatmeter.strategy.HeatmeterPeriodTypeStrategy;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 19:25
 */
@FixedIdTypeHandler
public enum HeatmeterPeriodSubType implements IFixedIdType{
    OPERATING(HeatmeterPeriodTypeStrategy.OPERATING), ADJUSTMENT(HeatmeterPeriodTypeStrategy.ADJUSTMENT);

    private Long id;

    private HeatmeterPeriodSubType(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
