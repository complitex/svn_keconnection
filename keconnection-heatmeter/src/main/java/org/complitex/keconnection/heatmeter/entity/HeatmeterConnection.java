package org.complitex.keconnection.heatmeter.entity;

import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.CONNECTION;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.10.12 15:32
 */
public class HeatmeterConnection extends HeatmeterAttribute {
    private Long id;
    private Long buildingCodeId;
    private Long buildingId;
    private Long organizationId;
    private Integer code;
    private String organizationCode;

    public HeatmeterConnection() {
    }

    public HeatmeterConnection(Long heatmeterId, Long buildingCodeId) {
        this.buildingCodeId = buildingCodeId;

        setPeriod(new HeatmeterPeriod(heatmeterId, CONNECTION));
    }

    @Override
    public HeatmeterPeriodType getType() {
        return CONNECTION;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBuildingCodeId() {
        return buildingCodeId;
    }

    public void setBuildingCodeId(Long buildingCodeId) {
        this.buildingCodeId = buildingCodeId;
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
