/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.address.entity;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public class BuildingPartImport implements Serializable {

    private long id;
    private String part;
    private Long gek;
    private String code;
    private long buildingImportId;

    public BuildingPartImport() {
    }

    public BuildingPartImport(long id, String part, long gekId, String code, long buildingImportId) {
        this.id = id;
        this.part = part;
        this.gek = gekId;
        this.code = code;
        this.buildingImportId = buildingImportId;
    }

    public long getBuildingImportId() {
        return buildingImportId;
    }

    public void setBuildingImportId(long buildingImportId) {
        this.buildingImportId = buildingImportId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getGek() {
        return gek;
    }

    public void setGek(Long gek) {
        this.gek = gek;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }
}
