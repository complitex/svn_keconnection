/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.correction;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.template.web.template.ITemplateLink;
import org.complitex.template.web.template.ResourceTemplateMenu;

/**
 *
 * @author Artem
 */
public class CorrectionMenu extends ResourceTemplateMenu {

    @Override
    public String getTitle(Locale locale) {
        return getString(CorrectionMenu.class, locale, "title");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> links = Lists.newArrayList();

        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(CorrectionMenu.class, locale, "heatmeter_correction");
            }

            @Override
            public Class<? extends Page> getPage() {
                return HeatmeterCorrectionList.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "heatmeter_correction_item";
            }
        });
        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(CorrectionMenu.class, locale, "city_correction");
            }

            @Override
            public Class<? extends Page> getPage() {
                return CityCorrectionList.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "city_correction_item";
            }
        });
        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(CorrectionMenu.class, locale, "district_correction");
            }

            @Override
            public Class<? extends Page> getPage() {
                return DistrictCorrectionList.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "district_correction_item";
            }
        });
        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(CorrectionMenu.class, locale, "street_correction");
            }

            @Override
            public Class<? extends Page> getPage() {
                return StreetCorrectionList.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "street_correction_item";
            }
        });
        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(CorrectionMenu.class, locale, "street_type_correction");
            }

            @Override
            public Class<? extends Page> getPage() {
                return StreetTypeCorrectionList.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "street_type_correction_item";
            }
        });
        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(CorrectionMenu.class, locale, "building_correction");
            }

            @Override
            public Class<? extends Page> getPage() {
                return BuildingCorrectionList.class;
            }

            @Override
            public PageParameters getParameters() {
                return new PageParameters();
            }

            @Override
            public String getTagId() {
                return "building_correction_item";
            }
        });
        return links;
    }

    @Override
    public String getTagId() {
        return "correction_menu";
    }
}
