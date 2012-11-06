/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.strategy.entity;

import org.complitex.dictionary.converter.BooleanConverter;
import java.util.Date;
import java.util.Locale;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.util.AttributeUtil;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import static org.complitex.dictionary.util.DateUtil.*;

/**
 *
 * @author Artem
 */
public class Organization extends DomainObject {

    private Date operatingMonthDate;
    private String parentShortName;

    public Organization(DomainObject copy) {
        super(copy);
    }

    public Organization() {
    }

    public Date getOperatingMonthDate() {
        return operatingMonthDate;
    }

    public void setOperatingMonthDate(Date operatingMonthDate) {
        this.operatingMonthDate = operatingMonthDate;
    }

    public String getOperatingMonth(Locale locale) {
        if (operatingMonthDate == null) {
            return null;
        }
        return displayMonth(getMonth(getOperatingMonthDate()) + 1, locale);
    }

    public String getParentShortName() {
        return parentShortName;
    }

    public void setParentShortName(String parentShortName) {
        this.parentShortName = parentShortName;
    }

    public Boolean isReadyCloseOperatingMonth() {
        return AttributeUtil.getAttributeValue(this, IKeConnectionOrganizationStrategy.READY_CLOSE_OPER_MONTH,
                new BooleanConverter());
    }
}
