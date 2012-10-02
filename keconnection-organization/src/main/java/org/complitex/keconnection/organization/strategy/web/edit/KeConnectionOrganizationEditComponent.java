/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.strategy.web.edit;

import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.complitex.organization.strategy.web.edit.OrganizationEditComponent;

/**
 *
 * @author Artem
 */
public class KeConnectionOrganizationEditComponent extends OrganizationEditComponent {

    public KeConnectionOrganizationEditComponent(String id, boolean disabled) {
        super(id, disabled);
    }

    @Override
    protected boolean isOrganizationTypeEnabled() {
        Long organizationId = getDomainObject().getId();
        return !(organizationId != null && (organizationId == IKeConnectionOrganizationStrategy.ITSELF_ORGANIZATION_OBJECT_ID))
                && super.isOrganizationTypeEnabled();
    }
}
