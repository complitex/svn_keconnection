package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.mybatis.IFixedIdType;
import org.complitex.dictionary.mybatis.FixedIdTypeHandler;
import org.complitex.keconnection.heatmeter.strategy.HeatmeterTypeStrategy;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 16:46
 */
@FixedIdTypeHandler
public enum HeatmeterType implements IFixedIdType {
    HEATING(HeatmeterTypeStrategy.HEATING), HEATING_AND_WATER(HeatmeterTypeStrategy.HEATING_AND_WATER);

    private Long id;

    private HeatmeterType(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
