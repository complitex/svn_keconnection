package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.ILongId;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 19:25
 */
public class HeatmeterPeriod implements ILongId{
    private Long id;
    private HeatmeterPeriodType type;
    private HeatmeterPeriodSubType subType;
    private Date beginDate;
    private Date endDate;
    private Long attributeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HeatmeterPeriodType getType() {
        return type;
    }

    public void setType(HeatmeterPeriodType type) {
        this.type = type;
    }

    public HeatmeterPeriodSubType getSubType() {
        return subType;
    }

    public void setSubType(HeatmeterPeriodSubType subType) {
        this.subType = subType;
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

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
    }
}
