package org.complitex.keconnection.heatmeater.entity;

import org.complitex.dictionary.mybatis.IFixedIdType;
import org.complitex.dictionary.mybatis.FixedIdTypeHandler;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 16:46
 */
@FixedIdTypeHandler
public enum HeatmeterType implements IFixedIdType {
    HEATING(1L), HEATING_AND_WATER(2L);

    private Long id;

    private HeatmeterType(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
