package org.complitex.keconnection.heatmeter.entity;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.10.12 15:32
 */
public class HeatmeterConnection implements IHeatmeterEntity{
    private Long id;
    private Long heatmeterId;
    private Long buildingCodeId;
    private Date beginDate;
    private Date endDate;
    private Date operatingMonth;

    private Long buildingId;
    private Long organizationId;
    private Integer code;
    private String organizationCode;

    public HeatmeterConnection() {
    }

    public HeatmeterConnection(Date operatingMonth) {
        this.operatingMonth = operatingMonth;
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

    public Long getBuildingCodeId() {
        return buildingCodeId;
    }

    public void setBuildingCodeId(Long buildingCodeId) {
        this.buildingCodeId = buildingCodeId;
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

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }
}
