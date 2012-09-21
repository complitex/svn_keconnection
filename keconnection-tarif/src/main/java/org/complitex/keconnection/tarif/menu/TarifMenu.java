/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.tarif.menu;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.ITemplateLink;
import org.complitex.template.web.template.ResourceTemplateMenu;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class TarifMenu extends ResourceTemplateMenu {

    private IStrategy getStrategy(String entity) {
        return EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(entity);
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        return ImmutableList.<ITemplateLink>of(
                new ITemplateLink() {

                    static final String TARIF_GROUP_ENTITY = "tarif_group";

                    @Override
                    public String getLabel(Locale locale) {
                        return getStrategy(TARIF_GROUP_ENTITY).getPluralEntityLabel(locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return getStrategy(TARIF_GROUP_ENTITY).getListPage();
                    }

                    @Override
                    public PageParameters getParameters() {
                        return getStrategy(TARIF_GROUP_ENTITY).getListPageParams();
                    }

                    @Override
                    public String getTagId() {
                        return "tarif_group_item";
                    }
                },
                new ITemplateLink() {

                    static final String TARIF_ENTITY = "tarif";

                    @Override
                    public String getLabel(Locale locale) {
                        return getStrategy(TARIF_ENTITY).getPluralEntityLabel(locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return getStrategy(TARIF_ENTITY).getListPage();
                    }

                    @Override
                    public PageParameters getParameters() {
                        return getStrategy(TARIF_ENTITY).getListPageParams();
                    }

                    @Override
                    public String getTagId() {
                        return "tarif_item";
                    }
                });
    }

    @Override
    public String getTagId() {
        return "tarif_menu";
    }
}
