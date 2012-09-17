package org.complitex.keconnection.heatmeater.entity;

import org.complitex.dictionary.mybatis.FixedIdTypeHandler;
import org.complitex.dictionary.mybatis.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 19:25
 */
@FixedIdTypeHandler
public enum HeatmeaterPeriodType implements IFixedIdType{
    OPERATION(100L), ADJUSTMENT(200L);

    private Long id;

    private HeatmeaterPeriodType(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
