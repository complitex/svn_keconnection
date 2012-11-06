/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.correction.component.model;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.List;
import org.apache.wicket.model.Model;
import org.complitex.dictionary.entity.DomainObject;

/**
 *
 * @author Artem
 */
public abstract class OrganizationModel extends Model<DomainObject> {

    @Override
    public DomainObject getObject() {
        final Long organizationId = getOrganizationId();
        if (organizationId != null) {
            return Iterables.find(getOrganizations(), new Predicate<DomainObject>() {

                @Override
                public boolean apply(DomainObject object) {
                    return object.getId().equals(organizationId);
                }
            });
        }
        return null;
    }

    @Override
    public void setObject(DomainObject object) {
        if (object != null) {
            setOrganizationId(object.getId());
        } else {
            setOrganizationId(null);
        }
    }

    public abstract Long getOrganizationId();

    public abstract void setOrganizationId(Long organizationId);

    public abstract List<DomainObject> getOrganizations();
}
