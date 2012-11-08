package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.ILongId;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.11.12 13:30
 */
public interface IHeatmeterAttribute extends ILongId {
    HeatmeterPeriod getPeriod();
}
