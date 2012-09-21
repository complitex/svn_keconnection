/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.address.strategy.building;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.strategy.DeleteException;
import org.complitex.dictionary.strategy.web.AbstractComplexAttributesPanel;
import org.complitex.dictionary.strategy.web.validate.IValidator;
import org.complitex.keconnection.address.strategy.building.entity.BuildingOrganizationAssociation;
import org.complitex.keconnection.address.strategy.building.entity.BuildingOrganizationAssociationList;
import org.complitex.keconnection.address.strategy.building.entity.KeConnectionBuilding;
import org.complitex.keconnection.address.strategy.building.web.edit.KeConnectionBuildingEditComponent;
import org.complitex.keconnection.address.strategy.building.web.edit.KeConnectionBuildingValidator;
import org.complitex.keconnection.address.strategy.building.web.list.KeConnectionBuildingList;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.*;

/**
 *
 * @author Artem
 */
@Stateless
public class KeConnectionBuildingStrategy extends BuildingStrategy {

    public static final String KECONNECTION_BUILDING_STRATEGY_NAME = KeConnectionBuildingStrategy.class.getSimpleName();
    public static final String MAPPING_NAMESPACE = KeConnectionBuildingStrategy.class.getPackage().getName() + ".KeConnectionBuilding";
    /**
     * Attribute ids
     */
    private static final long ORGANIZATION_ASSOCIATIONS = 502;
    @EJB
    private LocaleBean localeBean;

    @Override
    public Class<? extends WebPage> getListPage() {
        return KeConnectionBuildingList.class;
    }

    @Override
    public PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity) {
        return addBuildingStrategyName(super.getEditPageParams(objectId, parentId, parentEntity));
    }

    @Override
    public PageParameters getHistoryPageParams(long objectId) {
        return addBuildingStrategyName(super.getHistoryPageParams(objectId));
    }

    private PageParameters addBuildingStrategyName(PageParameters params) {
        if (params == null) {
            params = new PageParameters();
        }
        params.set(STRATEGY, KECONNECTION_BUILDING_STRATEGY_NAME);
        return params;
    }

    @Transactional
    @Override
    public KeConnectionBuilding findById(long id, boolean runAsAdmin) {
        Building building = super.findById(id, runAsAdmin);
        if (building == null) {
            return null;
        }
        BuildingOrganizationAssociationList associationList = loadBuildingOrganizationAssociations(building);
        return new KeConnectionBuilding(building, associationList);
    }

    @Transactional
    @Override
    public KeConnectionBuilding findHistoryObject(long objectId, Date date) {
        Building building = super.findHistoryObject(objectId, date);
        if (building == null) {
            return null;
        }
        BuildingOrganizationAssociationList associationList = loadBuildingOrganizationAssociations(building);
        return new KeConnectionBuilding(building, associationList);
    }

    @Override
    public KeConnectionBuilding newInstance() {
        return new KeConnectionBuilding(super.newInstance(), new BuildingOrganizationAssociationList());
    }

    @Transactional
    @Override
    protected void insertDomainObject(DomainObject object, Date insertDate) {
        super.insertDomainObject(object, insertDate);

        KeConnectionBuilding building = (KeConnectionBuilding) object;
        if (!building.getBuildingOrganizationAssociationList().isEmpty()
                && !building.getBuildingOrganizationAssociationList().hasNulls()) {
            addBuildingOrganizationAssociationAttributes(building);
        }
    }

    @Transactional
    private void addBuildingOrganizationAssociationAttributes(KeConnectionBuilding building) {
        building.removeAttribute(ORGANIZATION_ASSOCIATIONS);

        long i = 1;
        for (BuildingOrganizationAssociation association : building.getBuildingOrganizationAssociationList()) {
            association.setBuildingId(building.getId());
            saveBuildingOrganizationAssociation(association);

            building.addAttribute(newBuildingOrganizationAssociationAttribute(i++, association.getId()));
        }
    }

    private Attribute newBuildingOrganizationAssociationAttribute(long attributeId, long buildingAssociationId) {
        Attribute a = new Attribute();
        a.setAttributeTypeId(ORGANIZATION_ASSOCIATIONS);
        a.setValueId(buildingAssociationId);
        a.setValueTypeId(ORGANIZATION_ASSOCIATIONS);
        a.setAttributeId(attributeId);
        return a;
    }

    @Transactional
    @Override
    public void update(DomainObject oldObject, DomainObject newObject, Date updateDate) {
        KeConnectionBuilding newBuilding = (KeConnectionBuilding) newObject;
        KeConnectionBuilding oldBuilding = (KeConnectionBuilding) oldObject;

        if (!newBuilding.getBuildingOrganizationAssociationList().isEmpty()
                && !newBuilding.getBuildingOrganizationAssociationList().hasNulls()) {
            if (!newBuilding.getBuildingOrganizationAssociationList().equals(oldBuilding.getBuildingOrganizationAssociationList())) {
                addBuildingOrganizationAssociationAttributes(newBuilding);
            }
        } else {
            newBuilding.removeAttribute(ORGANIZATION_ASSOCIATIONS);
        }

        super.update(oldObject, newObject, updateDate);
    }

    @Transactional
    private void saveBuildingOrganizationAssociation(BuildingOrganizationAssociation buildingOrganizationAssociation) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insertBuildingOrganizationAssociation", buildingOrganizationAssociation);
    }

    @Transactional
    public void addBuildingOrganizationAssociation(KeConnectionBuilding building, BuildingOrganizationAssociation association) {
        association.setBuildingId(building.getId());

        if (building.getBuildingOrganizationAssociationList().allowAddNew(association)) {
            saveBuildingOrganizationAssociation(association);

            int attributeId = building.getAttributes(ORGANIZATION_ASSOCIATIONS).size() + 1;
            Attribute a = newBuildingOrganizationAssociationAttribute(attributeId, association.getId());
            a.setObjectId(building.getId());
            a.setStartDate(building.getStartDate());
            insertAttribute(a);
        }
    }

    private BuildingOrganizationAssociationList loadBuildingOrganizationAssociations(Building building) {
        List<Attribute> buildingOrganizationAssociationAttributes = building.getAttributes(ORGANIZATION_ASSOCIATIONS);
        Set<Long> buildingOrganizationAssociationIds = Sets.newHashSet();
        for (Attribute associationAttribute : buildingOrganizationAssociationAttributes) {
            buildingOrganizationAssociationIds.add(associationAttribute.getValueId());
        }

        List<BuildingOrganizationAssociation> buildingOrganizationAssociations = new ArrayList<>();
        if (!buildingOrganizationAssociationIds.isEmpty()) {
            buildingOrganizationAssociations = sqlSession().selectList(MAPPING_NAMESPACE
                    + ".getBuildingOrganizationAssociations", ImmutableMap.of("ids", buildingOrganizationAssociationIds));
            Collections.sort(buildingOrganizationAssociations, new Comparator<BuildingOrganizationAssociation>() {

                @Override
                public int compare(BuildingOrganizationAssociation o1, BuildingOrganizationAssociation o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });
        }
        return new BuildingOrganizationAssociationList(buildingOrganizationAssociations);
    }

    @Transactional
    @Override
    public void delete(long objectId, Locale locale) throws DeleteException {
        deleteChecks(objectId, locale);

        sqlSession().delete(MAPPING_NAMESPACE + ".deleteBuildingOrganizationAssociations",
                ImmutableMap.of("objectId", objectId, "buildingOrganizationAssociationsAT", ORGANIZATION_ASSOCIATIONS));

        deleteStrings(objectId);
        deleteAttribute(objectId);
        deleteObject(objectId, locale);
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass() {
        return KeConnectionBuildingEditComponent.class;
    }

    @Override
    public IValidator getValidator() {
        return new KeConnectionBuildingValidator(localeBean.getSystemLocale());
    }

    public Long getBuildingCodeId(final Long organizationId, final String buildingCode) {
        return sqlSession().selectOne(MAPPING_NAMESPACE + ".selectBuildingCodeId", new HashMap<String, Object>() {

            {
                put("organizationId", organizationId);
                put("buildingCode", buildingCode);
            }
        });
    }
}
