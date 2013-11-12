/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.web.component;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.dictionary.web.component.permission.organization.OrganizationPermissionParameters;
import org.complitex.keconnection.organization.strategy.KeConnectionOrganizationStrategy;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Artem
 */
public class KeConnectionOrganizationPermissionPanel extends KeConnectionDomainObjectPermissionPanel {
    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeConnectionOrganizationStrategy organizationStrategy;

    private final Long organizationId;

    public KeConnectionOrganizationPermissionPanel(String id,
            OrganizationPermissionParameters organizationPermissionParameters) {
        super(id, organizationPermissionParameters.getParameters());
        this.organizationId = organizationPermissionParameters.getOrganizationId();
    }

    @Override
    protected boolean isAllowModifyOrganization(int index, Long subjectId) {
        return organizationId != null && organizationId > 0 ? index != 0 : true;
    }

    @Override
    protected List<DomainObject> initializeSelectedSubjects(Set<Long> selectedSubjectIds) {
        final List<DomainObject> superSelectedSubjects = super.initializeSelectedSubjects(selectedSubjectIds);

        DomainObject itself = organizationStrategy.findById(organizationId, true);
        if (organizationStrategy.isUserOrganization(itself)) {
            if (organizationId != null && organizationId > 0) {
                List<DomainObject> selectedSubjects = new ArrayList<>();

                for (DomainObject o : superSelectedSubjects) {
                    if (!o.getId().equals(organizationId)) {
                        selectedSubjects.add(o);
                    }
                }
                selectedSubjects.add(0, itself);
                return selectedSubjects;
            }
        }
        return superSelectedSubjects;
    }
}
