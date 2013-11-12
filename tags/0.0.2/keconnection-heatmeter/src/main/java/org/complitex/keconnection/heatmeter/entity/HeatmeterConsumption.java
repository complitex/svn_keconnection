package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.ILongId;

import java.math.BigDecimal;
import java.util.Date;

import static org.complitex.dictionary.util.DateUtil.getDaysDiff;
import static org.complitex.dictionary.util.DateUtil.getMax;
import static org.complitex.dictionary.util.DateUtil.getMin;
import static org.complitex.keconnection.heatmeter.entity.HeatmeterConsumptionStatus.NOT_LOADED;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 18:31
 */
public class HeatmeterConsumption implements ILongId {

    private Long id;
    private Long heatmeterInputId;
    private Date om;
    private BigDecimal consumption1 = BigDecimal.ZERO;
    private BigDecimal consumption2 = BigDecimal.ZERO;
    private BigDecimal consumption3 = BigDecimal.ZERO;
    private Date beginDate;
    private Date endDate;
    private HeatmeterConsumptionStatus status = NOT_LOADED;

    public HeatmeterConsumption() {
    }

    public HeatmeterConsumption(Date om, HeatmeterPayload payload, HeatmeterInput input) {
        this.om = om;

        int days = getDaysDiff(input.getBeginDate(), input.getEndDate()) + 1;

        beginDate = getMax(input.getBeginDate(), payload.getBeginDate());
        endDate = getMin(input.getEndDate(), payload.getEndDate());

        BigDecimal d = BigDecimal.valueOf((double)(getDaysDiff(beginDate, endDate) + 1)/(days*100));

        consumption1 = payload.getPayload1().multiply(input.getValue()).multiply(d);
        consumption2 = payload.getPayload2().multiply(input.getValue()).multiply(d);
        consumption3 = payload.getPayload3().multiply(input.getValue()).multiply(d);
    }

    public boolean isPeriodEquals(HeatmeterConsumption c){
        return beginDate.equals(c.beginDate) && endDate.equals(c.endDate) && om.equals(c.om);
    }

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

    public Date getOm() {
        return om;
    }

    public void setOm(Date om) {
        this.om = om;
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
