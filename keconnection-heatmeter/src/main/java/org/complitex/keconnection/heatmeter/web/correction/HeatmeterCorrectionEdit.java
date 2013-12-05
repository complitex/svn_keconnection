/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.correction;

import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.keconnection.heatmeter.entity.HeatmeterCorrectionView;
import org.complitex.keconnection.heatmeter.service.HeatmeterCorrectionBean;
import org.complitex.keconnection.heatmeter.web.correction.component.HeatmeterHistoryCorrectionDialog;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public final class HeatmeterCorrectionEdit extends FormTemplatePage {

    private final Logger log = LoggerFactory.getLogger(HeatmeterCorrectionEdit.class);
    static final String CORRECTION_ID = "correction_id";
    @EJB
    private HeatmeterCorrectionBean heatmeterCorrectionBean;

    public HeatmeterCorrectionEdit(PageParameters parameters) {
        final long correctionId = parameters.get(CORRECTION_ID).toLong();
        final HeatmeterCorrectionView correction = heatmeterCorrectionBean.findById(correctionId);
        if (correction == null) {
            throw new IllegalStateException("Not existing heatmeter correction with correction id: " + correctionId);
        }

        final boolean history = correction.isHistory();

        IModel<String> titleModel = new ResourceModel("title" + (history ? "View" : "Edit"));
        add(new Label("title", titleModel));
        add(new Label("caption", titleModel));

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        IModel<HeatmeterCorrectionView> model = new CompoundPropertyModel<>(correction);
        Form<HeatmeterCorrectionView> form = new Form<>("form", model);
        add(form);

        form.add(new Label("ls"));

        TextField<String> externalHeatmeterId = new TextField<>("externalHeatmeterId");
        externalHeatmeterId.setRequired(true);
        externalHeatmeterId.setEnabled(!history);
        form.add(externalHeatmeterId);

        TextField<String> heatmeterNumber = new TextField<>("heatmeterNumber");
        heatmeterNumber.setRequired(true);
        heatmeterNumber.setEnabled(!history);
        form.add(heatmeterNumber);

        final HeatmeterHistoryCorrectionDialog historyDialog =
                new HeatmeterHistoryCorrectionDialog("heatmeterHistoryCorrectionDialog",
                correction.getSystemHeatmeterId(), correction.getLs());
        add(historyDialog);

        AjaxLink<Void> historyLink = new AjaxLink<Void>("historyLink") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                historyDialog.open(target);
            }
        };
        historyLink.setVisible(historyDialog.isVisible());
        form.add(historyLink);

        AjaxButton save = new IndicatingAjaxButton("save", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    heatmeterCorrectionBean.save(correction);
                    setResponsePage(HeatmeterCorrectionList.class);
                } catch (Exception e) {
                    log.error("Couldn't save heatmeter correction", e);
                    error(getString("db_errror"));
                    target.add(messages);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }
        };
        save.setVisible(!history);
        form.add(save);

        Link<Void> cancel = new Link<Void>("cancel") {

            @Override
            public void onClick() {
                setResponsePage(HeatmeterCorrectionList.class);
            }
        };
        cancel.setVisible(!history);
        form.add(cancel);

        Link<Void> back = new Link<Void>("back") {

            @Override
            public void onClick() {
                setResponsePage(HeatmeterCorrectionList.class);
            }
        };
        back.setVisible(history);
        form.add(back);
    }
}
