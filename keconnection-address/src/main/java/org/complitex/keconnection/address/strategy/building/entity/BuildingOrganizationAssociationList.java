/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.address.strategy.building.entity;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;

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

    public boolean hasNulls() {
        for (BuildingOrganizationAssociation buildingOrganizationAssociation : this) {
            if (buildingOrganizationAssociation == null || buildingOrganizationAssociation.getOrganizationId() == null
                    || Strings.isNullOrEmpty(buildingOrganizationAssociation.getBuildingCode())) {
                return true;
            }
        }
        return false;
    }
}
