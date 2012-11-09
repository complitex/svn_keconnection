/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.component.heatmeter.list;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.web.component.EnumDropDownChoice;
import org.complitex.dictionary.web.component.dateinput.MaskedDateInput;
import org.complitex.keconnection.heatmeter.entity.*;
import org.complitex.keconnection.heatmeter.service.HeatmeterBean;
import org.complitex.keconnection.heatmeter.service.HeatmeterPeriodBean;
import org.complitex.keconnection.heatmeter.service.HeatmeterService;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public abstract class ActivateHeatmeterDialog extends Panel {

    private static class ActivateHeatmeterEntity implements Serializable {

        HeatmeterType heatmeterType;
        boolean calculating;
        Date activateDate;

        ActivateHeatmeterEntity(HeatmeterType heatmeterType, Boolean calculating, Date activateDate) {
            this.heatmeterType = heatmeterType;
            this.calculating = calculating != null ? calculating : false;
            this.activateDate = activateDate;
        }

        ActivateHeatmeterEntity() {
        }
    }
    private static final Logger log = LoggerFactory.getLogger(ActivateHeatmeterDialog.class);
    @EJB
    private HeatmeterService heatmeterService;
    @EJB
    private HeatmeterPeriodBean heatmeterPeriodBean;
    @EJB
    private HeatmeterBean heatmeterBean;
    private final Dialog dialog;
    private final WebMarkupContainer container;
    private final IModel<ActivateHeatmeterEntity> model;
    private Heatmeter heatmeter;

    public ActivateHeatmeterDialog(String id) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setWidth(400);
        dialog.setModal(true);
        add(dialog);

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        dialog.add(container);

        final FeedbackPanel messages = new FeedbackPanel("messages", new ContainerFeedbackMessageFilter(container));
        messages.setOutputMarkupId(true);
        container.add(messages);

        model = new CompoundPropertyModel<>(new ActivateHeatmeterEntity());
        Form<ActivateHeatmeterEntity> form = new Form<>("form", model);
        container.add(form);

        form.add(new Label("ls", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return heatmeter != null ? String.valueOf(heatmeter.getLs()) : null;
            }
        }));

        EnumDropDownChoice<HeatmeterType> heatmeterType =
                new EnumDropDownChoice<>("heatmeterType", HeatmeterType.class, false);
        heatmeterType.setRequired(true);
        form.add(heatmeterType);

        form.add(new CheckBox("calculating"));

        MaskedDateInput activateDate = new MaskedDateInput("activateDate");
        activateDate.setRequired(true);
        form.add(activateDate);

        form.add(new IndicatingAjaxButton("activate", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                HeatmeterPeriod period = preparePeriod(model.getObject().activateDate);

//                HeatmeterValidate validate = heatmeterService.validatePeriods(heatmeter);
                HeatmeterValidate validate = null;
                if (HeatmeterValidateStatus.VALID != validate.getStatus()) {
                    error(MessageFormat.format(getString(validate.getStatus().name().toLowerCase()), validate));
                    target.add(messages);
                } else {
                    try {
                        //save heatmeter period
                        heatmeterPeriodBean.save(period);

                        //save heatmeter info
                        copyHeatmeterInfo(model.getObject());
                        heatmeterBean.saveHeatmeterInfo(heatmeter);

                        onActivate(heatmeter, target);

                        dialog.close(target);
                    } catch (Exception e) {
                        log.error("Db error.", e);
                        error(getString("db_save_error"));
                        target.add(messages);
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }
        });
    }

    private void copyHeatmeterInfo(ActivateHeatmeterEntity info) {
        heatmeter.setCalculating(info.calculating);
        heatmeter.setType(info.heatmeterType);
    }

    private HeatmeterPeriod preparePeriod(Date beginDate) {
        //try find open ADJUSTMENT period
//        Optional<HeatmeterPeriod> openAdjustmentPeriod = Iterables.tryFind(heatmeter.getPeriods(),
//                new Predicate<HeatmeterPeriod>() {
//
//                    @Override
//                    public boolean apply(HeatmeterPeriod period) {
//                        return period.getEndDate() == null && period.getType() == HeatmeterPeriodSubType.ADJUSTMENT;
//                    }
//                });
//        if (openAdjustmentPeriod.isPresent()) {
//            HeatmeterPeriod adjustmentPeriod = openAdjustmentPeriod.get();
//            adjustmentPeriod.setEndDate(beginDate);
//            return adjustmentPeriod;
//        } else {
//            //add new operational period
//            HeatmeterPeriod operationalPeriod = new HeatmeterPeriod(heatmeterListWrapper.getOperatingMonthDate());
//            operationalPeriod.setBeginDate(beginDate);
//            operationalPeriod.setType(HeatmeterPeriodSubType.OPERATING);
//            operationalPeriod.setHeatmeterId(heatmeter.getId());
//            heatmeter.getPeriods().add(operationalPeriod);
//            return operationalPeriod;
//        }

        return null;
    }

    public void open(Heatmeter heatmeter, AjaxRequestTarget target) {
        this.heatmeter = heatmeter;

        model.setObject(new ActivateHeatmeterEntity(heatmeter.getType(), heatmeter.getCalculating(),
                DateUtil.getCurrentDate()));

        target.add(container);
        dialog.open(target);
    }

    protected abstract void onActivate(Heatmeter heatmeter, AjaxRequestTarget target);
}
