/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateless;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.AttributeExample;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.strategy.DeleteException;
import org.complitex.dictionary.util.AttributeUtil;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@Stateless
public class HeatmeterPeriodTypeStrategy extends TemplateStrategy {

    private static final String RESOURCE_BUNDLE = HeatmeterPeriodTypeStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long NAME = 3500;
    /**
     * Predefined heatmeter period type ids
     */
    public static final long OPERATING = 1;
    public static final long ADJUSTMENT = 2;
    /* Reserved heatmeter period types */
    private static final Set<Long> RESERVED_HEATMETER_PERIOD_TYPE_IDS = ImmutableSet.of(OPERATING, ADJUSTMENT);

    @Override
    public String getEntityTable() {
        return "heatmeter_period_type";
    }

    @Override
    protected List<Long> getListAttributeTypes() {
        return Lists.newArrayList(NAME);
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

    public List<DomainObject> getAll() {
        DomainObjectExample example = new DomainObjectExample();
        configureExample(example, ImmutableMap.<String, Long>of(), null);
        return (List<DomainObject>) find(example);
    }

    @Transactional
    @Override
    protected void deleteChecks(long objectId, Locale locale) throws DeleteException {
        if (RESERVED_HEATMETER_PERIOD_TYPE_IDS.contains(objectId)) {
            throw new DeleteException(ResourceUtil.getString(RESOURCE_BUNDLE, "delete_reserved_instance_error", locale));
        }
        super.deleteChecks(objectId, locale);
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
