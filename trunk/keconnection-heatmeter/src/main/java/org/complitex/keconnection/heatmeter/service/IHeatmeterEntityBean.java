package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.service.IEditBean;
import org.complitex.dictionary.service.IListBean;
import org.complitex.keconnection.heatmeter.entity.IHeatmeterEntity;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.10.12 17:50
 */
public interface IHeatmeterEntityBean<T extends IHeatmeterEntity> extends IListBean<T>, IEditBean<T> {
    List<T> getList(Long heatmeterId);
}
