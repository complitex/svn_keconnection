/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.address.strategy.building.entity;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Artem
 */
public final class BuildingOrganizationAssociationList extends ArrayList<BuildingOrganizationAssociation> {

    public BuildingOrganizationAssociationList(List<BuildingOrganizationAssociation> buildingOrganizationAssociations) {
        super(buildingOrganizationAssociations);
    }

    public BuildingOrganizationAssociationList() {
        super(new ArrayList<BuildingOrganizationAssociation>());
    }

    public void addNew() {
        add(new BuildingOrganizationAssociation());
    }

    public boolean containsOrganization(long organizationId) {
        for (BuildingOrganizationAssociation buildingOrganizationAssociation : this) {
            if (new Long(organizationId).equals(buildingOrganizationAssociation.getOrganizationId())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNulls() {
        for (BuildingOrganizationAssociation buildingOrganizationAssociation : this) {
            if (buildingOrganizationAssociation == null || buildingOrganizationAssociation.getOrganizationId() == null
                    || Strings.isNullOrEmpty(buildingOrganizationAssociation.getBuildingCode())) {
                return true;
            }
        }
        return false;
    }

    public Set<Long> getDuplicateOrganizationIds() {
        final Multimap<Long, String> buildingOrganizationCodes = ArrayListMultimap.create();

        for (BuildingOrganizationAssociation buildingOrganizationAssociation : this) {
            if (buildingOrganizationAssociation != null && buildingOrganizationAssociation.getOrganizationId() != null) {
                buildingOrganizationCodes.put(buildingOrganizationAssociation.getOrganizationId(),
                        buildingOrganizationAssociation.getBuildingCode());
            }
        }

        Set<Long> result = Sets.newHashSet();

        for (Entry<Long> e : buildingOrganizationCodes.keys().entrySet()) {
            if (e.getCount() > 1) {
                result.add(e.getElement());
            }
        }

        return result.isEmpty() ? null : result;
    }
}
