package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.mybatis.FixedIdTypeHandler;
import org.complitex.dictionary.mybatis.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 18:37
 */
@FixedIdTypeHandler
public enum HeatmeterConsumptionStatus implements IFixedIdType {
    NOT_LOADED(0L), LOADED(1L);

    private Long id;

    private HeatmeterConsumptionStatus(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
