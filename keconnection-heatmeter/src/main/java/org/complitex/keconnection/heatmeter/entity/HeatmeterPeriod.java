package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.ILongId;

import java.util.Date;

import static org.complitex.dictionary.util.DateUtil.newDate;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 19:25
 */
public class HeatmeterPeriod implements ILongId{
    public final static Date DEFAULT_BEGIN_DATE = newDate(1, 10, 2012);
    public final static Date DEFAULT_END_DATE = newDate(31, 12, 2054);

    public final static Date DEFAULT_BEGIN_OM = newDate(1, 10, 2012);
    public final static Date DEFAULT_END_OM = newDate(1, 12, 2054);

    private Long id;
    private Long heatmeterId;
    private Long attributeId;
    private HeatmeterPeriodType type;
    private HeatmeterPeriodSubType subType;
    private Date beginDate = DEFAULT_BEGIN_DATE;
    private Date endDate = DEFAULT_END_DATE;
    private Date beginOm = DEFAULT_BEGIN_OM;
    private Date endOm = DEFAULT_END_OM;

    public HeatmeterPeriod() {
    }

    public HeatmeterPeriod(Long heatmeterId, HeatmeterPeriodType type) {
        this.heatmeterId = heatmeterId;
        this.type = type;
    }

    public HeatmeterPeriod(Long heatmeterId, HeatmeterPeriodType type, HeatmeterPeriodSubType subType) {
        this.heatmeterId = heatmeterId;
        this.type = type;
        this.subType = subType;
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

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
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
