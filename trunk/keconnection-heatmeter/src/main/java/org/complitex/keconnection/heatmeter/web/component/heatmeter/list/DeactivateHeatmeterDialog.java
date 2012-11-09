/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.component.heatmeter.list;

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
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodSubType;
import org.complitex.keconnection.heatmeter.service.HeatmeterPeriodBean;
import org.complitex.keconnection.heatmeter.service.HeatmeterService;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.Serializable;
import java.util.Date;

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
    private static final Logger log = LoggerFactory.getLogger(DeactivateHeatmeterDialog.class);
    @EJB
    private HeatmeterService heatmeterService;
    @EJB
    private HeatmeterPeriodBean heatmeterPeriodBean;
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
//                HeatmeterPeriod period = preparePeriod(model.getObject());

//                HeatmeterValidate validate = heatmeterService.validatePeriods(heatmeter);
//                if (HeatmeterValidateStatus.VALID != validate.getStatus()) {
//                    error(MessageFormat.format(getString(validate.getStatus().name().toLowerCase()), validate));
//                    target.add(messages);
//                } else {
//                    try {
//                        //save heatmeter period
//                        heatmeterPeriodBean.save(period);
//
//                        onDeactivate(heatmeter, target);
//
//                        dialog.close(target);
//                    } catch (Exception e) {
//                        log.error("Db error.", e);
//                        error(getString("db_save_error"));
//                        target.add(messages);
//                    }
//                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }
        });
    }

//    private HeatmeterPeriod preparePeriod(DeactivateHeatmeterEntity info) {
//        final Heatmeter heatmeter = heatmeterListWrapper.getHeatmeter();
//        if (info.deactivateType == HeatmeterPeriodSubType.OPERATING) {
//            HeatmeterPeriod lastOperationalPeriod = Iterables.find(heatmeter.getPeriods(),
//                    new Predicate<HeatmeterPeriod>() {
//
//                        @Override
//                        public boolean apply(HeatmeterPeriod period) {
//                            return (period.getEndDate() == null) && (period.getType() == HeatmeterPeriodSubType.OPERATING);
//                        }
//                    });
//            lastOperationalPeriod.setEndDate(info.deactivateDate);
//            return lastOperationalPeriod;
//        } else if (info.deactivateType == HeatmeterPeriodSubType.ADJUSTMENT) {
//            HeatmeterPeriod adjustmentPeriod = new HeatmeterPeriod(heatmeterListWrapper.getOperatingMonthDate());
//            adjustmentPeriod.setBeginDate(info.deactivateDate);
//            adjustmentPeriod.setType(HeatmeterPeriodSubType.ADJUSTMENT);
//            adjustmentPeriod.setHeatmeterId(heatmeter.getId());
//            heatmeter.getPeriods().add(adjustmentPeriod);
//            return adjustmentPeriod;
//        } else {
//            throw new IllegalStateException("Unknown heatmeter period type: " + info.deactivateType);
//        }
//    }

    public void open(Heatmeter heatmeter, AjaxRequestTarget target) {
        this.heatmeter = heatmeter;

        model.setObject(new DeactivateHeatmeterEntity(HeatmeterPeriodSubType.ADJUSTMENT, DateUtil.getCurrentDate()));

        target.add(container);
        dialog.open(target);
    }

    protected abstract void onDeactivate(Heatmeter heatmeter, AjaxRequestTarget target);
}
