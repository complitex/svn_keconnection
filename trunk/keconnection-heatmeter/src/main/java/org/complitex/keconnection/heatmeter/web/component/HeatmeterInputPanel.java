package org.complitex.keconnection.heatmeter.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.complitex.dictionary.web.component.LabelDateField;
import org.complitex.dictionary.web.component.LabelTextField;
import org.complitex.dictionary.web.component.TextLabel;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConsumption;
import org.complitex.keconnection.heatmeter.entity.HeatmeterInput;
import org.complitex.keconnection.heatmeter.service.HeatmeterInputBean;

import javax.ejb.EJB;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.complitex.dictionary.web.component.LabelTextField.Converter.BIG_DECIMAL_CONVERTER_7;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.10.12 14:55
 */
public class HeatmeterInputPanel extends AbstractHeatmeterEditPanel {
    @EJB
    private HeatmeterInputBean inputBean;

    public HeatmeterInputPanel(String id, final IModel<Heatmeter> model, final IModel<Date> om) {
        super(id, model, om);

        setOutputMarkupId(true);

        final IModel<List<HeatmeterInput>> inputsModel = new LoadableDetachableModel<List<HeatmeterInput>>() {

            @Override
            protected List<HeatmeterInput> load() {
                Heatmeter heatmeter = model.getObject();

                return isActiveOm()
                        ? heatmeter.getInputs()
                        : inputBean.getList(heatmeter.getId(), om.getObject());
            }
        };

        final ListView<HeatmeterInput> listView = new ListView<HeatmeterInput>("list_view", inputsModel) {

            @Override
            protected void populateItem(ListItem<HeatmeterInput> item) {
                final HeatmeterInput input = item.getModelObject();
                final HeatmeterConsumption consumption = input.getSumConsumption();

                item.add(new LabelDateField("readoutDate", new PropertyModel<Date>(input, "endDate"), false));
                item.add(new LabelTextField<>("consumption", 15, new PropertyModel<BigDecimal>(input, "value"), BIG_DECIMAL_CONVERTER_7));
                item.add(new TextLabel("consumption1", new PropertyModel<>(consumption, "consumption1")));
                item.add(new TextLabel("consumption2", new PropertyModel<>(consumption, "consumption2")));
                item.add(new TextLabel("consumption3", new PropertyModel<>(consumption, "consumption3")));

//                item.add(new TextLabel("status", input.getStatus()));

                item.add(new AjaxSubmitLink("remove") {

                    @Override
                    public boolean isVisible() {
                        return isActiveOm() && !inputsModel.getObject().isEmpty();
                    }

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        model.getObject().getInputs().remove(input);
                        target.add(HeatmeterInputPanel.this);
                    }

                    @Override
                    protected void onError(AjaxRequestTarget target, Form<?> form) {
                    }
                });

                item.visitChildren(new IVisitor<Component, Object>() {

                    @Override
                    public void component(Component object, IVisit<Object> visit) {
                        object.setEnabled(isActiveOm());
                        visit.dontGoDeeper();
                    }
                });
            }
        };
        add(listView);

        add(new WebMarkupContainer("header") {

            @Override
            public boolean isVisible() {
                return !inputsModel.getObject().isEmpty();
            }
        });

        add(new AjaxSubmitLink("add") {

            @Override
            public boolean isVisible() {
                return isActiveOm();
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                HeatmeterInput input = new HeatmeterInput(model.getObject().getId(), om.getObject());
                input.setEndOm(om.getObject());
                model.getObject().getInputs().add(input);

                target.add(HeatmeterInputPanel.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        });
    }
}
