/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.service.exception;

import org.complitex.keconnection.heatmeter.entity.HeatmeterBindingStatus;

/**
 *
 * @author Artem
 */
public class HeatmeterBindException extends Exception {

    private final HeatmeterBindingStatus status;

    public HeatmeterBindException(HeatmeterBindingStatus status) {
        this.status = status;
    }

    public HeatmeterBindingStatus getStatus() {
        return status;
    }
}
