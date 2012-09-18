/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.strategy;

import java.util.List;
import java.util.Locale;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;

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
}
