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
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.template.web.pages.EntityDescription;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.ITemplateLink;
import org.complitex.template.web.template.ResourceTemplateMenu;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class TarifDescriptionMenu extends ResourceTemplateMenu {

    private String getEntityName(String entity, Locale locale) {
        StringCultureBean stringBean = EjbBeanLocator.getBean(StringCultureBean.class);
        IStrategy strategy = EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(entity);
        return stringBean.displayValue(strategy.getEntity().getEntityNames(), locale);
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(final Locale locale) {
        return ImmutableList.<ITemplateLink>of(
                new ITemplateLink() {

                    static final String TARIF_GROUP_ENTITY = "tarif_group";

                    @Override
                    public String getLabel(Locale locale) {
                        return getEntityName(TARIF_GROUP_ENTITY, locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return EntityDescription.class;
                    }

                    @Override
                    public PageParameters getParameters() {
                        return new PageParameters().set(EntityDescription.ENTITY, TARIF_GROUP_ENTITY);
                    }

                    @Override
                    public String getTagId() {
                        return "tarif_group_description_item";
                    }
                },
                new ITemplateLink() {

                    static final String TARIF_ENTITY = "tarif";

                    @Override
                    public String getLabel(Locale locale) {
                        return getEntityName(TARIF_ENTITY, locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return EntityDescription.class;
                    }

                    @Override
                    public PageParameters getParameters() {
                        return new PageParameters().set(EntityDescription.ENTITY, TARIF_ENTITY);
                    }

                    @Override
                    public String getTagId() {
                        return "tarif_description_item";
                    }
                });
    }

    @Override
    public String getTagId() {
        return "tarif_description_menu";
    }
}
