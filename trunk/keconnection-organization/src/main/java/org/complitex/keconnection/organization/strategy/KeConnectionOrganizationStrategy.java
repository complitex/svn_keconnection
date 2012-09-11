/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.strategy;

import javax.ejb.Stateless;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.organization.strategy.OrganizationStrategy;

/**
 *
 * @author Artem
 */
@Stateless
public class KeConnectionOrganizationStrategy extends OrganizationStrategy implements IKeConnectionOrganizationStrategy {

    public static final String KECONNECTION_ORGANIZATION_STRATEGY_NAME = KeConnectionOrganizationStrategy.class.getSimpleName();

    @Override
    public PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity) {
        PageParameters pageParameters = super.getEditPageParams(objectId, parentId, parentEntity);
        pageParameters.set(STRATEGY, KECONNECTION_ORGANIZATION_STRATEGY_NAME);
        return pageParameters;
    }

    @Override
    public PageParameters getHistoryPageParams(long objectId) {
        PageParameters pageParameters = super.getHistoryPageParams(objectId);
        pageParameters.set(STRATEGY, KECONNECTION_ORGANIZATION_STRATEGY_NAME);
        return pageParameters;
    }

    @Override
    public PageParameters getListPageParams() {
        PageParameters pageParameters = super.getListPageParams();
        pageParameters.set(STRATEGY, KECONNECTION_ORGANIZATION_STRATEGY_NAME);
        return pageParameters;
    }
}
