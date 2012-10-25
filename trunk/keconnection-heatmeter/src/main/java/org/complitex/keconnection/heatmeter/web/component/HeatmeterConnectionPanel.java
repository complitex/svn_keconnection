package org.complitex.keconnection.heatmeter.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.web.component.DatePicker;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConnection;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.complitex.dictionary.util.DateUtil.getFirstDayOfCurrentMonth;
import static org.complitex.dictionary.util.DateUtil.isSameMonth;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 16:24
 */
public class HeatmeterConnectionPanel extends AbstractHeatmeterEditPanel {
    @EJB(name = IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME)
    private IKeConnectionOrganizationStrategy organizationStrategy;

    @EJB
    private AddressRendererBean addressRendererBean;

    public HeatmeterConnectionPanel(String id, final IModel<Heatmeter> model, final IModel<Date> operatingMonthModel) {
        super(id, model, operatingMonthModel);

        setOutputMarkupId(true);

        final ListView<HeatmeterConnection> connections = new ListView<HeatmeterConnection>("list_view",
                new LoadableDetachableModel<List<HeatmeterConnection>>() {
                    @Override
                    protected List<HeatmeterConnection> load() {
                        List<HeatmeterConnection> list = new ArrayList<>();

                        for (HeatmeterConnection c : model.getObject().getConnections()){
                            if (isSameMonth(c.getOperatingMonth(), operatingMonthModel.getObject())){
                                list.add(c);
                            }
                        }

                        return list;
                    }
                }) {
            @Override
            protected void populateItem(ListItem<HeatmeterConnection> item) {
                final HeatmeterConnection connection = item.getModelObject();

                //date
                item.add(new DatePicker<>("begin_date", new PropertyModel<>(connection, "beginDate"))
                        .setEnabled(isCurrentOperationMonth()));
                item.add(new DatePicker<>("end_date", new PropertyModel<>(connection, "endDate"))
                        .setEnabled(isCurrentOperationMonth()));

                //organization
                String organization = "";
                if (connection.getOrganizationId() != null) {
                    DomainObject domainObject = organizationStrategy.findById(connection.getOrganizationId(), true);
                    organization = organizationStrategy.displayDomainObject(domainObject, getLocale());
                }

                item.add(new Label("organization", organization));

                //code
                item.add(new TextField<>("code", new PropertyModel<>(connection, "code")));

                //address
                String address = "";
                if (connection.getBuildingId() != null){
                    address = addressRendererBean.displayBuildingSimple(connection.getBuildingId(), getLocale());
                }

                item.add(new Label("address", address));

                //remove
                item.add(new AjaxSubmitLink("remove") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        model.getObject().getConnections().remove(connection);

                        target.add(HeatmeterConnectionPanel.this);
                    }

                    @Override
                    protected void onError(AjaxRequestTarget target, Form<?> form) {
                    }
                });
            }
        };
        add(connections);

        add(new WebMarkupContainer("header"){
            @Override
            public boolean isVisible() {
                return !connections.getModelObject().isEmpty();
            }
        });

        add(new AjaxSubmitLink("add") {
            @Override
            public boolean isVisible() {
                return isCurrentOperationMonth();
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                model.getObject().getConnections().add(new HeatmeterConnection(getFirstDayOfCurrentMonth()));

                target.add(HeatmeterConnectionPanel.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        });
    }
}
