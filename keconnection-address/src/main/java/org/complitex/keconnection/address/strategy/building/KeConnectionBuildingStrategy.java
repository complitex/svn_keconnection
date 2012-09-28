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
import org.complitex.keconnection.address.strategy.building.entity.BuildingCode;
import org.complitex.keconnection.address.strategy.building.entity.BuildingCodeList;
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
    private static final long BUILDING_CODE = 502;
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
        BuildingCodeList buildingCodes = loadBuildingCodes(building);
        return new KeConnectionBuilding(building, buildingCodes);
    }

    @Transactional
    @Override
    public KeConnectionBuilding findHistoryObject(long objectId, Date date) {
        Building building = super.findHistoryObject(objectId, date);
        if (building == null) {
            return null;
        }
        BuildingCodeList associationList = loadBuildingCodes(building);
        return new KeConnectionBuilding(building, associationList);
    }

    @Override
    public KeConnectionBuilding newInstance() {
        return new KeConnectionBuilding(super.newInstance(), new BuildingCodeList());
    }

    @Transactional
    @Override
    protected void insertDomainObject(DomainObject object, Date insertDate) {
        super.insertDomainObject(object, insertDate);

        KeConnectionBuilding building = (KeConnectionBuilding) object;
        if (!building.getBuildingCodeList().isEmpty()
                && !building.getBuildingCodeList().hasNulls()) {
            addBuildingCode(building);
        }
    }

    @Transactional
    private void addBuildingCode(KeConnectionBuilding building) {
        building.removeAttribute(BUILDING_CODE);

        long i = 1;
        for (BuildingCode association : building.getBuildingCodeList()) {
            association.setBuildingId(building.getId());
            saveBuildingCode(association);

            building.addAttribute(newBuildingCodeAttribute(i++, association.getId()));
        }
    }

    private Attribute newBuildingCodeAttribute(long attributeId, long buildingAssociationId) {
        Attribute a = new Attribute();
        a.setAttributeTypeId(BUILDING_CODE);
        a.setValueId(buildingAssociationId);
        a.setValueTypeId(BUILDING_CODE);
        a.setAttributeId(attributeId);
        return a;
    }

    @Transactional
    @Override
    public void update(DomainObject oldObject, DomainObject newObject, Date updateDate) {
        KeConnectionBuilding newBuilding = (KeConnectionBuilding) newObject;
        KeConnectionBuilding oldBuilding = (KeConnectionBuilding) oldObject;

        if (!newBuilding.getBuildingCodeList().isEmpty()
                && !newBuilding.getBuildingCodeList().hasNulls()) {
            if (!newBuilding.getBuildingCodeList().equals(oldBuilding.getBuildingCodeList())) {
                addBuildingCode(newBuilding);
            }
        } else {
            newBuilding.removeAttribute(BUILDING_CODE);
        }

        super.update(oldObject, newObject, updateDate);
    }

    @Transactional
    private void saveBuildingCode(BuildingCode buildingCode) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insertBuildingCode", buildingCode);
    }

    private BuildingCodeList loadBuildingCodes(Building building) {
        List<Attribute> buildingCodeAttributes = building.getAttributes(BUILDING_CODE);
        Set<Long> buildingCodeIds = Sets.newHashSet();
        for (Attribute associationAttribute : buildingCodeAttributes) {
            buildingCodeIds.add(associationAttribute.getValueId());
        }

        List<BuildingCode> buildingCodes = new ArrayList<>();
        if (!buildingCodeIds.isEmpty()) {
            buildingCodes = sqlSession().selectList(MAPPING_NAMESPACE
                    + ".getBuildingCodes", ImmutableMap.of("ids", buildingCodeIds));
            Collections.sort(buildingCodes, new Comparator<BuildingCode>() {

                @Override
                public int compare(BuildingCode o1, BuildingCode o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });
        }
        return new BuildingCodeList(buildingCodes);
    }

    @Transactional
    @Override
    public void delete(long objectId, Locale locale) throws DeleteException {
        deleteChecks(objectId, locale);

        sqlSession().delete(MAPPING_NAMESPACE + ".deleteBuildingCodes",
                ImmutableMap.of("objectId", objectId, "buildingCodesAT", BUILDING_CODE));

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
        return sqlSession().selectOne(MAPPING_NAMESPACE + ".selectBuildingCodeIdByCode",
                ImmutableMap.of("organizationId", organizationId, "buildingCode", buildingCode));
    }

    public Long getBuildingCodeId(final Long organizationId, final Long buildingId) {
        return sqlSession().selectOne(MAPPING_NAMESPACE + ".selectBuildingCodeIdByBuilding",
                ImmutableMap.of("organizationId", organizationId, "buildingId", buildingId));
    }
}
