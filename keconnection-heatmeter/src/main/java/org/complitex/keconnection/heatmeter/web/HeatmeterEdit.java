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
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.dictionary.web.component.EnumDropDownChoice;
import org.complitex.keconnection.address.strategy.building.KeConnectionBuildingStrategy;
import org.complitex.keconnection.heatmeter.entity.*;
import org.complitex.keconnection.heatmeter.service.HeatmeterBean;
import org.complitex.keconnection.heatmeter.service.HeatmeterImportService;
import org.complitex.keconnection.heatmeter.service.HeatmeterPeriodBean;
import org.complitex.keconnection.heatmeter.web.component.HeatmeterCodePanel;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;

import java.util.ArrayList;

import static org.complitex.dictionary.util.PageUtil.newRequiredTextFields;
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

    @EJB(name = IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME)
    private IKeConnectionOrganizationStrategy organizationStrategy;

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
            heatmeter.setHeatmeterCodes(new ArrayList<HeatmeterCode>());
            heatmeter.setOrganizationId(KE_ORGANIZATION_OBJECT_ID);
        }

        final IModel<Heatmeter> model = new CompoundPropertyModel<>(heatmeter);

        Form form = new Form<>("form", model);
        add(form);

        //Ls
        form.add(newRequiredTextFields(new String[]{"ls"}));

        //Type
        form.add(new EnumDropDownChoice<>("type", HeatmeterType.class).setNullValid(false));

        //Heatmeter Code Panel
        form.add(new HeatmeterCodePanel("heatmeter_codes", new ListModel<>(heatmeter.getHeatmeterCodes())));

        //Save
        form.add(new Button("save"){
            @Override
            public void onSubmit() {
                try {
                    Heatmeter heatmeter = model.getObject();

                    //heatmeter.setBuildingCodeId(buildingCodeId); todo

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
                    getSession().error("Ошибка сохранения теплосчетчика: " + e.getMessage());
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
    }
}
