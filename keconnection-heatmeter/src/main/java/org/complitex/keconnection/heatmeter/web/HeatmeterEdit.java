package org.complitex.keconnection.heatmeter.web;

import com.google.common.collect.Range;
import com.google.common.collect.Ranges;
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
import org.complitex.keconnection.heatmeter.entity.*;
import org.complitex.keconnection.heatmeter.service.HeatmeterBean;
import org.complitex.keconnection.heatmeter.service.HeatmeterImportService;
import org.complitex.keconnection.heatmeter.service.HeatmeterPeriodBean;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.complitex.dictionary.util.DateUtil.*;
import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.ADJUSTMENT;
import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.OPERATION;
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

    @EJB(name = IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME)
    private IKeConnectionOrganizationStrategy organizationStrategy;

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
                return false;  //todo
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
                        HeatmeterPeriod period = new HeatmeterPeriod();
                        period.setHeatmeterId(heatmeter.getId());
                        period.setType(OPERATION);
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

    private boolean validateHeatmeter(Heatmeter heatmeter){
        //validate period
        /*Периодов как функционирования, так и юстировки может быть
        несколько, но однотипные периоды не могут пересекаться, а периоды юстировки должны полностью принадлежать
        периоду функционирования*/

        boolean hasOpenOperation = false;
        boolean hasOpenAdjustment = false;

        for (HeatmeterPeriod period : heatmeter.getPeriods()){
            if (period.getBeginDate() != null && period.getEndDate() != null && period.getBeginDate().after(period.getEndDate())){
                error(getStringFormat("error_period_begin_date_after_end_date", period.getBeginDate(), period.getEndDate()));
                return false;
            }

            if (period.getBeginDate() == null){
                error(getString("error_period_begin_date_required"));
                return false;
            }

            if (period.getType() == null){
                error(getString("error_period_type_required"));
                return false;
            }else {
                if (period.getEndDate() == null){
                    //Одновременно без даты окончания может быть не более двух периодов: один период функционирования
                    //и какой либо другой (пока возможен только период юстировки).
                    if (OPERATION.equals(period.getType())){
                        if (hasOpenOperation){
                            error(getStringFormat("error_period_more_than_two_open_operation", period.getBeginDate()));
                            return false;
                        }

                        hasOpenOperation = true;
                    }else if (ADJUSTMENT.equals(period.getType())){
                        if (hasOpenAdjustment){
                            error(getStringFormat("error_period_more_than_two_open_adjustment", period.getBeginDate()));
                            return false;
                        }
                        hasOpenAdjustment = true;
                    }
                }
            }
        }

        //однотипные периоды не могут пересекаться
        List<HeatmeterPeriod> periods = heatmeter.getPeriods();
        for (int i = 0; i < periods.size() - 1 ; i++) {
            HeatmeterPeriod p1 = periods.get(i);

            for (int j = i+1; j < periods.size(); j++) {
                HeatmeterPeriod p2 = periods.get(j);

                if (p1.getType() != null && p1.getType().equals(p2.getType())){
                    Range<Date> r1 = p1.getEndDate() != null
                            ? Ranges.closed(p1.getBeginDate(), p1.getEndDate())
                            : Ranges.atLeast(p1.getBeginDate());

                    Range<Date> r2 = p2.getEndDate() != null
                            ? Ranges.closed(p2.getBeginDate(), p2.getEndDate())
                            : Ranges.atLeast(p2.getBeginDate());

                    if (r1.isConnected(r2)){
                        error(getStringFormat("error_period_intersection",
                                format(p1.getBeginDate()), format(p1.getEndDate()),
                                format(p2.getBeginDate()), format(p2.getEndDate())));
                        return false;
                    }
                }
            }
        }

        //validate connection
        for (HeatmeterConnection connection : heatmeter.getConnections()){
            if (connection.getBeginDate() == null){
                error(getString("error_connection_begin_date_required"));
                return false;
            }

            if (connection.getBuildingCodeId() == null){
                error(getString("error_connection_not_found"));
                return false;
            }
        }

        //validate payload
        for (HeatmeterPayload payload : heatmeter.getPayloads()){
            if (payload.getBeginDate() == null){
                error(getString("error_payload_begin_date_required"));
                return false;
            }

            if (payload.getPayload1() == null || payload.getPayload2() == null || payload.getPayload3() == null){
                error(getString("error_payload_values_required"));
                return false;
            }
        }

        //validate consumption
        for(HeatmeterConsumption consumption : heatmeter.getConsumptions()){
            if (consumption.getReadoutDate() == null) {
                error(getString("error_consumption_readout_date_required"));
                return false;
            }

            if (consumption.getConsumption() == null){
                error(getString("error_consumption_value_required"));
                return false;
            }
        }

        return true;
    }
}
