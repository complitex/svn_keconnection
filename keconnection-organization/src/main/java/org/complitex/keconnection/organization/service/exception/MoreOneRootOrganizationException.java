/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.service.exception;

import javax.ejb.ApplicationException;
import org.complitex.dictionary.service.exception.AbstractException;

/**
 *
 * @author Artem
 */
@ApplicationException(rollback = true)
public class MoreOneRootOrganizationException extends AbstractException {

    public MoreOneRootOrganizationException(String[] organizationNames) {
        super("Найдено более одной организации первого уровня: {0}", formatOrganzationNames(organizationNames));
    }

    private static String formatOrganzationNames(String[] organizationNames) {
        if (organizationNames != null && organizationNames.length > 0) {
            StringBuilder result = new StringBuilder("");
            for (int i = 0, length = organizationNames.length; i < length; i++) {
                result.append(organizationNames[i]);
                if (i < length - 1) {
                    result.append(", ");
                }
            }
            return result.toString();
        } else {
            throw new IllegalArgumentException("At least one organzation must be specified.");
        }
    }
}
