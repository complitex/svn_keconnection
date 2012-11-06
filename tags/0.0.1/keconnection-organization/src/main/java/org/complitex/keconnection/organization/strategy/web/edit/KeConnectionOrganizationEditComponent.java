/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.strategy.web.edit;

import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.web.component.DomainObjectInputPanel;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.complitex.keconnection.organization.strategy.entity.Organization;
import org.complitex.keconnection.organization_type.strategy.KeConnectionOrganizationTypeStrategy;
import org.complitex.organization.strategy.web.edit.OrganizationEditComponent;

/**
 *
 * @author Artem
 */
public class KeConnectionOrganizationEditComponent extends OrganizationEditComponent {

    @EJB(name = IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME)
    private IKeConnectionOrganizationStrategy organizationStrategy;
    @EJB
    private StringCultureBean stringBean;
    private WebMarkupContainer readyCloseOperMonthSection;
    private WebMarkupContainer operatingMonthSection;

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
            readyCloseOperMonthSection = new WebMarkupContainer("readyCloseOperMonthSection");
            readyCloseOperMonthSection.setOutputMarkupPlaceholderTag(true);
            add(readyCloseOperMonthSection);

            final long attributeTypeId = IKeConnectionOrganizationStrategy.READY_CLOSE_OPER_MONTH;
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
            readyCloseOperMonthSection.add(new Label("label",
                    DomainObjectInputPanel.labelModel(attributeType.getAttributeNames(), getLocale())));
            readyCloseOperMonthSection.add(new WebMarkupContainer("required").setVisible(attributeType.isMandatory()));

            readyCloseOperMonthSection.add(DomainObjectInputPanel.newInputComponent("organization", getStrategyName(),
                    organization, attribute, getLocale(), true));

            //initial visibility
            readyCloseOperMonthSection.setVisible(isServicingOrganization());
        }

        //Operating month. Only for servicing organizations.
        {
            operatingMonthSection = new WebMarkupContainer("operatingMonthSection");
            operatingMonthSection.setOutputMarkupPlaceholderTag(true);
            add(operatingMonthSection);

            operatingMonthSection.add(new Label("operatingMonth", organization.getOperatingMonth(getLocale())));

            //initial visibility
            operatingMonthSection.setVisibilityAllowed(!isDisabled);
            operatingMonthSection.setVisible(isServicingOrganization());
        }
    }

    @Override
    protected void onOrganizationTypeChanged(AjaxRequestTarget target) {
        super.onOrganizationTypeChanged(target);

        //Readiness to close operating month.
        {
            boolean wasVisible = readyCloseOperMonthSection.isVisible();
            readyCloseOperMonthSection.setVisible(isServicingOrganization());
            boolean visibleNow = readyCloseOperMonthSection.isVisible();
            if (wasVisible ^ visibleNow) {
                target.add(readyCloseOperMonthSection);
            }
        }

        //Operating month.
        {
            boolean wasVisible = operatingMonthSection.isVisible();
            operatingMonthSection.setVisible(isServicingOrganization());
            boolean visibleNow = operatingMonthSection.isVisible();
            if (wasVisible ^ visibleNow) {
                target.add(operatingMonthSection);
            }
        }
    }

    @Override
    protected String getStrategyName() {
        return IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME;
    }

    public boolean isServicingOrganization() {
        for (DomainObject organizationType : getOrganizationTypesModel().getObject()) {
            if (organizationType.getId().equals(KeConnectionOrganizationTypeStrategy.SERVICING_ORGANIZATION)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean isOrganizationTypeEnabled() {
        Long organizationId = getDomainObject().getId();
        return !(organizationId != null && (organizationId == IKeConnectionOrganizationStrategy.ITSELF_ORGANIZATION_OBJECT_ID))
                && super.isOrganizationTypeEnabled();
    }

    @Override
    protected void onPersist() {
        super.onPersist();

        final Organization organization = getDomainObject();

        if (!isServicingOrganization()) {
            //Readiness to close operating month.
            organization.removeAttribute(IKeConnectionOrganizationStrategy.READY_CLOSE_OPER_MONTH);
        }
    }
}
