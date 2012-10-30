package org.complitex.keconnection.heatmeter.service;

import com.google.common.collect.Range;
import com.google.common.collect.Ranges;
import org.complitex.keconnection.heatmeter.entity.*;

import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.ADJUSTMENT;
import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.OPERATION;
import static org.complitex.keconnection.heatmeter.entity.HeatmeterValidateStatus.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.10.12 12:58
 */
@Stateless
public class HeatmeterService {
    public HeatmeterValidate validate(Heatmeter heatmeter){
        //validate period
        boolean hasOpenOperation = false;
        boolean hasOpenAdjustment = false;

        for (HeatmeterPeriod period : heatmeter.getPeriods()){
            if (period.getBeginDate() != null && period.getEndDate() != null && period.getBeginDate().after(period.getEndDate())){
                return new HeatmeterValidate(ERROR_PERIOD_BEGIN_DATE_AFTER_END_DATE,
                        period.getBeginDate(), period.getEndDate());
            }

            if (period.getBeginDate() == null){
                return new HeatmeterValidate(ERROR_PERIOD_BEGIN_DATE_REQUIRED);
            }

            if (period.getType() == null){
                return new HeatmeterValidate(ERROR_PERIOD_TYPE_REQUIRED);
            }else {
                if (period.getEndDate() == null){
                    //Одновременно без даты окончания может быть не более двух периодов: один период функционирования
                    //и какой либо другой (пока возможен только период юстировки).
                    if (OPERATION.equals(period.getType())){
                        if (hasOpenOperation){
                            return new HeatmeterValidate(ERROR_PERIOD_MORE_THAN_TWO_OPEN_OPERATION,
                                    period.getBeginDate());
                        }

                        hasOpenOperation = true;
                    }else if (ADJUSTMENT.equals(period.getType())){
                        if (hasOpenAdjustment){
                            return new HeatmeterValidate(ERROR_PERIOD_MORE_THAN_TWO_OPEN_ADJUSTMENT,
                                    period.getBeginDate());
                        }

                        hasOpenAdjustment = true;
                    }
                }
            }
        }

        //однотипные периоды не могут пересекаться
        List<HeatmeterPeriod> periods = heatmeter.getPeriods();
        for (int i = 0; i < periods.size() - 1 ; i++) {
            HeatmeterPeriod p1 = periods.get(i);

            for (int j = i+1; j < periods.size(); j++) {
                HeatmeterPeriod p2 = periods.get(j);

                if (p1.getType() != null && p1.getType().equals(p2.getType())){
                    Range<Date> r1 = p1.getEndDate() != null
                            ? Ranges.closed(p1.getBeginDate(), p1.getEndDate())
                            : Ranges.atLeast(p1.getBeginDate());

                    Range<Date> r2 = p2.getEndDate() != null
                            ? Ranges.closed(p2.getBeginDate(), p2.getEndDate())
                            : Ranges.atLeast(p2.getBeginDate());

                    if (r1.isConnected(r2)){
                        return new HeatmeterValidate(ERROR_PERIOD_INTERSECTION, p1.getBeginDate(), p1.getEndDate(),
                                p2.getBeginDate(), p2.getEndDate());
                    }
                }
            }
        }

        for (HeatmeterPeriod p1 : periods){
            if (ADJUSTMENT.equals(p1.getType())) {
                boolean encloses = false;

                Range<Date> r1 = p1.getEndDate() != null
                        ? Ranges.closed(p1.getBeginDate(), p1.getEndDate())
                        : Ranges.atLeast(p1.getBeginDate());

                for (HeatmeterPeriod p2 : periods){
                    if (OPERATION.equals(p2.getType())){
                        Range<Date> r2 = p2.getEndDate() != null
                                ? Ranges.closed(p2.getBeginDate(), p2.getEndDate())
                                : Ranges.atLeast(p2.getBeginDate());

                        if (r2.encloses(r1)){
                            encloses = true;
                            break;
                        }
                    }
                }

                if (!encloses){
                    return new HeatmeterValidate(ERROR_PERIOD_OPERATION_MUST_ENCLOSES_ADJUSTMENT,
                            p1.getBeginDate(), p1.getEndDate());
                }
            }
        }

        //validate connection
        for (HeatmeterConnection connection : heatmeter.getConnections()){
            if (connection.getBeginDate() == null){
                return new HeatmeterValidate(ERROR_CONNECTION_BEGIN_DATE_REQUIRED);
            }

            if (connection.getBuildingCodeId() == null){
                return new HeatmeterValidate(ERROR_CONNECTION_NOT_FOUND);
            }
        }

        //validate payload
        for (HeatmeterPayload payload : heatmeter.getPayloads()){
            if (payload.getBeginDate() == null){
                return new HeatmeterValidate(ERROR_PAYLOAD_BEGIN_DATE_REQUIRED);
            }

            if (payload.getPayload1() == null || payload.getPayload2() == null || payload.getPayload3() == null){
                return new HeatmeterValidate(ERROR_PAYLOAD_VALUES_REQUIRED);
            }
        }

        //validate consumption
        for(HeatmeterConsumption consumption : heatmeter.getConsumptions()){
            if (consumption.getReadoutDate() == null) {
                return new HeatmeterValidate(ERROR_CONSUMPTION_READOUT_DATE_REQUIRED);
            }

            if (consumption.getConsumption() == null){
                return new HeatmeterValidate(ERROR_CONSUMPTION_VALUE_REQUIRED);
            }
        }

        return new HeatmeterValidate(VALID);
    }
}
