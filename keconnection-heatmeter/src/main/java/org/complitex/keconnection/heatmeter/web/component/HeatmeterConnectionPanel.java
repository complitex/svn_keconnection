package org.complitex.keconnection.heatmeter.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.web.component.dateinput.MaskedDateInput;
import org.complitex.keconnection.address.strategy.building.KeConnectionBuildingStrategy;
import org.complitex.keconnection.address.strategy.building.entity.BuildingCode;
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

    @EJB
    private KeConnectionBuildingStrategy buildingStrategy;

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
                item.add(new MaskedDateInput("begin_date", new PropertyModel<Date>(connection, "beginDate")).setRequired(true));
                item.add(new MaskedDateInput("end_date", new PropertyModel<Date>(connection, "endDate")));

                //organization
                final Label organization = new Label("organization", new LoadableDetachableModel<String>() {
                    @Override
                    protected String load() {
                        String s = getString("organization_not_found");

                        if (connection.getOrganizationId() != null) {
                            DomainObject domainObject = organizationStrategy.findById(connection.getOrganizationId(), true);
                            s = organizationStrategy.displayDomainObject(domainObject, getLocale());
                        }

                        return s;
                    }
                });
                organization.setOutputMarkupId(true);
                item.add(organization);

                //address
                final Label address = new Label("address", new LoadableDetachableModel<Object>() {
                    @Override
                    protected Object load() {
                        String s = getString("address_not_found");
                        if (connection.getBuildingId() != null){
                            s = addressRendererBean.displayBuildingSimple(connection.getBuildingId(), getLocale());
                        }

                        return s;
                    }
                });
                address.setOutputMarkupId(true);
                item.add(address);

                //address code
                final TextField code = new TextField<>("code", new Model<String>(){
                    @Override
                    public String getObject() {
                        String s = super.getObject();

                        if (s == null && connection.getCode() != null){
                            s = connection.getCode().toString();
                        }

                        return s;
                    }

                    @Override
                    public void setObject(String object) {
                        super.setObject(object);

                        Long buildingCodeId = buildingStrategy.getBuildingCodeId(connection.getOrganizationId(), object);

                        connection.setBuildingCodeId(buildingCodeId);

                        if (buildingCodeId != null){
                            BuildingCode buildingCode = buildingStrategy.getBuildingCodeById(buildingCodeId);
                            connection.setBuildingId(buildingCode.getBuildingId());
                            connection.setCode(buildingCode.getBuildingCode());
                        }else {
                            connection.setBuildingId(null);
                            connection.setCode(null);
                        }
                    }
                });
                code.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        target.add(address);
                    }
                });
                code.setRequired(true);
                code.setOutputMarkupId(true);
                item.add(code);

                //organization code
                TextField organizationCode = new TextField<>("organization_code", new Model<String>() {
                    @Override
                    public void setObject(String object) {
                        super.setObject(object);

                        Long organizationId = organizationStrategy.getObjectId(object);

                        connection.setOrganizationId(organizationId);

                        if (organizationId == null){
                            connection.setBuildingCodeId(null);
                            connection.setBuildingId(null);
                            connection.setCode(null);
                        }
                    }

                    @Override
                    public String getObject() {
                        String s = super.getObject();

                        if (s == null && connection.getOrganizationId() != null) {
                            s = organizationStrategy.getUniqueCode(connection.getOrganizationId());
                        }

                        return s;
                    }
                });
                organizationCode.add(new AjaxFormComponentUpdatingBehavior("onchange") {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        target.add(organization);
                        target.add(address);
                        target.add(code);
                    }
                });
                organizationCode.setRequired(true);
                item.add(organizationCode);

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

                item.visitChildren(new IVisitor<Component, Object>() {
                    @Override
                    public void component(Component object, IVisit<Object> visit) {
                        object.setEnabled(isCurrentOperationMonth());
                        visit.dontGoDeeper();
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
