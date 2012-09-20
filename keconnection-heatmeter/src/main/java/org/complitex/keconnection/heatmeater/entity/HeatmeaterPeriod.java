package org.complitex.keconnection.heatmeater.entity;

import org.complitex.dictionary.entity.ILongId;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 19:25
 */
public class HeatmeaterPeriod implements ILongId{
    private Long id;
    private Long parentId;
    private Long heatmeaterId;
    private HeatmeaterPeriodType type;
    private Date beginDate;
    private Date endDate;
    private Date operatingMonth;

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

    public Long getHeatmeaterId() {
        return heatmeaterId;
    }

    public void setHeatmeaterId(Long heatmeaterId) {
        this.heatmeaterId = heatmeaterId;
    }

    public HeatmeaterPeriodType getType() {
        return type;
    }

    public void setType(HeatmeaterPeriodType type) {
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
