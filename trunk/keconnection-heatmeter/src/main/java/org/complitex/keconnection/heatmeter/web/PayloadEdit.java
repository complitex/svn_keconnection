package org.complitex.keconnection.heatmeter.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.dictionary.web.component.DatePicker;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.Payload;
import org.complitex.keconnection.heatmeter.service.HeatmeterBean;
import org.complitex.keconnection.heatmeter.service.PayloadBean;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;

import javax.ejb.EJB;

import static org.complitex.dictionary.util.PageUtil.newDatePickers;
import static org.complitex.dictionary.util.PageUtil.newRequiredTextFields;
import static org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy.KE_ORGANIZATION_OBJECT_ID;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.10.12 18:20
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class PayloadEdit extends FormTemplatePage{
    @EJB
    private PayloadBean payloadBean;

    @EJB
    private HeatmeterBean heatmeterBean;

    public PayloadEdit(PageParameters pageParameters) {
        Long id = pageParameters.get("id").toOptionalLong();

        Payload payload = id != null ? payloadBean.getPayload(id) : new Payload();

        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        final IModel<Payload> model = new CompoundPropertyModel<>(payload);

        Form<Payload> form = new Form<>("form", model);
        add(form);

        //fields
        form.add(newRequiredTextFields("ls", "payload1", "payload2", "payload3"));
        form.add(newDatePickers(true, "beginDate", "operatingMonth"));
        form.add(new DatePicker("endDate"));

        form.add(new Button("save"){
            @Override
            public void onSubmit() {
                Payload payload = model.getObject();

                Heatmeter heatmeter = heatmeterBean.getHeatmeterByLs(payload.getLs(), KE_ORGANIZATION_OBJECT_ID);

                if (heatmeter == null){
                    error(getStringFormat("error_heatmeter_not_found"));
                    return;
                }

                //heatmeter
                payload.setHeatmeterId(heatmeter.getId());

                payloadBean.save(payload);

                getSession().info(getStringFormat("info_saved", payload.getLs()));

                setResponsePage(PayloadList.class);
            }
        });

        form.add(new Link("cancel"){
            @Override
            public void onClick() {
                setResponsePage(PayloadList.class);
            }
        });
    }
}
