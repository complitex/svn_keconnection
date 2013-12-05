/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.component.heatmeter.list;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
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
import org.complitex.keconnection.heatmeter.service.HeatmeterOperationBean;
import org.complitex.keconnection.heatmeter.service.HeatmeterService;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;

import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodSubType.ADJUSTMENT;

/**
 *
 * @author Artem
 */
public abstract class DeactivateHeatmeterDialog extends Panel {

    private static class DeactivateHeatmeterEntity implements Serializable {

        HeatmeterPeriodSubType deactivateType;
        Date deactivateDate;

        DeactivateHeatmeterEntity(HeatmeterPeriodSubType deactivateType, Date deactivateDate) {
            this.deactivateType = deactivateType;
            this.deactivateDate = deactivateDate;
        }

        DeactivateHeatmeterEntity() {
        }
    }
    private final Logger log = LoggerFactory.getLogger(DeactivateHeatmeterDialog.class);

    @EJB
    private HeatmeterService heatmeterService;

    @EJB
    private HeatmeterOperationBean operationBean;

    private final Dialog dialog;
    private final WebMarkupContainer container;
    private final IModel<DeactivateHeatmeterEntity> model;
    private Heatmeter heatmeter;

    public DeactivateHeatmeterDialog(String id) {
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

        model = new CompoundPropertyModel<>(new DeactivateHeatmeterEntity());
        Form<DeactivateHeatmeterEntity> form = new Form<>("form", model);
        container.add(form);

        form.add(new Label("ls", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return heatmeter != null ? String.valueOf(heatmeter.getLs()) : null;
            }
        }));

        EnumDropDownChoice<HeatmeterPeriodSubType> deactivateType =
                new EnumDropDownChoice<>("deactivateType", HeatmeterPeriodSubType.class, false);
        deactivateType.setRequired(true);
        form.add(deactivateType);

        MaskedDateInput deactivateDate = new MaskedDateInput("deactivateDate");
        deactivateDate.setRequired(true);
        form.add(deactivateDate);

        form.add(new IndicatingAjaxButton("deactivate", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                HeatmeterOperation period = preparePeriod(model.getObject());

                //TODO: fix validation:
                HeatmeterValidate validate = new HeatmeterValidate(HeatmeterValidateStatus.VALID); //heatmeterService.validatePeriods(heatmeter);
                if (HeatmeterValidateStatus.VALID != validate.getStatus()) {
                    error(MessageFormat.format(getString(validate.getStatus().name().toLowerCase()), validate));
                    target.add(messages);
                } else {
                    try {
                        //save heatmeter period
                        operationBean.save(period, heatmeter.getOm());

                        onDeactivate(heatmeter, target);

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

    private HeatmeterOperation preparePeriod(DeactivateHeatmeterEntity info) {
        if (info.deactivateType == HeatmeterPeriodSubType.OPERATING) {
            HeatmeterOperation lastOperationalPeriod = Iterables.find(heatmeter.getOperations(),
                    new Predicate<HeatmeterPeriod>() {

                        @Override
                        public boolean apply(HeatmeterPeriod period) {
                            return period.getSubType() == HeatmeterPeriodSubType.OPERATING
                                    && HeatmeterPeriod.DEFAULT_END_DATE.equals(period.getEndDate());
                        }
                    });
            lastOperationalPeriod.setEndDate(info.deactivateDate);
            lastOperationalPeriod.setEndOm(heatmeter.getOm());

            return lastOperationalPeriod;
        } else if (info.deactivateType == ADJUSTMENT) {
            HeatmeterOperation adjustmentPeriod = new HeatmeterOperation(heatmeter.getId(), heatmeter.getOm(), ADJUSTMENT);
            adjustmentPeriod.setBeginDate(info.deactivateDate);
            heatmeter.getOperations().add(adjustmentPeriod);
            return adjustmentPeriod;
        } else {
            throw new IllegalStateException("Unknown heatmeter period type: " + info.deactivateType);
        }
    }

    public void open(Heatmeter heatmeter, AjaxRequestTarget target) {
        this.heatmeter = heatmeter;

        model.setObject(new DeactivateHeatmeterEntity(ADJUSTMENT, DateUtil.getCurrentDate()));

        target.add(container);
        dialog.open(target);
    }

    protected abstract void onDeactivate(Heatmeter heatmeter, AjaxRequestTarget target);
}
