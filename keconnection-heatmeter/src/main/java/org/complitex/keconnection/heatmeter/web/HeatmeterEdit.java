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
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.dictionary.service.exception.AbstractException;
import org.complitex.dictionary.web.component.EnumDropDownChoice;
import org.complitex.keconnection.address.strategy.building.KeConnectionBuildingStrategy;
import org.complitex.keconnection.heatmeter.entity.*;
import org.complitex.keconnection.heatmeter.service.HeatmeterBean;
import org.complitex.keconnection.heatmeter.service.HeatmeterImportService;
import org.complitex.keconnection.heatmeter.service.HeatmeterPeriodBean;
import org.complitex.keconnection.heatmeter.web.component.*;
import org.complitex.keconnection.heatmeter.web.correction.component.HeatmeterCorrectionDialog;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Date;

import static org.complitex.dictionary.util.DateUtil.*;
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
    private HeatmeterPeriodBean heatmeterPeriodBean;

    @EJB
    private KeConnectionBuildingStrategy buildingStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private CityStrategy cityStrategy;



    public final static Date DEFAULT_BEGIN_DATE = newDate(1, 10, 2012);

    private IModel<Date> operatingMonthModel = Model.of(getFirstDayOfCurrentMonth());

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
        }
        else{
            heatmeter = new Heatmeter();
            heatmeter.setConnections(new ArrayList<HeatmeterConnection>());
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
        container.add(new Label("current_operation_month", operatingMonthModel));

        container.add(new AjaxLink("previous_month") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                operatingMonthModel.setObject(addMonth(operatingMonthModel.getObject(), -1));

                target.add(container);
            }

            @Override
            public boolean isEnabled() {
                return DEFAULT_BEGIN_DATE.before(operatingMonthModel.getObject());
            }
        });

        container.add(new AjaxLink("next_month") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                operatingMonthModel.setObject(addMonth(operatingMonthModel.getObject(), 1));

                target.add(container);
            }

            @Override
            public boolean isEnabled() {
                return getCurrentDate().after(operatingMonthModel.getObject());
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

        //Heatmeter Code Panel
        form.add(new HeatmeterCodePanel("heatmeter_codes", new ListModel<>(heatmeter.getConnections())));

        //Save
        form.add(new Button("save"){
            @Override
            public void onSubmit() {
                try {
                    Heatmeter heatmeter = model.getObject();

                    //validate
                    for (HeatmeterConnection code : heatmeter.getConnections()){
                        if (code.getBuildingCodeId() == null){
                            error(getString("error_code_not_found"));
                        }
                    }

                    //save
                    heatmeterBean.save(heatmeter);

                    if (heatmeter.getId() == null){
                        heatmeter.setType(HeatmeterType.HEATING);

                        //create period
                        HeatmeterPeriod period = new HeatmeterPeriod();
                        period.setHeatmeterId(heatmeter.getId());
                        period.setType(HeatmeterPeriodType.OPERATION);
                        period.setBeginDate(HeatmeterImportService.DEFAULT_BEGIN_DATE);
                        period.setOperatingMonth(HeatmeterImportService.DEFAULT_BEGIN_DATE);

                        heatmeterPeriodBean.save(period);
                        heatmeterPeriodBean.updateParent(period.getId(), period.getId());
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
}
