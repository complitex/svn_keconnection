/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.correction.component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.complitex.keconnection.heatmeter.entity.HeatmeterCorrection;
import org.complitex.keconnection.heatmeter.service.HeatmeterBindingStatusRenderer;

/**
 *
 * @author Artem
 */
public final class HeatmeterItemCorrectionPanel extends Panel {

    private static final String DATE_PATTERN = "HH:mm dd.MM.yyyy";
    @EJB
    private HeatmeterBindingStatusRenderer heatmeterBindingStatusRenderer;

    private static DateFormat newDateFormat(String pattern, Locale locale) {
        return new SimpleDateFormat(pattern, locale);
    }

    private static String format(Date date, Locale locale) {
        return newDateFormat(DATE_PATTERN, locale).format(date);
    }

    public HeatmeterItemCorrectionPanel(String id, HeatmeterCorrection correction) {
        super(id);

        add(new Label("externalHeatmeterId", correction.getExternalHeatmeterId()));
        add(new Label("heatmeterNumber", correction.getHeatmeterNumber()));
        add(new Label("bindingDate", format(correction.getBindingDate(), getLocale())));
        add(new Label("bindingStatus",
                heatmeterBindingStatusRenderer.render(correction.getBindingStatus(), getLocale())));
    }
}
