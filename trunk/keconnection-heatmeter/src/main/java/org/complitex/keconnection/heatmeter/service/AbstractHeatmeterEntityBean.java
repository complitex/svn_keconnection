package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.IHeatmeterEntity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.10.12 17:37
 */
public abstract class AbstractHeatmeterEntityBean<T extends IHeatmeterEntity> extends AbstractBean
        implements  IHeatmeterEntityBean<T> {
}
