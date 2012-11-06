package org.complitex.keconnection.heatmeter.service.exception;

import org.complitex.dictionary.service.exception.AbstractException;
import org.complitex.keconnection.heatmeter.entity.HeatmeterWrapper;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.09.12 15:35
 */
public class OrganizationNotFoundException extends AbstractException {
    public OrganizationNotFoundException(HeatmeterWrapper heatmeterWrapper) {
        super("Организация не найдена для теплосчетчика {0}", heatmeterWrapper);
    }
}
