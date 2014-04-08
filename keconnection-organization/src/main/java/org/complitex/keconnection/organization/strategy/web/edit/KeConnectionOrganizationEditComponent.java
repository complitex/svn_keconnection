/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.strategy.web.edit;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.dictionary.web.component.DomainObjectComponentUtil;
import org.complitex.keconnection.organization.strategy.KeConnectionOrganizationStrategy;
import org.complitex.keconnection.organization.strategy.entity.Organization;
import org.complitex.keconnection.organization_type.strategy.KeConnectionOrganizationTypeStrategy;
import org.complitex.organization.strategy.web.edit.OrganizationEditComponent;

import javax.ejb.EJB;

/**
 *
 * @author Artem
 */
public class KeConnectionOrganizationEditComponent extends OrganizationEditComponent {

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeConnectionOrganizationStrategy organizationStrategy;

    @EJB
    private StringCultureBean stringBean;
    private WebMarkupContainer readyCloseOmSection;
    private WebMarkupContainer omSection;

    public KeConnectionOrganizationEditComponent(String id, boolean disabled) {
        super(id, disabled);
    }

    @Override
    protected Organization getDomainObject() {
        return (Organization) super.getDomainObject();
    }

    @Override
    protected void init() {
        super.init();

        final boolean isDisabled = isDisabled();

        final Organization organization = getDomainObject();

        //Readiness to close operating month. It is servicing organization only attribute.
        {
            readyCloseOmSection = new WebMarkupContainer("readyCloseOmSection");
            readyCloseOmSection.setOutputMarkupPlaceholderTag(true);
            add(readyCloseOmSection);

            final long attributeTypeId = KeConnectionOrganizationStrategy.READY_CLOSE_OPER_MONTH;
            Attribute attribute = organization.getAttribute(attributeTypeId);
            if (attribute == null) {
                attribute = new Attribute();
                attribute.setAttributeTypeId(attributeTypeId);
                attribute.setObjectId(organization.getId());
                attribute.setAttributeId(1L);
                attribute.setLocalizedValues(stringBean.newStringCultures());
            }
            final EntityAttributeType attributeType =
                    organizationStrategy.getEntity().getAttributeType(attributeTypeId);
            readyCloseOmSection.add(new Label("label",
                    DomainObjectComponentUtil.labelModel(attributeType.getAttributeNames(), getLocale())));
            readyCloseOmSection.add(new WebMarkupContainer("required").setVisible(attributeType.isMandatory()));

            readyCloseOmSection.add(DomainObjectComponentUtil.newInputComponent("organization", getStrategyName(),
                    organization, attribute, getLocale(), true));

            //initial visibility
            readyCloseOmSection.setVisible(isServicingOrganization());
        }

        //Operating month. Only for servicing organizations.
        {
            omSection = new WebMarkupContainer("omSection");
            omSection.setOutputMarkupPlaceholderTag(true);
            add(omSection);

            omSection.add(new Label("om", organization.getOperatingMonth(getLocale())));

            //initial visibility
            omSection.setVisibilityAllowed(!isDisabled);
            omSection.setVisible(isServicingOrganization());
        }
    }

    @Override
    protected void onOrganizationTypeChanged(AjaxRequestTarget target) {
        super.onOrganizationTypeChanged(target);

        //Readiness to close operating month.
        {
            boolean wasVisible = readyCloseOmSection.isVisible();
            readyCloseOmSection.setVisible(isServicingOrganization());
            boolean visibleNow = readyCloseOmSection.isVisible();
            if (wasVisible ^ visibleNow) {
                target.add(readyCloseOmSection);
            }
        }

        //Operating month.
        {
            boolean wasVisible = omSection.isVisible();
            omSection.setVisible(isServicingOrganization());
            boolean visibleNow = omSection.isVisible();
            if (wasVisible ^ visibleNow) {
                target.add(omSection);
            }
        }
    }

    @Override
    protected String getStrategyName() {
        return KeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME;
    }

    public boolean isServicingOrganization() {
        for (DomainObject organizationType : getOrganizationTypesModel().getObject()) {
            if (organizationType.getId().equals(KeConnectionOrganizationTypeStrategy.SERVICING_ORGANIZATION_TYPE)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean isOrganizationTypeEnabled() {
        Long organizationId = getDomainObject().getId();
        return !(organizationId != null && (organizationId == KeConnectionOrganizationStrategy.ITSELF_ORGANIZATION_OBJECT_ID))
                && super.isOrganizationTypeEnabled();
    }

    @Override
    protected void onPersist() {
        super.onPersist();

        final Organization organization = getDomainObject();

        if (!isServicingOrganization()) {
            //Readiness to close operating month.
            organization.removeAttribute(KeConnectionOrganizationStrategy.READY_CLOSE_OPER_MONTH);
        }
    }
}
