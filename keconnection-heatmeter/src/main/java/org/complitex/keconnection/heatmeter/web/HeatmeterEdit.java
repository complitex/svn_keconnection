package org.complitex.keconnection.heatmeter.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterType;
import org.complitex.keconnection.heatmeter.service.HeatmeterBean;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;

import static org.complitex.dictionary.util.PageUtil.newRequiredTextFields;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.09.12 15:25
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class HeatmeterEdit extends FormTemplatePage{
    private final static Logger log = LoggerFactory.getLogger(HeatmeterEdit.class);

    @EJB
    private HeatmeterBean heatmeterBean;

    public HeatmeterEdit() {
        init(null);
    }

    public HeatmeterEdit(PageParameters pageParameters) {
        init(pageParameters.get("id").toLongObject());
    }

    private void init(Long id){
        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        Heatmeter heatmeter = id != null ? heatmeterBean.getHeatmeter(id) : new Heatmeter();
        final IModel<Heatmeter> model = new CompoundPropertyModel<>(heatmeter);

        Form form = new Form<>("form", model);
        add(form);

        form.add(newRequiredTextFields(new String[]{"ls", "buildingCodeId"}));

        form.add(new Button("save"){
            @Override
            public void onSubmit() {
                try {
                    Heatmeter heatmeter = model.getObject();
                    heatmeter.setType(HeatmeterType.HEATING);

                    heatmeterBean.save(heatmeter);

                    getSession().info(getString("info_saved"));
                    setResponsePage(HeatmeterList.class);
                } catch (Exception e) {
                    log.error("Ошибка сохранения теплосчетчика", e);
                }
            }
        });

        form.add(new Button("cancel"){
            @Override
            public void onSubmit() {
                setResponsePage(HeatmeterList.class);
            }
        }.setDefaultFormProcessing(false));
    }
}
