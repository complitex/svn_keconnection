package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.ILongId;

import java.math.BigDecimal;
import java.util.Date;

import static org.complitex.keconnection.heatmeter.entity.HeatmeterConsumptionStatus.NOT_LOADED;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 18:31
 */
public class HeatmeterConsumption implements ILongId {
    private Long id;
    private Long heatmeterInputId;
    private Date operatingMonth;
    private BigDecimal consumption1;
    private BigDecimal consumption2;
    private BigDecimal consumption3;
    private Date beginDate;
    private Date endDate;
    private HeatmeterConsumptionStatus status = NOT_LOADED;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHeatmeterInputId() {
        return heatmeterInputId;
    }

    public void setHeatmeterInputId(Long heatmeterInputId) {
        this.heatmeterInputId = heatmeterInputId;
    }

    public Date getOperatingMonth() {
        return operatingMonth;
    }

    public void setOperatingMonth(Date operatingMonth) {
        this.operatingMonth = operatingMonth;
    }

    public BigDecimal getConsumption1() {
        return consumption1;
    }

    public void setConsumption1(BigDecimal consumption1) {
        this.consumption1 = consumption1;
    }

    public BigDecimal getConsumption2() {
        return consumption2;
    }

    public void setConsumption2(BigDecimal consumption2) {
        this.consumption2 = consumption2;
    }

    public BigDecimal getConsumption3() {
        return consumption3;
    }

    public void setConsumption3(BigDecimal consumption3) {
        this.consumption3 = consumption3;
    }

    public HeatmeterConsumptionStatus getStatus() {
        return status;
    }

    public void setStatus(HeatmeterConsumptionStatus status) {
        this.status = status;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
