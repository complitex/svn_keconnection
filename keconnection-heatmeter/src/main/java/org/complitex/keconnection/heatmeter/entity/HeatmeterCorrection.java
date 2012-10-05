/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.entity;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Artem
 */
public class HeatmeterCorrection implements Serializable {

    private Long id;
    private long systemHeatmeterId;
    private String externalHeatmeterId;
    private String heatmeterNumber;
    private Date bindingDate;
    private HeatmeterBindingStatus bindingStatus;
    private boolean history;

    public HeatmeterCorrection() {
    }

    public HeatmeterCorrection(long systemHeatmeterId, String externalHeatmeterId, String heatmeterNumber,
            HeatmeterBindingStatus bindingStatus) {
        this.systemHeatmeterId = systemHeatmeterId;
        this.externalHeatmeterId = externalHeatmeterId;
        this.heatmeterNumber = heatmeterNumber;
        this.bindingStatus = bindingStatus;
        this.history = false;
    }

    public HeatmeterCorrection(long systemHeatmeterId) {
        this.systemHeatmeterId = systemHeatmeterId;
        this.history = false;
    }

    public boolean isHistory() {
        return history;
    }

    public void setHistory(boolean history) {
        this.history = history;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getBindingDate() {
        return bindingDate;
    }

    public void setBindingDate(Date bindingDate) {
        this.bindingDate = bindingDate;
    }

    public HeatmeterBindingStatus getBindingStatus() {
        return bindingStatus;
    }

    public void setBindingStatus(HeatmeterBindingStatus bindingStatus) {
        this.bindingStatus = bindingStatus;
    }

    public String getExternalHeatmeterId() {
        return externalHeatmeterId;
    }

    public void setExternalHeatmeterId(String externalHeatmeterId) {
        this.externalHeatmeterId = externalHeatmeterId;
    }

    public String getHeatmeterNumber() {
        return heatmeterNumber;
    }

    public void setHeatmeterNumber(String heatmeterNumber) {
        this.heatmeterNumber = heatmeterNumber;
    }

    public long getSystemHeatmeterId() {
        return systemHeatmeterId;
    }

    public void setSystemHeatmeterId(long systemHeatmeterId) {
        this.systemHeatmeterId = systemHeatmeterId;
    }
}
