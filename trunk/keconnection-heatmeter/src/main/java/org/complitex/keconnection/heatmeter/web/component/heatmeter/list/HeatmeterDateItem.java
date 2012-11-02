/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.component.heatmeter.list;

import java.util.Date;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.web.component.dateinput.MaskedDateInput;

/**
 *
 * @author Artem
 */
public final class HeatmeterDateItem extends Panel {

    public HeatmeterDateItem(String id, final IModel<Date> model, boolean editable) {
        super(id);

        Label label = new Label("label", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return DateUtil.format(model.getObject());
            }
        });
        label.setVisible(!editable);
        add(label);

        WebMarkupContainer inputContainer = new WebMarkupContainer("inputContainer");
        inputContainer.setVisible(editable);
        add(inputContainer);
        MaskedDateInput input = new MaskedDateInput("input", model);
        input.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        inputContainer.add(input);
    }
}
