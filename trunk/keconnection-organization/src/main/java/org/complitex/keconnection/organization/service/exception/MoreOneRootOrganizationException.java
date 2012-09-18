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

    public MoreOneRootOrganizationException() {
        super("Найдено более одной организации первого уровня.");
    }
}
