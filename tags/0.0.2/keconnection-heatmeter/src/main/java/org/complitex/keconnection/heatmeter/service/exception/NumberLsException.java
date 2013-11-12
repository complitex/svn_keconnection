package org.complitex.keconnection.heatmeter.service.exception;

import org.complitex.dictionary.service.exception.AbstractException;
import org.complitex.keconnection.heatmeter.entity.HeatmeterWrapper;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.10.12 18:30
 */
public class NumberLsException extends AbstractException{
    public NumberLsException(HeatmeterWrapper heatmeterWrapper) {
        super("Не числовой л/с счетчика {0}", heatmeterWrapper);
    }
}
