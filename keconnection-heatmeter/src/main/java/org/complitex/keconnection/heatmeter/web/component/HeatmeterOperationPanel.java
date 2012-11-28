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
import org.complitex.dictionary.web.component.EnumDropDownChoice;
import org.complitex.dictionary.web.component.dateinput.MaskedDateInput;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriod;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodSubType;
import org.complitex.keconnection.heatmeter.service.HeatmeterOperationBean;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 15:45
 */
public class HeatmeterOperationPanel extends AbstractHeatmeterEditPanel {
    @EJB
    private HeatmeterOperationBean operationBean;

    public HeatmeterOperationPanel(String id, final IModel<Heatmeter> model, final IModel<Date> om) {
        super(id, model, om);
        setOutputMarkupId(true);

        final ListView<HeatmeterPeriod> periods = new ListView<HeatmeterPeriod>("list_view",
                new LoadableDetachableModel<List<HeatmeterPeriod>>() {
                    @Override
                    protected List<HeatmeterPeriod> load() {
                        Heatmeter heatmeter = model.getObject();

                        return new ArrayList<>();

//                        return isActiveOm()
//                                ? heatmeter.getOperations()
//                                : operationBean.getList(heatmeter.getId(), om.getObject(), HeatmeterPeriodType.OPERATION);
                    }
                }) {
            @Override
            protected void populateItem(ListItem<HeatmeterPeriod> item) {
                HeatmeterPeriod heatmeterPeriod = item.getModelObject();

                item.add(new MaskedDateInput("begin_date", new PropertyModel<Date>(heatmeterPeriod, "beginDate")));
                item.add(new MaskedDateInput("end_date", new PropertyModel<Date>(heatmeterPeriod, "endDate")));
                item.add(new EnumDropDownChoice<>("type", HeatmeterPeriodSubType.class,
                        new PropertyModel<HeatmeterPeriodSubType>(heatmeterPeriod, "type"), false));

                item.visitChildren(new IVisitor<Component, Object>() {
                    @Override
                    public void component(Component object, IVisit<Object> visit) {
                        object.setEnabled(isActiveOm());
                        visit.dontGoDeeper();
                    }
                });
            }
        };
        add(periods);

        add(new WebMarkupContainer("header"){
            @Override
            public boolean isVisible() {
                return !periods.getModelObject().isEmpty();
            }
        });

        add(new AjaxSubmitLink("add") {
            @Override
            public boolean isVisible() {
                return isActiveOm();
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
               //model.getObject().getOperations().add(new HeatmeterPeriod(operatingMonthModel.getObject()));

                target.add(HeatmeterOperationPanel.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        });

        add(new AjaxSubmitLink("remove") {
            @Override
            public boolean isVisible() {
                return isActiveOm() && !periods.getModelObject().isEmpty();
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                List<HeatmeterPeriod> list = periods.getModelObject();
                model.getObject().getOperations().remove(list.size() - 1);
                periods.detachModels();

                target.add(HeatmeterOperationPanel.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        });
    }
}
