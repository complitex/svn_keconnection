package org.complitex.keconnection.heatmeter.util;

import com.google.common.collect.Range;
import com.google.common.collect.Ranges;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriod;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.11.12 17:32
 */
public class HeatmeterPeriodUtil {
    public static Range<Date> range(HeatmeterPeriod p){
        return  p.getEndDate() != null ? Ranges.closed(p.getBeginDate(), p.getEndDate()) : Ranges.atLeast(p.getBeginDate());
    }
}
