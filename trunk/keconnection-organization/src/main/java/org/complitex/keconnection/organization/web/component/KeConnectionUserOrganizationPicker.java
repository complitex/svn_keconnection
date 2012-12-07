/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.web.component;

import javax.ejb.EJB;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.web.component.organization.user.UserOrganizationPickerParameters;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.complitex.organization_type.strategy.OrganizationTypeStrategy;

/**
 *
 * @author Artem
 */
public class KeConnectionUserOrganizationPicker extends Panel {

    @EJB
    private StrategyFactory strategyFactory;

    public KeConnectionUserOrganizationPicker(String id, final IModel<Long> organizationIdModel,
            UserOrganizationPickerParameters parameters) {
        super(id);

        add(new OrganizationPicker("picker", new Model<DomainObject>() {

            @Override
            public DomainObject getObject() {
                Long id = organizationIdModel.getObject();
                if (id != null) {
                    return getOrganizationStrategy().findById(id, true);
                }
                return null;
            }

            @Override
            public void setObject(DomainObject object) {
                organizationIdModel.setObject(object != null ? object.getId() : null);
            }
        }, OrganizationTypeStrategy.USER_ORGANIZATION_TYPE));
    }

    private IKeConnectionOrganizationStrategy getOrganizationStrategy() {
        return (IKeConnectionOrganizationStrategy) strategyFactory.getStrategy(
                IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME, "organization");
    }
}
