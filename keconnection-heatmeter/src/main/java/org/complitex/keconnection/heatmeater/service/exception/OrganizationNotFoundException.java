package org.complitex.keconnection.heatmeater.service.exception;

import org.complitex.dictionary.service.exception.AbstractException;
import org.complitex.keconnection.heatmeater.entity.HeatmeaterWrapper;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.09.12 15:35
 */
public class OrganizationNotFoundException extends AbstractException {
    public OrganizationNotFoundException(HeatmeaterWrapper heatmeaterWrapper) {
        super("Организация не найдена для теплосчетчика {0}", heatmeaterWrapper);
    }
}