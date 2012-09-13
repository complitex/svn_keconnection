/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.address.strategy.building.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Artem
 */
public class BuildingOrganizationAssociation implements Serializable {

    private Long id;
    private Long organizationId;
    private String buildingCode;
    private Long buildingId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BuildingOrganizationAssociation other = (BuildingOrganizationAssociation) obj;
        if (!Objects.equals(this.organizationId, other.organizationId)) {
            return false;
        }
        if (!Objects.equals(this.buildingCode, other.buildingCode)) {
            return false;
        }
        if (!Objects.equals(this.buildingId, other.buildingId)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + Objects.hashCode(this.organizationId);
        hash = 31 * hash + Objects.hashCode(this.buildingCode);
        hash = 31 * hash + Objects.hashCode(this.buildingId);
        return hash;
    }
}
