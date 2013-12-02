package org.complitex.keconnection.heatmeter.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.BuildingCode;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.dictionary.web.component.LabelDateField;
import org.complitex.dictionary.web.component.LabelTextField;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConnection;
import org.complitex.keconnection.heatmeter.service.HeatmeterConnectionBean;
import org.complitex.keconnection.organization.strategy.KeConnectionOrganizationStrategy;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 16:24
 */
public class HeatmeterConnectionPanel extends AbstractHeatmeterEditPanel {
    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeConnectionOrganizationStrategy organizationStrategy;

    @EJB
    private AddressRendererBean addressRendererBean;

    @EJB
    private BuildingStrategy buildingStrategy;

    @EJB
    private HeatmeterConnectionBean connectionBean;

    public HeatmeterConnectionPanel(String id, final IModel<Heatmeter> model, final IModel<Date> om) {
        super(id, model, om);

        setOutputMarkupId(true);

        final ListView<HeatmeterConnection> connections = new ListView<HeatmeterConnection>("list_view",
                new LoadableDetachableModel<List<HeatmeterConnection>>() {
                    @Override
                    protected List<HeatmeterConnection> load() {
                        Heatmeter heatmeter = model.getObject();

                        return isActiveOm()
                                ? heatmeter.getConnections()
                                : connectionBean.getList(heatmeter.getId(), om.getObject());
                    }
                }) {
            @Override
            protected void populateItem(ListItem<HeatmeterConnection> item) {
                final HeatmeterConnection connection = item.getModelObject();

                //date
                item.add(new LabelDateField("begin_date", new PropertyModel<Date>(connection, "beginDate"), false));
                item.add(new LabelDateField("end_date", new PropertyModel<Date>(connection, "endDate"), false));

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
                final Component code = new LabelTextField<>("code", 4, new Model<String>(){
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

                        connection.setObjectId(buildingCodeId);

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
                code.setOutputMarkupId(true);
                item.add(code);

                //organization code
                Component organizationCode = new LabelTextField<>("organization_code", 4, new Model<String>() {
                    @Override
                    public void setObject(String object) {
                        super.setObject(object);

                        Long organizationId = organizationStrategy.getObjectIdByCode(object);

                        connection.setOrganizationId(organizationId);

                        if (organizationId == null){
                            connection.setObjectId(null);
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

                    @Override
                    public boolean isVisible() {
                        return isActiveOm();
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
                return isActiveOm();
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                HeatmeterConnection connection = new HeatmeterConnection();
                connection.setHeatmeterId(model.getObject().getId());
                connection.setBeginOm(om.getObject());

                model.getObject().getConnections().add(connection);

                target.add(HeatmeterConnectionPanel.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        });
    }
}
