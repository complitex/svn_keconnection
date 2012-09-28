package org.complitex.keconnection.heatmeter.web;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.dictionary.web.component.EnumDropDownChoice;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.CollapsibleInputSearchComponent;
import org.complitex.dictionary.web.component.search.ISearchCallback;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.keconnection.address.strategy.building.KeConnectionBuildingStrategy;
import org.complitex.keconnection.address.strategy.building.entity.BuildingCode;
import org.complitex.keconnection.address.strategy.building.entity.KeConnectionBuilding;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriod;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;
import org.complitex.keconnection.heatmeter.entity.HeatmeterType;
import org.complitex.keconnection.heatmeter.service.HeatmeterBean;
import org.complitex.keconnection.heatmeter.service.HeatmeterImportService;
import org.complitex.keconnection.heatmeter.service.HeatmeterPeriodBean;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

        Heatmeter heatmeter = id != null ? heatmeterBean.getHeatmeter(id) : new Heatmeter();
        final IModel<Heatmeter> model = new CompoundPropertyModel<>(heatmeter);

        Form form = new Form<>("form", model);
        add(form);

        //Ls
        form.add(newRequiredTextFields(new String[]{"ls"}));

        //Type
        form.add(new EnumDropDownChoice<>("type", HeatmeterType.class).setNullValid(false));

        //SearchComponentState
        final SearchComponentState searchComponentState = new SearchComponentState();
        if (heatmeter.getId() != null){
            Building building = buildingStrategy.findById(heatmeter.getBuildingId(), true);
            searchComponentState.put("building", building);

            DomainObject street = streetStrategy.findById(building.getPrimaryStreetId(), true);
            searchComponentState.put("street", street);

            DomainObject city = cityStrategy.findById(street.getParentId(), true);
            searchComponentState.put("city", city);
        }

        //Organization
        final IModel<DomainObject> organizationModel = Model.of(heatmeter.getOrganizationId() != null
                ? organizationStrategy.findById(heatmeter.getOrganizationId(), true)
                : new DomainObject());

        final DisableAwareDropDownChoice<DomainObject> organizations = new DisableAwareDropDownChoice<>("organization",
                organizationModel,
                new LoadableDetachableModel<List<? extends DomainObject>>() {
                    @Override
                    protected List<? extends DomainObject> load() {
                        List<DomainObject> list = new ArrayList<>();

                        Building b = (Building) searchComponentState.get("building");

                        if (b != null) {
                            KeConnectionBuilding building = buildingStrategy.findById(b.getId(), true);

                            if (building != null && building.getBuildingCodeList() != null){
                                for (BuildingCode buildingCode : building.getBuildingCodeList()){
                                    list.add(organizationStrategy.findById(buildingCode.getOrganizationId(), true));
                                }
                            }
                        }

                        return list;
                    }
                },
                new DomainObjectDisableAwareRenderer() {

                    @Override
                    public Object getDisplayValue(DomainObject object) {
                        return organizationStrategy.displayDomainObject(object, getLocale());
                    }
                });
        organizations.setOutputMarkupId(true);

        form.add(organizations);

        //Building
        form.add(new CollapsibleInputSearchComponent("building", searchComponentState,
                Arrays.asList("city", "street", "building"), new ISearchCallback() {
            @Override
            public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
                target.add(organizations);
            }
        }, ShowMode.ALL, true));

        //Save
        form.add(new Button("save"){
            @Override
            public void onSubmit() {
                try {
                    Heatmeter heatmeter = model.getObject();

                    Building building = (Building) searchComponentState.get("building");
                    if (building == null){
                        error(getString("error_building_required"));
                        return;
                    }

                    DomainObject organization = organizations.getModelObject();
                    if (organization == null){
                        error(getString("error_organization_required"));
                        return;
                    }

                    Long buildingCodeId = buildingStrategy.getBuildingCodeId(organization.getId(), building.getId());
                    if (buildingCodeId == null){
                        error(getString("error_building_code_not_found"));
                        return;
                    }

                    heatmeter.setBuildingCodeId(buildingCodeId);

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
