package org.complitex.keconnection.heatmeater.entity;

import org.complitex.dictionary.mybatis.FixedIdTypeHandler;
import org.complitex.dictionary.mybatis.IFixedIdType;
import org.complitex.keconnection.heatmeater.strategy.HeatmeterPeriodTypeStrategy;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 19:25
 */
@FixedIdTypeHandler
public enum HeatmeaterPeriodType implements IFixedIdType{
    OPERATION(HeatmeterPeriodTypeStrategy.OPERATION), ADJUSTMENT(HeatmeterPeriodTypeStrategy.ADJUSTMENT);

    private Long id;

    private HeatmeaterPeriodType(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
