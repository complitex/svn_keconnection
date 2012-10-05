/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.FilterWrapper;

/**
 *
 * @author Artem
 */
public class HeatmeterFilterWrapper extends FilterWrapper<Heatmeter> {

    private final HeatmeterBindingStatus unboundBindingStatus = HeatmeterBindingStatus.UNBOUND;

    public HeatmeterFilterWrapper() {
    }

    public HeatmeterFilterWrapper(Heatmeter object) {
        super(object);
    }

    public HeatmeterFilterWrapper(Heatmeter object, int first, int count) {
        super(object, first, count);
    }

    public HeatmeterBindingStatus getUnboundBindingStatus() {
        return unboundBindingStatus;
    }
}
