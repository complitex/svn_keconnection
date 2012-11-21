package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.util.IdListUtil;
import org.complitex.keconnection.heatmeter.entity.HeatmeterAttribute;

import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 20.11.12 19:39
 */
public abstract class HeatmeterAttributeBean<T extends HeatmeterAttribute> extends AbstractBean{
    public abstract void save(T object);

    public abstract List<T> getList(Long heatmeterId, Date om);

    public abstract void delete(Long id);

    public void save(Long heatmeterId, Date om, List<T> list){
        if (heatmeterId != null) {
            List<T> db = getList(heatmeterId, om);

            for (T object : IdListUtil.getDiff(db, list)) {
                delete(object.getId());
            }
        }

        for (T object : list) {
            object.getPeriod().setHeatmeterId(heatmeterId);

            save(object);
        }
    }
}
