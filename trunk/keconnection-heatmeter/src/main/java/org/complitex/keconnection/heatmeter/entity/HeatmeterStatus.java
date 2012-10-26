package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.mybatis.FixedIdTypeHandler;
import org.complitex.dictionary.mybatis.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.10.12 16:06
 */
@FixedIdTypeHandler
public enum HeatmeterStatus implements IFixedIdType {
    OFF(0L), OPERATION(1L), ADJUSTMENT(2L), REMOVED(3L);

    private Long id;

    private HeatmeterStatus(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
