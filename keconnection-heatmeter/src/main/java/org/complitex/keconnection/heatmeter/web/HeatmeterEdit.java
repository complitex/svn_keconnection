package org.complitex.keconnection.heatmeter.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.dictionary.service.exception.AbstractException;
import org.complitex.dictionary.web.component.EnumDropDownChoice;
import org.complitex.keconnection.address.strategy.building.KeConnectionBuildingStrategy;
import org.complitex.keconnection.address.strategy.building.entity.BuildingCode;
import org.complitex.keconnection.heatmeter.entity.*;
import org.complitex.keconnection.heatmeter.service.HeatmeterBean;
import org.complitex.keconnection.heatmeter.service.HeatmeterService;
import org.complitex.keconnection.heatmeter.web.component.HeatmeterConnectionPanel;
import org.complitex.keconnection.heatmeter.web.component.HeatmeterInputPanel;
import org.complitex.keconnection.heatmeter.web.component.HeatmeterOperationPanel;
import org.complitex.keconnection.heatmeter.web.component.HeatmeterPayloadPanel;
import org.complitex.keconnection.heatmeter.web.correction.component.HeatmeterCorrectionDialog;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;

import static org.complitex.dictionary.util.DateUtil.*;
import static org.complitex.keconnection.heatmeter.entity.HeatmeterValidateStatus.VALID;
import static org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy.KE_ORGANIZATION_OBJECT_ID;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.09.12 15:25
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class HeatmeterEdit extends FormTemplatePage {

    private final static Logger log = LoggerFactory.getLogger(HeatmeterEdit.class);
    @EJB
    private HeatmeterBean heatmeterBean;
    @EJB
    private HeatmeterService heatmeterService;
    @EJB(name = IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME)
    private IKeConnectionOrganizationStrategy organizationStrategy;
    @EJB
    private KeConnectionBuildingStrategy buildingStrategy;
    private IModel<Date> om = new Model<>();
    private Date minOm = null;

    public HeatmeterEdit() {
        init(null);
    }

    public HeatmeterEdit(PageParameters pageParameters) {
        init(pageParameters.get("id").toLongObject());
    }

    private void init(Long id) {
        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        Heatmeter heatmeter;

        if (id != null) {
            heatmeter = heatmeterBean.getHeatmeter(id);
            om.setObject(heatmeter.getOm());
            minOm = heatmeterBean.getMinOm(heatmeter.getId());
        } else {
            heatmeter = new Heatmeter();
            heatmeter.setOrganizationId(KE_ORGANIZATION_OBJECT_ID);
        }

        final IModel<Heatmeter> model = new CompoundPropertyModel<>(heatmeter);

        Form form = new Form<>("form", model);
        add(form);

        //Ls
        form.add(new TextField<>("ls"));

        //Type
        form.add(new EnumDropDownChoice<>("type", HeatmeterType.class, false));

        //Calculating
        form.add(new CheckBox("calculating"));

        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        form.add(container);

        //Operating month
        final WebMarkupContainer omContainer = new WebMarkupContainer("om_container") {

            @Override
            public boolean isVisible() {
                return om.getObject() != null;
            }
        };
        omContainer.setOutputMarkupId(true);
        container.add(omContainer);

        omContainer.add(new Label("current_operation_month", om));

        omContainer.add(new AjaxLink("previous_month") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                om.setObject(addMonth(om.getObject(), -1));

                target.add(container);
            }

            @Override
            public boolean isEnabled() {
                return minOm != null && om.getObject().after(minOm);
            }
        });

        omContainer.add(new AjaxLink("next_month") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                om.setObject(addMonth(om.getObject(), 1));

                target.add(container);
            }

            @Override
            public boolean isEnabled() {
                return om.getObject().before(model.getObject().getOm());
            }
        });

        //Operation
        container.add(new HeatmeterOperationPanel("operations", model, om));

        //Connection
        container.add(new HeatmeterConnectionPanel("connections", model, om));

        //Payloads
        container.add(new HeatmeterPayloadPanel("payloads", model, om));

        //Input
        container.add(new HeatmeterInputPanel("inputs", model, om));

        //Save
        form.add(new Button("save") {

            @Override
            public void onSubmit() {
                try {
                    Heatmeter heatmeter = model.getObject();

                    //validate
                    HeatmeterValidate validate = heatmeterService.validate(heatmeter);

                    if (!VALID.equals(validate.getStatus())) {
                        error(getStringFormat(validate.getStatus().name().toLowerCase(), validate));

                        return;
                    }

                    //update om for new heatmeter
                    if (heatmeter.getId() == null) {
                        HeatmeterConnection connection = heatmeter.getConnections().get(0);
                        BuildingCode buildingCode = buildingStrategy.getBuildingCodeById(connection.getObjectId());

                        Date om = organizationStrategy.getOperatingMonthDate(buildingCode.getOrganizationId());

                        if (om == null) {
                            error(getStringFormat("error_om_not_found", buildingCode.getBuildingCode()));
                            return;
                        }

                        heatmeter.setOm(om);

                        //update om for periods
                        List<HeatmeterPeriod> periods = heatmeter.getPeriods();
                        for (HeatmeterPeriod p : periods) {
                            p.setBeginOm(om);
                        }
                    }

                    //adjust begin date for previous inputs
                    {
                        int size = heatmeter.getInputs().size();
                        HeatmeterInput prev = null;
                        for (HeatmeterInput input : heatmeter.getInputs()) {
                            if (prev != null) {
                                input.setBeginDate(nextDay(prev.getEndDate()));
                            } else if (heatmeter.getId() == null || size == 1) {
                                input.setBeginDate(heatmeter.getOm());
                            }
                            prev = input;
                        }
                    }

                    //recalculate consumptions for inputs
                    {
                        heatmeterService.calculateConsumptions(heatmeter.getPayloads(), heatmeter.getInputs());
                    }

                    //save
                    heatmeterBean.save(heatmeter, heatmeter.getOm());

                    getSession().info(getStringFormat("info_saved", heatmeter.getLs()));
                } catch (Exception e) {
                    log.error("Ошибка сохранения теплосчетчика", e);
                    getSession().error(new AbstractException(e, "Ошибка сохранения теплосчетчика: {0}", model.getObject().getLs()) {
                    });
                }

                setResponsePage(HeatmeterList.class);
            }
        });

        form.add(new Link("cancel") {

            @Override
            public void onClick() {
                setResponsePage(HeatmeterList.class);
            }
        });

        final HeatmeterCorrectionDialog heatmeterCorrectionDialog =
                new HeatmeterCorrectionDialog("heatmeterCorrectionDialog", heatmeter);
        add(heatmeterCorrectionDialog);
        AjaxLink<Void> correctionsLink = new AjaxLink<Void>("correctionsLink") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                heatmeterCorrectionDialog.open(target);
            }
        };
        correctionsLink.setVisible(heatmeterCorrectionDialog.isVisible());
        form.add(correctionsLink);
    }
}
