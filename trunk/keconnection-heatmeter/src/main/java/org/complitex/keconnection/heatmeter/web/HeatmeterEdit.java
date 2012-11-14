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
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterType;
import org.complitex.keconnection.heatmeter.entity.HeatmeterValidate;
import org.complitex.keconnection.heatmeter.service.HeatmeterBean;
import org.complitex.keconnection.heatmeter.service.HeatmeterPeriodBean;
import org.complitex.keconnection.heatmeter.service.HeatmeterService;
import org.complitex.keconnection.heatmeter.web.component.HeatmeterConnectionPanel;
import org.complitex.keconnection.heatmeter.web.component.HeatmeterConsumptionPanel;
import org.complitex.keconnection.heatmeter.web.component.HeatmeterPayloadPanel;
import org.complitex.keconnection.heatmeter.web.component.HeatmeterPeriodPanel;
import org.complitex.keconnection.heatmeter.web.correction.component.HeatmeterCorrectionDialog;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Date;

import static org.complitex.dictionary.util.DateUtil.addMonth;
import static org.complitex.keconnection.heatmeter.entity.HeatmeterValidateStatus.VALID;
import static org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy.KE_ORGANIZATION_OBJECT_ID;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.09.12 15:25
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class HeatmeterEdit extends FormTemplatePage{
    private final static Logger log = LoggerFactory.getLogger(HeatmeterEdit.class);

    @EJB
    private HeatmeterBean heatmeterBean;

    @EJB
    private HeatmeterService heatmeterService;

    @EJB
    private HeatmeterPeriodBean heatmeterPeriodBean;

    @EJB(name = IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME)
    private IKeConnectionOrganizationStrategy organizationStrategy;

    private IModel<Date> operatingMonthModel = Model.of(new Date());


    public HeatmeterEdit() {
        init(null);
    }

    public HeatmeterEdit(PageParameters pageParameters) {
        init(pageParameters.get("id").toLongObject());
    }

    private void init(Long id){
        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        Heatmeter heatmeter;

        if (id != null){
            heatmeter = heatmeterBean.getHeatmeter(id);
            operatingMonthModel.setObject(heatmeter.getOperatingMonth());
        }
        else{
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
        WebMarkupContainer omContainer = new WebMarkupContainer("om_container");
        omContainer.setOutputMarkupId(true);
        container.add(omContainer);

        omContainer.add(new Label("current_operation_month", operatingMonthModel));

        omContainer.add(new AjaxLink("previous_month") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                operatingMonthModel.setObject(addMonth(operatingMonthModel.getObject(), -1));

                target.add(container);
            }

            @Override
            public boolean isEnabled() {
                return false;  //todo
            }
        });

        omContainer.add(new AjaxLink("next_month") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                operatingMonthModel.setObject(addMonth(operatingMonthModel.getObject(), 1));

                target.add(container);
            }

            @Override
            public boolean isEnabled() {
                return false; //todo
            }
        });

        //Periods
        container.add(new HeatmeterPeriodPanel("periods", model, operatingMonthModel));

        //Connection
        container.add(new HeatmeterConnectionPanel("connections", model, operatingMonthModel));

        //Payloads
        container.add(new HeatmeterPayloadPanel("payloads", model, operatingMonthModel));

        //Consumption
        container.add(new HeatmeterConsumptionPanel("consumption", model, operatingMonthModel));

        //Save
        form.add(new Button("save"){
            @Override
            public void onSubmit() {
                try {
                    Heatmeter heatmeter = model.getObject();

                    //validate
                    if (!validateHeatmeter(heatmeter)){
                        return;
                    }

                    //save
                    heatmeterBean.save(heatmeter);

                    if (heatmeter.getId() == null){
                        heatmeter.setType(HeatmeterType.HEATING);

                        //create period
//                        HeatmeterPeriod period = new HeatmeterPeriod();
//                        period.setHeatmeterId(heatmeter.getId());
//                        period.setType(OPERATING);
//                        period.setBeginDate(HeatmeterImportService.DEFAULT_BEGIN_DATE);
//                        period.setOm(HeatmeterImportService.DEFAULT_BEGIN_DATE);

//                        heatmeterPeriodBean.save(period);
//                        heatmeterPeriodBean.updateParent(period.getId(), period.getId());
                    }

                    getSession().info(getStringFormat("info_saved", heatmeter.getLs()));
                } catch (Exception e) {
                    log.error("Ошибка сохранения теплосчетчика", e);
                    getSession().error(new AbstractException(e, "Ошибка сохранения теплосчетчика: {0}", model.getObject().getLs()) {});
                }

                setResponsePage(HeatmeterList.class);
            }
        });

        form.add(new Link("cancel"){
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

    private boolean validateHeatmeter(Heatmeter heatmeter){
        HeatmeterValidate validate = heatmeterService.validate(heatmeter);

        if (!VALID.equals(validate.getStatus())){
            error(getStringFormat(validate.getStatus().name().toLowerCase(), validate));

            return false;
        }

        return true;
    }
}
