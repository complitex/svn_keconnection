package org.complitex.keconnection.heatmeter.util;

import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriod;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.11.12 17:32
 */
public class HeatmeterPeriodUtil {
    public static <T extends HeatmeterPeriod> T firstConnectedPeriod(List<T> list, HeatmeterPeriod period){
        for (T t : list){
            if (t.isConnected(period)){
                return t;
            }
        }

        return null;
    }

    public static <T extends HeatmeterPeriod> T lastConnectedPeriod(List<T> list, HeatmeterPeriod period){
        for (int i = list.size()-1; i > -1; --i){
            T t = list.get(i);

            if (t.isConnected(period)){
                return t;
            }
        }

        return null;
    }
}
