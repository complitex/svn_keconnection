package org.complitex.keconnection.heatmeter.service.exception;

import org.complitex.dictionary.service.exception.AbstractException;
import org.complitex.keconnection.heatmeter.entity.HeatmeterWrapper;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.09.12 15:35
 */
public class BuildingNotFoundException extends AbstractException{
    public BuildingNotFoundException(HeatmeterWrapper heatmeterWrapper) {
        super("Дом не найден для теплосчетчика {0}", heatmeterWrapper);
    }
}
