/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.address.strategy.building.web.list;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.address.strategy.building.web.list.BuildingList;
import org.complitex.keconnection.address.strategy.building.KeConnectionBuildingStrategy;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADDRESS_MODULE_VIEW)
public class KeConnectionBuildingList extends BuildingList {

    @Override
    protected String getBuildingStrategyName() {
        return KeConnectionBuildingStrategy.KECONNECTION_BUILDING_STRATEGY_NAME;
    }
}
