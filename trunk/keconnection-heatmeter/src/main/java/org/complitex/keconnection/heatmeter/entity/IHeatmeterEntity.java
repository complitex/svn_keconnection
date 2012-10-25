package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.ILongId;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.10.12 17:37
 */
public interface IHeatmeterEntity extends ILongId{
    Long getHeatmeterId();

    void setHeatmeterId(Long heatmeterId);
}
