package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.HeatmeterAttribute;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 20.11.12 19:39
 */
public abstract class HeatmeterAttributeBean<T extends HeatmeterAttribute> extends AbstractBean
        implements IHeatmeterAttributeBean<T>{
    //abstract class used for ejb lookup
}
