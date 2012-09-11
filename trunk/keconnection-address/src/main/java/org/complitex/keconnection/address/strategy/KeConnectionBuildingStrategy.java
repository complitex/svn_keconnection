/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.address.strategy;

import javax.ejb.Stateless;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.keconnection.address.strategy.building.entity.KeConnectionBuilding;
import org.complitex.keconnection.address.strategy.building.web.list.KeConnectionBuildingList;

/**
 *
 * @author Artem
 */
@Stateless
public class KeConnectionBuildingStrategy extends BuildingStrategy {

    public static final String KECONNECTION_BUILDING_STRATEGY_NAME = KeConnectionBuildingStrategy.class.getSimpleName();

    @Override
    public Class<? extends WebPage> getListPage() {
        return KeConnectionBuildingList.class;
    }

    @Override
    public PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity) {
        PageParameters params = super.getEditPageParams(objectId, parentId, parentEntity);
        if (params == null) {
            params = new PageParameters();
        }
        params.set(STRATEGY, KECONNECTION_BUILDING_STRATEGY_NAME);
        return params;
    }

    @Transactional
    @Override
    public Building findById(long id, boolean runAsAdmin) {
        Building building = super.findById(id, runAsAdmin);
        if (building != null) {
            return new KeConnectionBuilding(building);
        } else {
            return null;
        }
    }
}
