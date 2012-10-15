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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.complitex.keconnection.heatmeter.entity.HeatmeterCorrection;
import org.complitex.keconnection.heatmeter.service.HeatmeterCorrectionBean;
import org.odlabs.wiquery.ui.dialog.Dialog;

/**
 *
 * @author Artem
 */
public final class HeatmeterHistoryCorrectionDialog extends Panel {

    @EJB
    private HeatmeterCorrectionBean heatmeterCorrectionBean;
    private final Dialog dialog;

    public HeatmeterHistoryCorrectionDialog(String id, final long heatmeterId, final int ls) {
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
                return MessageFormat.format(getString("caption"), heatmeterId, ls);
            }
        }));

        List<HeatmeterCorrection> historyCorrections = heatmeterCorrectionBean.findHistoryCorrections(heatmeterId);
        if (historyCorrections == null || historyCorrections.isEmpty()) {
            historyCorrections = new ArrayList<>();
            setVisible(false);
        }

        dialog.add(new HeatmeterHistoryCorrectionPanel("heatmeterHistoryCorrectionPanel", historyCorrections));
    }

    public void open(AjaxRequestTarget target) {
        dialog.open(target);
    }
}
