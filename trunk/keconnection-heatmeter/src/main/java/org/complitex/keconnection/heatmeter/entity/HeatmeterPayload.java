package org.complitex.keconnection.heatmeter.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.PAYLOAD;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.09.12 18:27
 */
public class HeatmeterPayload extends HeatmeterPeriod{
    private Long tablegramRecordId;
    private BigDecimal payload1;
    private BigDecimal payload2;
    private BigDecimal payload3;
    private Integer ls;

    public HeatmeterPayload() {
        super(PAYLOAD);
    }

    public HeatmeterPayload(Long heatmeterId, Date beginOm) {
        super(PAYLOAD);

        setHeatmeterId(heatmeterId);
        setBeginOm(beginOm);
    }

    @Override
    public boolean isSameValue(HeatmeterPeriod p) {
        if (p instanceof HeatmeterPayload){
            HeatmeterPayload pp = (HeatmeterPayload) p;

            return super.isSameValue(p) && Objects.equals(payload1, pp.payload1)
                    && Objects.equals(payload2, pp.payload2) && Objects.equals(payload3, pp.payload3);
        }

        return super.isSameValue(p);
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
