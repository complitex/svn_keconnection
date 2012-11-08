package org.complitex.keconnection.heatmeter.entity;

import java.math.BigDecimal;

import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.PAYLOAD;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.09.12 18:27
 */
public class HeatmeterPayload extends HeatmeterAttribute{
    private Long id;
    private Long tablegramRecordId;
    private BigDecimal payload1;
    private BigDecimal payload2;
    private BigDecimal payload3;

    private Integer ls;

    public HeatmeterPayload() {
    }

    public HeatmeterPayload(Long heatmeterId, Long tablegramRecordId) {
        this.tablegramRecordId = tablegramRecordId;

        setPeriod(new HeatmeterPeriod(heatmeterId, PAYLOAD));
    }

    @Override
    public HeatmeterPeriodType getType() {
        return PAYLOAD;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTablegramRecordId() {
        return tablegramRecordId;
    }

    public void setTablegramRecordId(Long tablegramRecordId) {
        this.tablegramRecordId = tablegramRecordId;
    }

    public BigDecimal getPayload1() {
        return payload1;
    }

    public void setPayload1(BigDecimal payload1) {
        this.payload1 = payload1;
    }

    public BigDecimal getPayload2() {
        return payload2;
    }

    public void setPayload2(BigDecimal payload2) {
        this.payload2 = payload2;
    }

    public BigDecimal getPayload3() {
        return payload3;
    }

    public void setPayload3(BigDecimal payload3) {
        this.payload3 = payload3;
    }

    public Integer getLs() {
        return ls;
    }

    public void setLs(Integer ls) {
        this.ls = ls;
    }
}
