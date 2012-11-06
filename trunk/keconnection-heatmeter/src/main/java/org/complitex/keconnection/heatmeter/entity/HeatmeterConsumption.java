package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.IDateRange;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 18:31
 */
public class HeatmeterConsumption implements IHeatmeterEntity, IDateRange {
    private Long id;
    private Long heatmeterId;
    private Date readoutDate;
    private Date operatingMonth;
    private BigDecimal consumption;
    private BigDecimal consumption1 = new BigDecimal(0);
    private BigDecimal consumption2 = new BigDecimal(0);
    private BigDecimal consumption3 = new BigDecimal(0);
    private HeatmeterConsumptionStatus status = HeatmeterConsumptionStatus.NOT_LOADED;

    private Date beginDate;
    private Date endDate;

    public HeatmeterConsumption() {
    }

    public HeatmeterConsumption(Date operatingMonth) {
        this.operatingMonth = operatingMonth;
    }

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

    public Date getReadoutDate() {
        return readoutDate;
    }

    public void setReadoutDate(Date readoutDate) {
        this.readoutDate = readoutDate;
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
}
