package org.complitex.keconnection.heatmeater.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.dictionary.util.PageUtil;
import org.complitex.keconnection.heatmeater.entity.Heatmeater;
import org.complitex.keconnection.heatmeater.service.HeatmeaterBean;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;

import static org.complitex.dictionary.util.PageUtil.newRequiredTextFields;
import static org.complitex.dictionary.util.PageUtil.newTextFields;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.09.12 15:25
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class HeatmeaterEdit extends FormTemplatePage{
    private final static Logger log = LoggerFactory.getLogger(HeatmeaterEdit.class);

    @EJB
    private HeatmeaterBean heatmeaterBean;

    public HeatmeaterEdit() {
        init(null);
    }

    public HeatmeaterEdit(PageParameters pageParameters) {
        init(pageParameters.get("id").toLongObject());
    }

    private void init(Long id){
        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        Heatmeater heatmeater = id != null ? heatmeaterBean.getHeatmeater(id) : new Heatmeater();
        final IModel<Heatmeater> model = new CompoundPropertyModel<>(heatmeater);

        Form form = new Form<>("form", model);
        add(form);

        form.add(newRequiredTextFields(new String[]{"gek", "dom", "ul", "ndom"}));
        form.add(newTextFields(new String[]{"lotop0", "lotop1", "lotop2", "lotop3", "lotop4"}));

        //todo add organization and building?

        form.add(new Button("save"){
            @Override
            public void onSubmit() {
                try {
                    heatmeaterBean.save(model.getObject());

                    getSession().info(getString("info_saved"));
                    setResponsePage(HeatmeaterList.class);
                } catch (Exception e) {
                    log.error("Ошибка сохранения теплосчетчика", e);
                }
            }
        });

        form.add(new Button("cancel"){
            @Override
            public void onSubmit() {
                setResponsePage(HeatmeaterList.class);
            }
        }.setDefaultFormProcessing(false));
    }
}
