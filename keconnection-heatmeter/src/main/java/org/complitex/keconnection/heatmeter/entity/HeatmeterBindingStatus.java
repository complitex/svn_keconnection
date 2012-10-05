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

    UNBOUND(1), ORGANIZATION_NOT_FOUND(2), BUILDING_NOT_FOUND(3), BINDING_ERROR(4), BOUND(5);
    private long id;

    private HeatmeterBindingStatus(long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
