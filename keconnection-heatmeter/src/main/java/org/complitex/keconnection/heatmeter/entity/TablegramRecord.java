package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.ILongId;

import java.math.BigDecimal;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.10.12 16:23
 */
public class TablegramRecord implements ILongId{
    private Long id;
    private Long tablegramId;
    private Long heatmeterId;
    private Integer ls;
    private String name;
    private String address;
    private BigDecimal payload1;
    private BigDecimal payload2;
    private BigDecimal payload3;
    private TablegramRecordStatus status;

    public TablegramRecord() {
    }

    public TablegramRecord(Long tablegramId) {
        this.tablegramId = tablegramId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTablegramId() {
        return tablegramId;
    }

    public void setTablegramId(Long tablegramId) {
        this.tablegramId = tablegramId;
    }

    public Long getHeatmeterId() {
        return heatmeterId;
    }

    public void setHeatmeterId(Long heatmeterId) {
        this.heatmeterId = heatmeterId;
    }

    public Integer getLs() {
        return ls;
    }

    public void setLs(Integer ls) {
        this.ls = ls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public TablegramRecordStatus getStatus() {
        return status;
    }

    public void setStatus(TablegramRecordStatus status) {
        this.status = status;
    }
}
