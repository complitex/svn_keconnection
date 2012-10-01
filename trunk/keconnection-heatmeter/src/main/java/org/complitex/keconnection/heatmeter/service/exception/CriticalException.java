package org.complitex.keconnection.heatmeter.service.exception;

import org.complitex.dictionary.service.exception.AbstractException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.10.12 16:20
 */
public class CriticalException extends AbstractException {
    public CriticalException(Throwable cause) {
        super(cause, "Критическая ошибка");
    }
}
