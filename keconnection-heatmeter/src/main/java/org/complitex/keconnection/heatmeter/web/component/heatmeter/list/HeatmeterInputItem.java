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
public final class HeatmeterInputItem extends Panel {

    public HeatmeterInputItem(String id, final IModel<BigDecimal> inputModel, boolean editable) {
        super(id);

        Label label = new Label("label", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                BigDecimal value = inputModel.getObject();
                if (value == null) {
                    value = BigDecimal.ZERO;
                }
                NumberFormat formatter = new DecimalFormat("0.0000000", DecimalFormatSymbols.getInstance(getLocale()));
                return formatter.format(value);
            }
        });
        label.setVisible(!editable);
        add(label);

        TextField<BigDecimal> input = new TextField<BigDecimal>("input", inputModel);
        input.setVisible(editable);
        input.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        add(input);
    }
}
