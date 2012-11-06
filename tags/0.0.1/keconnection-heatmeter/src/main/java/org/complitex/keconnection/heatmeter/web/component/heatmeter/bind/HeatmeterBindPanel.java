/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.component.heatmeter.bind;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.keconnection.heatmeter.entity.ExternalHeatmeter;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterBindingStatus;
import org.complitex.keconnection.heatmeter.service.HeatmeterBindService;
import org.complitex.keconnection.heatmeter.service.exception.HeatmeterBindException;
import org.odlabs.wiquery.ui.dialog.Dialog;

/**
 *
 * @author Artem
 */
public abstract class HeatmeterBindPanel extends Panel {

    @EJB
    private HeatmeterBindService heatmeterBindService;
    private final Dialog dialog;
    private final WebMarkupContainer container;
    private final FormComponent<?> bind;
    private final IModel<List<ExternalHeatmeter>> externalHeatmetersModel = new ListModel<>(new ArrayList<ExternalHeatmeter>());
    private final IModel<ExternalHeatmeter> model = Model.of();
    private Heatmeter heatmeter;
    private String buildingLabel;

    public HeatmeterBindPanel(String id) {
        super(id);

        dialog = new Dialog("dialog") {

            {
                getOptions().putLiteral("width", "auto");
            }
        };
        dialog.setModal(true);
        add(dialog);

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        dialog.add(container);

        final FeedbackPanel messages = new FeedbackPanel("messages", new ContainerFeedbackMessageFilter(container));
        messages.setOutputMarkupId(true);
        container.add(messages);

        container.add(new Label("heatmeterInfo", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if (heatmeter != null) {
                    return MessageFormat.format(getString("heatmeterInfo"), heatmeter.getLs(), buildingLabel);
                } else {
                    return null;
                }
            }
        }));

        Form<Void> form = new Form<>("form");
        container.add(form);

        final RadioGroup<ExternalHeatmeter> radioGroup = new RadioGroup<ExternalHeatmeter>("radioGroup", model);
        form.add(radioGroup);

        ListView<ExternalHeatmeter> externalHeatmetersView = new ListView<ExternalHeatmeter>("externalHeatmeters",
                externalHeatmetersModel) {

            @Override
            protected void populateItem(ListItem<ExternalHeatmeter> item) {
                ExternalHeatmeter e = item.getModelObject();
                item.add(new Radio<ExternalHeatmeter>("radio", item.getModel(), radioGroup));
                item.add(new Label("id", StringUtil.valueOf(e.getId())));
                item.add(new Label("number", StringUtil.valueOf(e.getNumber())));
            }
        };
        radioGroup.add(externalHeatmetersView);

        bind = new IndicatingAjaxButton("bind", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                final ExternalHeatmeter externalHeatmeter = model.getObject();

                if (externalHeatmeter == null) {
                    error(getString("heatmeterNotChosen"));
                    target.add(messages);
                    return;
                }

                heatmeterBindService.updateHeatmeterCorrection(heatmeter, externalHeatmeter,
                        HeatmeterBindingStatus.BOUND);
                
                onBind(heatmeter, target);

                close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        };
        form.add(bind);

        form.add(new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                close(target);
            }
        });
    }

    private void close(AjaxRequestTarget target) {
        dialog.close(target);
    }
    
    protected abstract void onBind(Heatmeter heatmeter, AjaxRequestTarget target);

    public void open(Heatmeter heatmeter, String buildingLabel, AjaxRequestTarget target) {
        this.heatmeter = heatmeter;
        this.buildingLabel = buildingLabel;

        boolean error = false;
        List<ExternalHeatmeter> externalHeatmeters = null;
        try {
            externalHeatmeters = heatmeterBindService.getExternalHeatmeters(heatmeter);
            if (externalHeatmeters == null) {
                error = true;
                container.error(getString("incorrectHeatmeterError"));
            }
        } catch (Exception e) {
            error = true;
            if (e instanceof HeatmeterBindException) {
                HeatmeterBindingStatus status = ((HeatmeterBindException) e).getStatus();
                heatmeterBindService.updateHeatmeterCorrection(heatmeter, null, status);
            }
            container.error(HeatmeterBindError.message(heatmeter, e, getLocale()));
        }

        if (error) {
            bind.setVisible(false);
        } else {
            bind.setVisible(true);
        }

        externalHeatmetersModel.setObject(externalHeatmeters);
        model.setObject(externalHeatmeters != null && !externalHeatmeters.isEmpty() ? externalHeatmeters.get(0) : null);

        dialog.open(target);
        target.add(container);
    }
}
