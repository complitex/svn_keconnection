/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.web.template;

import org.apache.wicket.model.IModel;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.keconnection.organization.strategy.entity.Organization;
import org.complitex.template.web.component.MainUserOrganizationPicker;

import javax.ejb.EJB;
import java.util.Locale;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 * @author Artem
 */
public class KeConnectionMainUserOrganizationPicker extends MainUserOrganizationPicker {
    @EJB(name = "OrganizationStrategy")
    private IOrganizationStrategy organizationStrategy;


    public KeConnectionMainUserOrganizationPicker(String id, IModel<DomainObject> model) {
        super(id, model);
    }

    @Override
    protected String displayOrganization(DomainObject organization) {
        Organization o = (Organization) organization;

        final Locale locale = getLocale();
        final String name = organizationStrategy.displayDomainObject(o, locale);
        final String code = organizationStrategy.getCode(o);
        final String operatingMonth = o.getOperatingMonth(locale);

        if (isNullOrEmpty(operatingMonth)) {
            if (isNullOrEmpty(code)) {
                return name;
            } else {
                return name + " " + code;
            }
        } else if (isNullOrEmpty(code)) {
            return name + " " + operatingMonth;
        } else {
            return code + " - " + name + " (" + operatingMonth + ")";
        }
    }
}
