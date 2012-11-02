/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.component.heatmeter.list;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 *
 * @author Artem
 */
public final class HeatmeterConsumptionItem extends Panel {

    public HeatmeterConsumptionItem(String id, final IModel<BigDecimal> consumptionModel, boolean editable) {
        super(id);

        Label label = new Label("label", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                BigDecimal value = consumptionModel.getObject();
                if (value == null) {
                    value = BigDecimal.ZERO;
                }
                NumberFormat formatter = new DecimalFormat("#######0.0000###", DecimalFormatSymbols.getInstance(getLocale()));
                return formatter.format(value);
            }
        });
        label.setVisible(!editable);
        add(label);

        TextField<BigDecimal> input = new TextField<BigDecimal>("input", consumptionModel);
        input.setVisible(editable);
        input.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        add(input);
    }
}
