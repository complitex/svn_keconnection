/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.strategy;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;

import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public interface IKeConnectionOrganizationStrategy extends IOrganizationStrategy {

    String KECONNECTION_ORGANIZATION_STRATEGY_NAME = "KeConnectionOrganizationStrategy";
    /*
     * Attribute type ids
     */
    long SHORT_NAME = 920;

    List<DomainObject> getAllServicingOrganizations(Locale locale);

    String displayShortName(Long organizationId, Locale locale);
}
