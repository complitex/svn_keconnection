/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.strategy;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;

import java.util.List;
import java.util.Locale;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.keconnection.organization.strategy.entity.Organization;

/**
 *
 * @author Artem
 */
public interface IKeConnectionOrganizationStrategy extends IOrganizationStrategy {

    String KECONNECTION_ORGANIZATION_STRATEGY_NAME = "KeConnectionOrganizationStrategy";
    /*
     * Attribute type ids
     */
    /**
     * Organization's short name.
     */
    long SHORT_NAME = 920;
    /**
     * Flag of organization being performer.
     */
    long PERFORMER = 921;
    /**
     * Flag of readiness to close operating month.
     */
    long READY_CLOSE_OPER_MONTH = 922;
    /**
     * Itself organization instance id.
     */
    long ITSELF_ORGANIZATION_OBJECT_ID = 0;
    /**
     * КИЕВЭНЕРГО
     */
    long KE_ORGANIZATION_OBJECT_ID = 1;

    List<Organization> getAllServicingOrganizations(Locale locale);

    String displayShortName(Long organizationId, Locale locale);

    /**
     * Returns organization that represents keconnection program module, i.e. "itself".
     * 
     * @return "Itself" organization.
     */
    DomainObject getItselfOrganization();

    List<Organization> getAllOuterOrganizations(Locale locale);

    List<Organization> find(DomainObjectExample example);

    void setReadyCloseOperatingMonthFlag(Organization organization);

    String displayShortNameAndCode(DomainObject organization, Locale locale);
}
