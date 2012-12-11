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
import org.complitex.dictionary.web.component.LabelEnumDropDownChoice;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterOperation;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodSubType;
import org.complitex.keconnection.heatmeter.service.HeatmeterOperationBean;

import javax.ejb.EJB;
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

        final IModel<List<HeatmeterOperation>> operationsModel = new LoadableDetachableModel<List<HeatmeterOperation>>() {

            @Override
            protected List<HeatmeterOperation> load() {
                Heatmeter heatmeter = model.getObject();

                return isActiveOm()
                        ? heatmeter.getOperations()
                        : operationBean.getHeatmeterOperations(heatmeter.getId(), om.getObject());
            }
        };

        final ListView<HeatmeterOperation> operations = new ListView<HeatmeterOperation>("list_view", operationsModel) {

            @Override
            protected void populateItem(ListItem<HeatmeterOperation> item) {
                final HeatmeterOperation operation = item.getModelObject();

                item.add(new LabelDateField("begin_date", new PropertyModel<Date>(operation, "beginDate"), false));
                item.add(new LabelDateField("end_date", new PropertyModel<Date>(operation, "endDate"), false));

                item.add(new LabelEnumDropDownChoice<>("type", HeatmeterPeriodSubType.class,
                        new PropertyModel<HeatmeterPeriodSubType>(operation, "subType"), false));

                item.add(new AjaxSubmitLink("remove") {

                    @Override
                    public boolean isVisible() {
                        return isActiveOm() && !operationsModel.getObject().isEmpty();
                    }

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        model.getObject().getOperations().remove(operation);
                        target.add(HeatmeterOperationPanel.this);
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
        add(operations);

        add(new WebMarkupContainer("header") {

            @Override
            public boolean isVisible() {
                return !operationsModel.getObject().isEmpty();
            }
        });

        add(new AjaxSubmitLink("add") {

            @Override
            public boolean isVisible() {
                return isActiveOm();
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                HeatmeterOperation operation = new HeatmeterOperation();
                operation.setHeatmeterId(model.getObject().getId());
                operation.setBeginOm(om.getObject());
                model.getObject().getOperations().add(operation);

                target.add(HeatmeterOperationPanel.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        });
    }
}
