/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.address.entity;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Artem
 */
public class BuildingImport implements Serializable {

    private Long id;
    private Long distrId;
    private Long streetId;
    private String num;
    private String part;
    private boolean processed;
    private List<BuildingPartImport> buildingParts;

    public BuildingImport() {
    }

    public BuildingImport(long distrId, long streetId, String num, String part) {
        this.distrId = distrId;
        this.streetId = streetId;
        this.num = num;
        this.part = part;
    }

    public long getBuildingPartId() {
        if (buildingParts == null || buildingParts.isEmpty()) {
            throw new IllegalStateException("Building has no parts. Building: distrId = " + distrId
                    + ", streetId = " + streetId + ", num = " + num + ", part = " + part);
        }
        return buildingParts.get(0).getId();
    }

    public Long getDistrId() {
        return distrId;
    }

    public void setDistrId(Long distrId) {
        this.distrId = distrId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Long getStreetId() {
        return streetId;
    }

    public void setStreetId(Long streetId) {
        this.streetId = streetId;
    }

    public List<BuildingPartImport> getBuildingParts() {
        return buildingParts;
    }

    public void setBuildingParts(List<BuildingPartImport> buildingParts) {
        this.buildingParts = buildingParts;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }
}
