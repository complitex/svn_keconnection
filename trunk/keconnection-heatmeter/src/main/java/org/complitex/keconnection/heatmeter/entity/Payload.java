package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.ILongId;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.09.12 18:27
 */
public class Payload implements ILongId {
    private Long id;
    private Long tablegramId;
    private Long parentId;
    private Long heatmeterId;
    private Date beginDate;
    private Date endDate;
    private Date operatingMonth;
    private Integer ls;
    private String name;
    private String address;
    private BigDecimal payload1;
    private BigDecimal payload2;
    private BigDecimal payload3;
    private PayloadStatus status;

    public Payload() {
    }

    public Payload(Long tablegramId) {
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

    public PayloadStatus getStatus() {
        return status;
    }

    public void setStatus(PayloadStatus status) {
        this.status = status;
    }
}
