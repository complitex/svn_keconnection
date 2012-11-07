package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.ILongId;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 07.11.12 18:25
 */
public class HeatmeterDataset implements ILongId {
    private Long id;
    private Long heatmeterId;
    private Long periodId;
    private Date beginOm;
    private Date endOm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHeatmeterId() {
        return heatmeterId;
    }

    public void setHeatmeterId(Long heatmeterId) {
        this.heatmeterId = heatmeterId;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public Date getBeginOm() {
        return beginOm;
    }

    public void setBeginOm(Date beginOm) {
        this.beginOm = beginOm;
    }

    public Date getEndOm() {
        return endOm;
    }

    public void setEndOm(Date endOm) {
        this.endOm = endOm;
    }
}
