package org.complitex.keconnection.heatmeter.web;

import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.template.web.template.ITemplateLink;
import org.complitex.template.web.template.ResourceTemplateMenu;

import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.09.12 15:02
 */
public class HeatmeterTypeMenu extends ResourceTemplateMenu {
    public HeatmeterTypeMenu() {
        //heatmeter types
        add(new ITemplateLink() {

            private IStrategy getStrategy() {
                StrategyFactory strategyFactory = EjbBeanLocator.getBean(StrategyFactory.class);
                return strategyFactory.getStrategy("heatmeter_type");
            }

            @Override
            public String getLabel(Locale locale) {
                return getStrategy().getPluralEntityLabel(locale);
            }

            @Override
            public Class<? extends Page> getPage() {
                return getStrategy().getListPage();
            }

            @Override
            public PageParameters getParameters() {
                return getStrategy().getListPageParams();
            }

            @Override
            public String getTagId() {
                return "heatmeter_type_item";
            }
        });

        //heatmeter period types
        add(new ITemplateLink() {

            private IStrategy getStrategy() {
                StrategyFactory strategyFactory = EjbBeanLocator.getBean(StrategyFactory.class);
                return strategyFactory.getStrategy("heatmeter_period_type");
            }

            @Override
            public String getLabel(Locale locale) {
                return getStrategy().getPluralEntityLabel(locale);
            }

            @Override
            public Class<? extends Page> getPage() {
                return getStrategy().getListPage();
            }

            @Override
            public PageParameters getParameters() {
                return getStrategy().getListPageParams();
            }

            @Override
            public String getTagId() {
                return "heatmeter_period_type_item";
            }
        });
    }
}
