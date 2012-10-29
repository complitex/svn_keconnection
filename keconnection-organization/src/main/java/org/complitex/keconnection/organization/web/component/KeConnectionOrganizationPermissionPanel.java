/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.web.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.web.component.permission.organization.OrganizationPermissionParameters;

/**
 *
 * @author Artem
 */
public class KeConnectionOrganizationPermissionPanel extends KeConnectionDomainObjectPermissionPanel {

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
        List<DomainObject> superSelectedSubjects = super.initializeSelectedSubjects(selectedSubjectIds);
        if (organizationId != null && organizationId > 0 && !selectedSubjectIds.contains(organizationId)) {
            List<DomainObject> selectedSubjects = new ArrayList<>(superSelectedSubjects);
            //add organization itself at 0 position in list because organization must see themself.
            selectedSubjects.add(0, getOrganizationStrategy().findById(organizationId, true));
            return selectedSubjects;
        } else {
            return superSelectedSubjects;
        }
    }
}
