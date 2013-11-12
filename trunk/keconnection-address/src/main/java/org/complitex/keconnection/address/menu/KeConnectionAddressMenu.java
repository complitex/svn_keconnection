/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.address.menu;

import com.google.common.collect.Lists;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.address.menu.AddressMenu;
import org.complitex.address.resource.CommonResources;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.ITemplateLink;
import org.complitex.template.web.template.ResourceTemplateMenu;

import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADDRESS_MODULE_VIEW)
public class KeConnectionAddressMenu extends ResourceTemplateMenu {

    private static final String[] ADDRESS_ENTITIES = {"country", "region", "city", "city_type", "district", "street",
        "street_type", "building"};

    private static IStrategy getStrategy(String entity) {
        return EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(entity);
    }

    @Override
    public String getTitle(Locale locale) {
        return getString(CommonResources.class, locale, "address_menu");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(final Locale locale) {
        List<ITemplateLink> links = Lists.newArrayList();

        for (final String addressEntity : ADDRESS_ENTITIES) {
            links.add(new ITemplateLink() {

                @Override
                public String getLabel(Locale locale) {
                    return getStrategy(addressEntity).getPluralEntityLabel(locale);
                }

                @Override
                public Class<? extends Page> getPage() {
                    return getStrategy(addressEntity).getListPage();
                }

                @Override
                public PageParameters getParameters() {
                    return getStrategy(addressEntity).getListPageParams();
                }

                @Override
                public String getTagId() {
                    return addressEntity + AddressMenu.ADDRESS_MENU_ITEM_SUFFIX;
                }
            });
        }
        return links;
    }

    @Override
    public String getTagId() {
        return "address_menu";
    }
}
