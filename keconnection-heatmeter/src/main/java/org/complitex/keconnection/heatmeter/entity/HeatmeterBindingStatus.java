/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.mybatis.FixedIdTypeHandler;
import org.complitex.dictionary.mybatis.IFixedIdType;

/**
 *
 * @author Artem
 */
@FixedIdTypeHandler
public enum HeatmeterBindingStatus implements IFixedIdType {

    UNBOUND(1), ORGANIZATION_NOT_FOUND(2), BUILDING_NOT_FOUND(3), BINDING_ERROR(4), MORE_ONE_EXTERNAL_HEATMETER(5),
    NO_EXTERNAL_HEATMETERS(6), BOUND(7);
    private final long id;

    private HeatmeterBindingStatus(long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
