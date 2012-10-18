/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.web.template;

import static com.google.common.base.Strings.*;
import java.util.Locale;
import org.apache.wicket.model.IModel;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.complitex.keconnection.organization.strategy.entity.Organization;
import org.complitex.template.web.component.MainUserOrganizationPicker;

/**
 *
 * @author Artem
 */
public class KeConnectionMainUserOrganizationPicker extends MainUserOrganizationPicker {

    public KeConnectionMainUserOrganizationPicker(String id, IModel<DomainObject> model) {
        super(id, model);
    }

    @Override
    protected String displayOrganization(DomainObject organization) {
        Organization o = (Organization) organization;

        IOrganizationStrategy organizationStrategy = getOrgaizationStrategy();
        final Locale locale = getLocale();
        final String name = organizationStrategy.displayDomainObject(o, locale);
        final String code = organizationStrategy.getUniqueCode(o);
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
            return name + " " + code + " " + operatingMonth;
        }
    }

    @Override
    protected String getOrganizationStrategyName() {
        return IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME;
    }
}
