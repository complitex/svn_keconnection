package org.complitex.keconnection.heatmeter.entity;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 19:25
 */
public class HeatmeterPeriod implements IHeatmeterEntity{
    private Long id;
    private Long parentId;
    private Long heatmeterId;
    private HeatmeterPeriodType type;
    private Date beginDate;
    private Date endDate;
    private Date operatingMonth;

    public HeatmeterPeriod() {
    }

    public HeatmeterPeriod(Date operatingMonth) {
        this.operatingMonth = operatingMonth;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getHeatmeterId() {
        return heatmeterId;
    }

    public void setHeatmeterId(Long heatmeterId) {
        this.heatmeterId = heatmeterId;
    }

    public HeatmeterPeriodType getType() {
        return type;
    }

    public void setType(HeatmeterPeriodType type) {
        this.type = type;
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

    public Date getOperatingMonth() {
        return operatingMonth;
    }

    public void setOperatingMonth(Date operatingMonth) {
        this.operatingMonth = operatingMonth;
    }
}
