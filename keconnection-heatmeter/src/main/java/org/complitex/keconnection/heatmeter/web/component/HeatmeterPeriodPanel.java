package org.complitex.keconnection.heatmeter.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.complitex.dictionary.web.component.DatePicker;
import org.complitex.dictionary.web.component.EnumDropDownChoice;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriod;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.complitex.dictionary.util.DateUtil.getFirstDayOfCurrentMonth;
import static org.complitex.dictionary.util.DateUtil.isSameMonth;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 15:45
 */
public class HeatmeterPeriodPanel extends AbstractHeatmeterEditPanel {
    public HeatmeterPeriodPanel(String id, final IModel<Heatmeter> model, final IModel<Date> operatingMonthModel) {
        super(id, model, operatingMonthModel);
        setOutputMarkupId(true);

        final ListView<HeatmeterPeriod> periods = new ListView<HeatmeterPeriod>("list_view",
                new LoadableDetachableModel<List<HeatmeterPeriod>>() {
                    @Override
                    protected List<HeatmeterPeriod> load() {
                        List<HeatmeterPeriod> list = new ArrayList<>();

                        for (HeatmeterPeriod p : model.getObject().getPeriods()){
                            if (isSameMonth(operatingMonthModel.getObject(), p.getOperatingMonth())){
                                list.add(p);
                            }
                        }

                        return list;
                    }
                }) {
            @Override
            protected void populateItem(ListItem<HeatmeterPeriod> item) {
                HeatmeterPeriod heatmeterPeriod = item.getModelObject();

                item.add(new DatePicker<>("begin_date", new PropertyModel<>(heatmeterPeriod, "beginDate"))
                        .setEnabled(isCurrentOperationMonth()));
                item.add(new DatePicker<>("end_date", new PropertyModel<>(heatmeterPeriod, "endDate"))
                        .setEnabled(isCurrentOperationMonth()));
                item.add(new EnumDropDownChoice<>("type", HeatmeterPeriodType.class,
                        new PropertyModel<HeatmeterPeriodType>(heatmeterPeriod, "type"), false)
                        .setEnabled(isCurrentOperationMonth()));
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
                return isCurrentOperationMonth();
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                model.getObject().getPeriods().add(new HeatmeterPeriod(getFirstDayOfCurrentMonth()));

                target.add(HeatmeterPeriodPanel.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        });

        add(new AjaxSubmitLink("remove") {
            @Override
            public boolean isVisible() {
                return isCurrentOperationMonth() && !periods.getModelObject().isEmpty();
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                List<HeatmeterPeriod> list = periods.getModelObject();
                model.getObject().getPeriods().remove(list.size() - 1);
                periods.detachModels();

                target.add(HeatmeterPeriodPanel.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        });
    }
}
