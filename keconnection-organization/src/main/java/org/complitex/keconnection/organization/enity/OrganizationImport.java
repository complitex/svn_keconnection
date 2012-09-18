/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.enity;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public class OrganizationImport implements Serializable {

    private Long pkId;
    private Long organizationId;
    private String code;
    private String shortName;
    private String fullName;
    private Long hlevel;

    public OrganizationImport() {
    }

    public OrganizationImport(Long organizationId, String code, String shortName, String fullName, Long hlevel) {
        this.organizationId = organizationId;
        this.code = code;
        this.shortName = shortName;
        this.fullName = fullName;
        this.hlevel = hlevel;
    }

    public Long getPkId() {
        return pkId;
    }

    public void setPkId(Long pkId) {
        this.pkId = pkId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getHlevel() {
        return hlevel;
    }

    public void setHlevel(Long hlevel) {
        this.hlevel = hlevel;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
