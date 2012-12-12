package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.ILongId;

import java.util.Date;

import static org.complitex.dictionary.util.DateUtil.newDate;
import static org.complitex.keconnection.heatmeter.util.HeatmeterPeriodUtil.range;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.09.12 19:25
 */
public abstract class HeatmeterPeriod implements ILongId{
    public final static Date DEFAULT_BEGIN_DATE = newDate(1, 10, 2012);
    public final static Date DEFAULT_END_DATE = newDate(31, 12, 2054);

    public final static Date DEFAULT_BEGIN_OM = newDate(1, 10, 2012);
    public final static Date DEFAULT_END_OM = newDate(1, 12, 2054);

    private Long id;
    private Long heatmeterId;
    private Long objectId;
    private HeatmeterPeriodType type;
    private HeatmeterPeriodSubType subType;
    private Date beginDate = DEFAULT_BEGIN_DATE;
    private Date endDate = DEFAULT_END_DATE;
    private Date beginOm = DEFAULT_BEGIN_OM;
    private Date endOm = DEFAULT_END_OM;

    private Date updated;

    public HeatmeterPeriod(HeatmeterPeriodType type) {
        this.type = type;
    }

    public boolean isConnected(HeatmeterPeriod p){
        return range(this).isConnected(range(p));
    }

    public boolean isEncloses(HeatmeterPeriod p){
        return range(this).encloses(range(p));
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

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
