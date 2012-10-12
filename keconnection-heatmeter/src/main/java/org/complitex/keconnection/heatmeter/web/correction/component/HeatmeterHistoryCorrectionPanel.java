/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.correction.component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.complitex.keconnection.heatmeter.entity.HeatmeterCorrection;
import org.complitex.keconnection.heatmeter.service.HeatmeterBindingStatusRenderer;
import static org.complitex.dictionary.util.PageUtil.*;

/**
 *
 * @author Artem
 */
public final class HeatmeterHistoryCorrectionPanel extends Panel {

    private static final String DATE_PATTERN = "HH:mm dd.MM.yyyy";
    @EJB
    private HeatmeterBindingStatusRenderer heatmeterBindingStatusRenderer;

    private static DateFormat newDateFormat(String pattern, Locale locale) {
        return new SimpleDateFormat(pattern, locale);
    }

    private static String format(Date date, Locale locale) {
        return newDateFormat(DATE_PATTERN, locale).format(date);
    }

    public HeatmeterHistoryCorrectionPanel(String id, List<HeatmeterCorrection> historyCorrections) {
        super(id);

        add(new ListView<HeatmeterCorrection>("corrections", historyCorrections) {

            @Override
            protected void populateItem(ListItem<HeatmeterCorrection> item) {
                final HeatmeterCorrection correction = item.getModelObject();
                item.add(newTextLabels("externalHeatmeterId", "heatmeterNumber"));
                item.add(new Label("bindingDate", format(correction.getBindingDate(), getLocale())));
                item.add(new Label("bindingStatus",
                        heatmeterBindingStatusRenderer.render(correction.getBindingStatus(), getLocale())));
            }

            @Override
            protected IModel<HeatmeterCorrection> getListItemModel(IModel<? extends List<HeatmeterCorrection>> listViewModel,
                    int index) {
                return new CompoundPropertyModel<>(super.getListItemModel(listViewModel, index));
            }
        });
    }
}
