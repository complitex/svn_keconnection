package org.complitex.keconnection.heatmeater.entity;

import org.complitex.dictionary.mybatis.IFixedIdType;
import org.complitex.dictionary.mybatis.FixedIdTypeHandler;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 16:46
 */
@FixedIdTypeHandler
public enum HeatmeterType implements IFixedIdType {
    HEATING(100L), HEATING_AND_WATER(200L);

    private Long id;

    private HeatmeterType(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
