/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.service;

import com.google.common.collect.ImmutableMap;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.organization.entity.OrganizationImport;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Artem
 */
@Stateless
public class OrganizationImportBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = OrganizationImportBean.class.getName();

    @Transactional
    public void importOrganization(OrganizationImport importOrganization) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insert", importOrganization);
    }

    public List<OrganizationImport> find(Long parentOrganizationId) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".find", parentOrganizationId);
    }

    public OrganizationImport findById(long organizationId) {
        return sqlSession().selectOne(MAPPING_NAMESPACE + ".findById", organizationId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void delete() {
        sqlSession().delete(MAPPING_NAMESPACE + ".delete");
    }

    public boolean operatingMonthExists(long organizationId) {
        Integer result = sqlSession().selectOne(MAPPING_NAMESPACE + ".operatingMonthExists", organizationId);
        return result != null && result == 1;
    }

    @Transactional
    public void insertOperatingMonth(long organizationId, Date beginOm) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insertOperatingMonth",
                ImmutableMap.of("organizationId", organizationId, "beginOm", beginOm));
    }
}
