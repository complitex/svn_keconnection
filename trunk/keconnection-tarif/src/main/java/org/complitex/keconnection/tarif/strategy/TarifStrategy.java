/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.tarif.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.Stateless;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.AttributeExample;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.strategy.web.AbstractComplexAttributesPanel;
import org.complitex.dictionary.strategy.web.validate.IValidator;
import org.complitex.dictionary.util.AttributeUtil;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.keconnection.tarif.strategy.web.edit.TarifEditComponent;
import org.complitex.keconnection.tarif.strategy.web.edit.TarifValidator;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@Stateless
public class TarifStrategy extends TemplateStrategy {

    private static final String RESOURCE_BUNDLE = TarifStrategy.class.getName();
    private static final String MAPPING_NAMESPACE = TarifStrategy.class.getPackage().getName() + ".Tarif";
    /**
     * Attribute type ids
     */
    public static final long NAME = 3300;
    public static final long CODE = 3301;
    public static final long TARIF_GROUP = 3302;

    @Override
    public String getEntityTable() {
        return "tarif";
    }

    @Override
    protected List<Long> getListAttributeTypes() {
        return Lists.newArrayList(NAME, CODE);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return AttributeUtil.getStringCultureValue(object, NAME, locale);
    }

    @Override
    public void configureExample(DomainObjectExample example, Map<String, Long> ids, String searchTextInput) {
        if (!Strings.isEmpty(searchTextInput)) {
            AttributeExample attrExample = example.getAttributeExample(NAME);
            if (attrExample == null) {
                attrExample = new AttributeExample(NAME);
                example.addAttributeExample(attrExample);
            }
            attrExample.setValue(searchTextInput);
        }
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(RESOURCE_BUNDLE, getEntityTable(), locale);
    }

    public DomainObject getObjectByCode(int code) {
        DomainObjectExample example = new DomainObjectExample();
        AttributeExample codeExample = new AttributeExample(CODE);
        codeExample.setValue(String.valueOf(code));
        example.addAttributeExample(codeExample);
        configureExample(example, ImmutableMap.<String, Long>of(), null);
        List<? extends DomainObject> results = find(example);
        if (results == null || results.isEmpty()) {
            return null;
        } else if (results.size() > 1) {
            throw new IllegalStateException("More one tarif have the same code.");
        } else {
            return results.get(0);
        }
    }

    public Long getIdByCode(int code) {
        DomainObject object = getObjectByCode(code);
        return object != null ? object.getId() : null;
    }

    @Transactional
    public Long validateCode(Long id, String code) {
        Map<String, Object> params = ImmutableMap.<String, Object>of("codeAT", CODE, "code", code);
        List<Long> results = sqlSession().selectList(MAPPING_NAMESPACE + ".validateCode", params);
        for (Long result : results) {
            if (!result.equals(id)) {
                return result;
            }
        }
        return null;
    }

    @Override
    public IValidator getValidator() {
        return new TarifValidator();
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass() {
        return TarifEditComponent.class;
    }

    @Override
    protected void extendOrderBy(DomainObjectExample example) {
        if (example.getOrderByAttributeTypeId() != null
                && example.getOrderByAttributeTypeId().equals(CODE)) {
            example.setOrderByNumber(true);
        }
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADMIN_MODULE_EDIT};
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.ADMIN_MODULE_EDIT};
    }
}
