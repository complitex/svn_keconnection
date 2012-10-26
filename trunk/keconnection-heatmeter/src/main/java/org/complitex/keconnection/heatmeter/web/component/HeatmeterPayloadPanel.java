package org.complitex.keconnection.heatmeter.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.web.component.dateinput.MaskedDateInput;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPayload;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.complitex.dictionary.util.DateUtil.getFirstDayOfCurrentMonth;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 16:42
 */
public class HeatmeterPayloadPanel extends AbstractHeatmeterEditPanel {
    public HeatmeterPayloadPanel(String id, final IModel<Heatmeter> model, final IModel<Date> operatingMonthModel) {
        super(id, model, operatingMonthModel);

        setOutputMarkupId(true);

        final ListView<HeatmeterPayload> payloads = new ListView<HeatmeterPayload>("list_view",
                new LoadableDetachableModel<List<HeatmeterPayload>>() {
            @Override
            protected List<HeatmeterPayload> load() {
                List<HeatmeterPayload> list = new ArrayList<>();

                for (HeatmeterPayload p : model.getObject().getPayloads()){
                    if (DateUtil.isSameMonth(p.getOperatingMonth(), operatingMonthModel.getObject())){
                        list.add(p);
                    }
                }

                return list;
            }
        }) {
            @Override
            protected void populateItem(ListItem<HeatmeterPayload> item) {
                HeatmeterPayload payload = item.getModelObject();

                item.add(new MaskedDateInput("begin_date", new PropertyModel<Date>(payload, "beginDate")));
                item.add(new MaskedDateInput("end_date", new PropertyModel<Date>(payload, "endDate")));
                item.add(new TextField<>("payload1", new PropertyModel<>(payload, "payload1")));
                item.add(new TextField<>("payload2", new PropertyModel<>(payload, "payload2")));
                item.add(new TextField<>("payload3", new PropertyModel<>(payload, "payload3")));

                item.visitChildren(new IVisitor<Component, Object>() {
                    @Override
                    public void component(Component object, IVisit<Object> visit) {
                        object.setEnabled(isCurrentOperationMonth());
                        visit.dontGoDeeper();
                    }
                });
            }
        };
        add(payloads);

        add(new WebMarkupContainer("header"){
            @Override
            public boolean isVisible() {
                return !payloads.getModelObject().isEmpty();
            }
        });

        add(new AjaxSubmitLink("add") {
            @Override
            public boolean isVisible() {
                return isCurrentOperationMonth();
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                model.getObject().getPayloads().add(new HeatmeterPayload(getFirstDayOfCurrentMonth()));

                target.add(HeatmeterPayloadPanel.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        });

        add(new AjaxSubmitLink("remove") {
            @Override
            public boolean isVisible() {
                return isCurrentOperationMonth() && !payloads.getModelObject().isEmpty();
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                List<HeatmeterPayload> list = payloads.getModelObject();
                model.getObject().getPayloads().remove(list.size() - 1);
                payloads.detachModels();

                target.add(HeatmeterPayloadPanel.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        });
    }
}
