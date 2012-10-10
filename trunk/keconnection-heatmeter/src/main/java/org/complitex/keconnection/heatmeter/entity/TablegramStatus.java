package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.mybatis.FixedIdTypeHandler;
import org.complitex.dictionary.mybatis.IFixedIdType;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.10.12 16:11
 */
@FixedIdTypeHandler
public enum TablegramStatus implements IFixedIdType{
    LOADED(1L), LINKED(2L), PROCESSED(3L);

    private Long id;

    private TablegramStatus(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
