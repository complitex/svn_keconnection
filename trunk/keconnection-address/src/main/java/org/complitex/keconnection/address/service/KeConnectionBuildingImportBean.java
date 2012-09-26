/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.address.service;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.address.entity.BuildingImport;
import org.complitex.keconnection.address.entity.BuildingPartImport;

/**
 *
 * @author Artem
 */
@Stateless
public class KeConnectionBuildingImportBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = KeConnectionBuildingImportBean.class.getName();

    @Transactional
    public void insert(long buildingPartId, long distrId, long streetId, String num, String part, long gekId, String code) {
        BuildingImport b = new BuildingImport(distrId, streetId, num);
        sqlSession().insert(MAPPING_NAMESPACE + ".insert", b);
        addPart(buildingPartId, part, gekId, code, b.getId());
    }

    public Long findId(long streetId, String num) {
        return sqlSession().selectOne(MAPPING_NAMESPACE + ".findId", ImmutableMap.of("streetId", streetId, "num", num));
    }

    @Transactional
    public void saveOrUpdate(long buildingPartId, long distrId, long streetId, String num, String part,
            long gekId, String code) {
        Long id = findId(streetId, num);
        if (id == null) {
            insert(buildingPartId, distrId, streetId, num, part, gekId, code);
        } else {
            addPart(buildingPartId, part, gekId, code, id);
        }
    }

    @Transactional
    public void addPart(long buildingPartId, String part, long gekId, String code, long buildingImportId) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insertPart",
                new BuildingPartImport(buildingPartId, part, gekId, code, buildingImportId));
    }

    @Transactional
    public void markProcessed(long buildingImportId) {
        sqlSession().update(MAPPING_NAMESPACE + ".markProcessed", buildingImportId);
    }

    @Transactional
    public void markProcessed(List<BuildingImport> buildings) {
        for (BuildingImport b : buildings) {
            markProcessed(b.getId());
        }
    }

    public List<BuildingImport> find(int count) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".find", count);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void delete() {
        sqlSession().delete(MAPPING_NAMESPACE + ".deleteParts");
        sqlSession().delete(MAPPING_NAMESPACE + ".delete");
    }
}
