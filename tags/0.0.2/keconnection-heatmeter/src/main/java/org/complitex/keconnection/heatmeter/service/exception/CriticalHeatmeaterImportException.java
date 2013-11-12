package org.complitex.keconnection.heatmeter.service.exception;

import org.complitex.dictionary.service.exception.AbstractException;
import org.complitex.keconnection.heatmeter.entity.HeatmeterWrapper;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.10.12 16:20
 */
public class CriticalHeatmeaterImportException extends AbstractException {
    public CriticalHeatmeaterImportException(Throwable cause, HeatmeterWrapper heatmeterWrapper) {
        super(cause, "Критическая ошибка импорта счетчика: {0}", heatmeterWrapper);
    }
}
