package org.complitex.keconnection.heatmeter.entity;

import java.math.BigDecimal;
import java.util.Date;

import static org.complitex.keconnection.heatmeter.entity.HeatmeterConsumptionStatus.NOT_LOADED;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 18:31
 */
public class HeatmeterConsumption implements IHeatmeterAttribute {
    private Long id;
    private Long heatmeterId;
    private Long heatmeterInputId;
    private Date operatingMonth;
    private BigDecimal consumption;
    private BigDecimal consumption1;
    private BigDecimal consumption2;
    private BigDecimal consumption3;
    private Date beginDate;
    private Date endDate;
    private HeatmeterConsumptionStatus status = NOT_LOADED;

    private HeatmeterPeriod period;

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

    public BigDecimal getConsumption() {
        return consumption;
    }

    public void setConsumption(BigDecimal consumption) {
        this.consumption = consumption;
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

    public HeatmeterPeriod getPeriod() {
        return period;
    }

    public void setPeriod(HeatmeterPeriod period) {
        this.period = period;
    }
}
