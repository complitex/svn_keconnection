package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.ILongId;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.10.12 18:20
 */
public class OperatingMonth implements ILongId{
    private Long id;
    private Long organizationId;
    private Date operatingMonth;
    private Date operatingMonthEnd;
    private Date updated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Date getOperatingMonth() {
        return operatingMonth;
    }

    public void setOperatingMonth(Date operatingMonth) {
        this.operatingMonth = operatingMonth;
    }

    public Date getOperatingMonthEnd() {
        return operatingMonthEnd;
    }

    public void setOperatingMonthEnd(Date operatingMonthEnd) {
        this.operatingMonthEnd = operatingMonthEnd;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
