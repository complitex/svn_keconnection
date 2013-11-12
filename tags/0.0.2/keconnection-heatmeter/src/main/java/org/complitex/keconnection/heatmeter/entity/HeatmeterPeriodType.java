package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.mybatis.FixedIdTypeHandler;
import org.complitex.dictionary.mybatis.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 19:25
 */
@FixedIdTypeHandler
public enum HeatmeterPeriodType implements IFixedIdType{
    OPERATION(1L), CONNECTION(2L), PAYLOAD(3L), INPUT(4L);

    private Long id;

    private HeatmeterPeriodType(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
