/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.strategy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.keconnection.organization_type.strategy.KeConnectionOrganizationTypeStrategy;
import org.complitex.organization.strategy.OrganizationStrategy;

/**
 *
 * @author Artem
 */
@Stateless
public class KeConnectionOrganizationStrategy extends OrganizationStrategy implements IKeConnectionOrganizationStrategy {

    @EJB
    private LocaleBean localeBean;

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

    @Override
    public List<DomainObject> getAllServicingOrganizations(Locale locale) {
        DomainObjectExample example = new DomainObjectExample();
        example.addAdditionalParam(ORGANIZATION_TYPE_PARAMETER,
                ImmutableList.of(KeConnectionOrganizationTypeStrategy.SERVICING_ORGANIZATION));
        if (locale != null) {
            example.setOrderByAttributeTypeId(NAME);
            example.setLocaleId(localeBean.convert(locale).getId());
            example.setAsc(true);
        }
        configureExample(example, ImmutableMap.<String, Long>of(), null);
        return (List<DomainObject>) find(example);
    }
}
