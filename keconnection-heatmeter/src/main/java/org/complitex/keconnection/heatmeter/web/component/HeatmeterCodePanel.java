package org.complitex.keconnection.heatmeter.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.CollapsibleInputSearchComponent;
import org.complitex.dictionary.web.component.search.ISearchCallback;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.keconnection.address.strategy.building.KeConnectionBuildingStrategy;
import org.complitex.keconnection.address.strategy.building.entity.BuildingCode;
import org.complitex.keconnection.address.strategy.building.entity.KeConnectionBuilding;
import org.complitex.keconnection.heatmeter.entity.HeatmeterCode;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 03.10.12 16:31
 */
public class HeatmeterCodePanel extends Panel {
    @EJB
    private KeConnectionBuildingStrategy buildingStrategy;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private CityStrategy cityStrategy;

    @EJB(name = IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME)
    private IKeConnectionOrganizationStrategy organizationStrategy;

    public HeatmeterCodePanel(String id, final IModel<List<HeatmeterCode>> model) {
        super(id, model);

        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        ListView listView = new ListView<HeatmeterCode>("list", model) {
            @Override
            protected void populateItem(ListItem<HeatmeterCode> item) {
                final HeatmeterCode heatmeterCode = item.getModelObject();

                //SearchComponentState
                final SearchComponentState searchComponentState = new SearchComponentState();

                if (heatmeterCode.getBuildingId() != null) {
                    Building building = buildingStrategy.findById(heatmeterCode.getBuildingId(), true);
                    searchComponentState.put("building", building);

                    if (building != null && building.getPrimaryStreetId() != null) {
                        DomainObject street = streetStrategy.findById(building.getPrimaryStreetId(), true);
                        searchComponentState.put("street", street);

                        if (street != null && street.getParentId() != null) {
                            DomainObject city = cityStrategy.findById(street.getParentId(), true);
                            searchComponentState.put("city", city);
                        }
                    }
                }

                //Organization
                final IModel<DomainObject> organizationModel = new Model<DomainObject>(){
                    @Override
                    public void setObject(DomainObject object) {
                        super.setObject(object);

                        heatmeterCode.setOrganizationId(object != null ? object.getId() : null);
                        updateHeatmeterCode(heatmeterCode);
                    }
                };

                if (heatmeterCode.getOrganizationId() != null) {
                    DomainObject organization = organizationStrategy.findById(heatmeterCode.getOrganizationId(), true);

                    if (organization != null){
                        organizationModel.setObject(organization);
                    }
                }else {
                    organizationModel.setObject(new DomainObject());
                }

                final DisableAwareDropDownChoice<DomainObject> organizations = new DisableAwareDropDownChoice<>("organization",
                        organizationModel,
                        new LoadableDetachableModel<List<? extends DomainObject>>() {
                            @Override
                            protected List<? extends DomainObject> load() {
                                List<DomainObject> list = new ArrayList<>();

                                DomainObject b = searchComponentState.get("building");

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

                item.add(organizations);

                //Building
                item.add(new CollapsibleInputSearchComponent("building", searchComponentState,
                        Arrays.asList("city", "street", "building"), new ISearchCallback() {
                    @Override
                    public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
                        heatmeterCode.setBuildingId(ids.get("building"));
                        updateHeatmeterCode(heatmeterCode);

                        target.add(organizations);
                    }
                }, ShowMode.ALL, true));

                item.add(new AjaxLink<List<HeatmeterCode>>("remove", model) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        getModelObject().remove(heatmeterCode);

                        target.add(container);
                    }
                });
            }
        };
        container.add(listView);

        add(new AjaxLink<List<HeatmeterCode>>("add", model) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                getModelObject().add(new HeatmeterCode());

                target.add(container);
            }
        });
    }

    private void updateHeatmeterCode(HeatmeterCode code){
        if (code.getBuildingId() != null && code.getOrganizationId() != null){
            Long buildingCodeId = buildingStrategy.getBuildingCodeId(code.getOrganizationId(), code.getBuildingId());

            code.setBuildingCodeId(buildingCodeId);
        }
    }
}
