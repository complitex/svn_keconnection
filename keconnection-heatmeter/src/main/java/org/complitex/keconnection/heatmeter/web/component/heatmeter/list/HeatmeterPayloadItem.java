/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.component.heatmeter.list;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

/**
 *
 * @author Artem
 */
public class HeatmeterPayloadItem extends Panel {

    public HeatmeterPayloadItem(String id, final IModel<BigDecimal> payloadModel, boolean editable) {
        super(id);

        Label label = new Label("label", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                BigDecimal value = payloadModel.getObject();
                if (value == null) {
                    value = BigDecimal.ZERO;
                }
                NumberFormat formatter = new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(getLocale()));
                return formatter.format(value);
            }
        });
        label.setVisible(!editable);
        add(label);

        TextField<BigDecimal> input = new TextField<>("input", payloadModel);
        input.setVisible(editable);
        input.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        add(input);
    }
}
