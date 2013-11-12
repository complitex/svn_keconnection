/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.correction.component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterCorrection;
import org.complitex.keconnection.heatmeter.service.HeatmeterCorrectionBean;
import org.odlabs.wiquery.ui.dialog.Dialog;

/**
 *
 * @author Artem
 */
public final class HeatmeterCorrectionDialog extends Panel {

    @EJB
    private HeatmeterCorrectionBean heatmeterCorrectionBean;
    private final Dialog dialog;

    public HeatmeterCorrectionDialog(String id, final Heatmeter heatmeter) {
        super(id);

        dialog = new Dialog("dialog") {

            {
                getOptions().putLiteral("width", "auto");
            }
        };
        dialog.setModal(true);
        add(dialog);

        dialog.add(new Label("caption", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return MessageFormat.format(getString("caption"), heatmeter.getId(), heatmeter.getLs());
            }
        }));

        final Long heatmeterId = heatmeter.getId();
        List<HeatmeterCorrection> allCorrections = heatmeterId != null
                ? heatmeterCorrectionBean.findAllCorrections(heatmeterId) : null;
        if (allCorrections == null || allCorrections.isEmpty()) {
            allCorrections = new ArrayList<>();
            setVisible(false);
        }
        HeatmeterCorrection activeCorrection = heatmeterId != null ? getActiveCorrection(heatmeterId, allCorrections) : null;

        dialog.add(activeCorrection != null
                ? new HeatmeterItemCorrectionPanel("heatmeterItemCorrectionPanel", activeCorrection)
                : new EmptyPanel("heatmeterItemCorrectionPanel"));

        WebMarkupContainer historySection = new WebMarkupContainer("historySection");
        dialog.add(historySection);
        List<HeatmeterCorrection> historyCorrections = heatmeterCorrectionBean.getHistoryCorrections(allCorrections);
        historySection.setVisible(!historyCorrections.isEmpty());
        historySection.add(new HeatmeterHistoryCorrectionPanel("heatmeterHistoryCorrectionPanel", historyCorrections));
    }

    private HeatmeterCorrection getActiveCorrection(Long heatmeterId, List<HeatmeterCorrection> allCorrections) {
        if (allCorrections != null && !allCorrections.isEmpty()) {
            for (HeatmeterCorrection c : allCorrections) {
                if (!c.isHistory()) {
                    return c;
                }
            }
            throw new IllegalStateException("For heatmeter id: " + heatmeterId + " no active correction.");
        }
        return null;
    }

    public void open(AjaxRequestTarget target) {
        dialog.open(target);
    }
}
