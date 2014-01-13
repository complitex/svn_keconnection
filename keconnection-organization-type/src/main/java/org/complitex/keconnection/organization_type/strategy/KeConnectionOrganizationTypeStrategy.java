/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization_type.strategy;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.organization_type.strategy.OrganizationTypeStrategy;

import javax.ejb.Stateless;
import java.util.Collection;

/**
 *
 * @author Artem
 */
@Stateless
public class KeConnectionOrganizationTypeStrategy extends OrganizationTypeStrategy {

    private static final String STRATEGY_NAME = KeConnectionOrganizationTypeStrategy.class.getSimpleName();
    /**
     * Organization type ids
     */
    public static final long CALCULATION_MODULE = 2;
    public static final long BALANCE_OWNER = 3;
    public static final long SERVICE_PROVIDER = 5;
    public static final long CONTRACTOR = 6;

    @Override
    protected Collection<Long> getReservedInstanceIds() {
        return ImmutableList.of(USER_ORGANIZATION_TYPE, CALCULATION_MODULE, BALANCE_OWNER,
                SERVICING_ORGANIZATION_TYPE, SERVICE_PROVIDER, CONTRACTOR);
    }

    @Override
    public PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity) {
        PageParameters pageParameters = super.getEditPageParams(objectId, parentId, parentEntity);
        pageParameters.set(STRATEGY, STRATEGY_NAME);
        return pageParameters;
    }

    @Override
    public PageParameters getHistoryPageParams(long objectId) {
        PageParameters pageParameters = super.getHistoryPageParams(objectId);
        pageParameters.set(STRATEGY, STRATEGY_NAME);
        return pageParameters;
    }

    @Override
    public PageParameters getListPageParams() {
        PageParameters pageParameters = super.getListPageParams();
        pageParameters.set(STRATEGY, STRATEGY_NAME);
        return pageParameters;
    }
}
