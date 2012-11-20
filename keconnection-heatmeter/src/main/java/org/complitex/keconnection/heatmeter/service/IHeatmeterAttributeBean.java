package org.complitex.keconnection.heatmeter.service;

import org.complitex.keconnection.heatmeter.entity.HeatmeterAttribute;

import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 20.11.12 19:42
 */
public interface IHeatmeterAttributeBean<T extends HeatmeterAttribute> {
    void save(T object);

    List<T> getList(Long heatmeterId, Date om);

    void delete(Long id);
}
