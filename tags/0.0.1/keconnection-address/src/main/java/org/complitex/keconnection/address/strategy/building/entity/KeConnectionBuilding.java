/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.address.strategy.building.entity;

import org.complitex.address.strategy.building.entity.Building;

/**
 *
 * @author Artem
 */
public class KeConnectionBuilding extends Building {

    private final BuildingCodeList buildingCodeList;

    public KeConnectionBuilding(Building copy, BuildingCodeList associationList) {
        super(copy);
        setPrimaryAddress(copy.getPrimaryAddress());
        setAlternativeAddresses(copy.getAlternativeAddresses());
        setDistrict(copy.getDistrict());
        setAccompaniedAddress(copy.getAccompaniedAddress());
        this.buildingCodeList = associationList;
    }

    public KeConnectionBuilding(BuildingCodeList associationList) {
        this.buildingCodeList = associationList;
    }

    public BuildingCodeList getBuildingCodeList() {
        return buildingCodeList;
    }
}
