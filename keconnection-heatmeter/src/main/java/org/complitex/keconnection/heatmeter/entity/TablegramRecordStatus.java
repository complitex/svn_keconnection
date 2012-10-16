package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.mybatis.FixedIdTypeHandler;
import org.complitex.dictionary.mybatis.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.10.12 16:11
 */
@FixedIdTypeHandler
public enum TablegramRecordStatus implements IFixedIdType{
    LOADED(1L), PROCESSED(2L), HEATMETER_NOT_FOUND(3L), ALREADY_HAS_PAYLOAD(4L);

    private Long id;

    private TablegramRecordStatus(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
