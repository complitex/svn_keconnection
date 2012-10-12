package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.mybatis.FixedIdTypeHandler;
import org.complitex.dictionary.mybatis.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.10.12 13:17
 */
@FixedIdTypeHandler
public enum  PayloadStatus implements IFixedIdType {
    NOT_LINKED(1L), HEATMETER_NOT_FOUND(2L), LINKED(3L);

    private Long id;

    private PayloadStatus(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
