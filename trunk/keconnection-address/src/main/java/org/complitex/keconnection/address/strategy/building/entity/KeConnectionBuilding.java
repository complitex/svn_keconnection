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

    public KeConnectionBuilding(Building copy) {
        super(copy);
        setPrimaryAddress(copy.getPrimaryAddress());
        setAlternativeAddresses(copy.getAlternativeAddresses());
        setDistrict(copy.getDistrict());
        setAccompaniedAddress(copy.getAccompaniedAddress());
    }
}
