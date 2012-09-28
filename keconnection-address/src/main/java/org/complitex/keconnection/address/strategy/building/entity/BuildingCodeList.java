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
public final class BuildingCodeList extends ArrayList<BuildingCode> {

    public BuildingCodeList(List<BuildingCode> buildingCodes) {
        super(buildingCodes);
    }

    public BuildingCodeList() {
        super(new ArrayList<BuildingCode>());
    }

    public void addNew() {
        add(new BuildingCode());
    }

    public boolean hasNulls() {
        for (BuildingCode buildingCode : this) {
            if (buildingCode == null || buildingCode.getOrganizationId() == null
                    || Strings.isNullOrEmpty(buildingCode.getBuildingCode())) {
                return true;
            }
        }
        return false;
    }

    public boolean allowAddNew(BuildingCode buildingCode) {
        for (BuildingCode a : this) {
            if (buildingCode.equals(a)) {
                return false;
            }
        }
        return true;
    }
}
