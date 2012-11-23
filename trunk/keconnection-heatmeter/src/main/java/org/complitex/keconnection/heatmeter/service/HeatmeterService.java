package org.complitex.keconnection.heatmeter.service;

import org.complitex.keconnection.heatmeter.entity.*;

import javax.ejb.Stateless;
import java.util.List;

import static org.complitex.dictionary.util.DateUtil.isSameMonth;
import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodSubType.ADJUSTMENT;
import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodSubType.OPERATING;
import static org.complitex.keconnection.heatmeter.entity.HeatmeterValidateStatus.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.10.12 12:58
 */
@Stateless
public class HeatmeterService {
    public HeatmeterValidate validate(Heatmeter heatmeter){
        HeatmeterValidate heatmeterValidate;

        if (heatmeter.getLs() == null){
            return new HeatmeterValidate(HEATMETER_LS_REQUIRED);
        }

        if (heatmeter.getType() == null){
            return new HeatmeterValidate(HEATMETER_TYPE_REQUIRED);
        }

        //periods
        heatmeterValidate = validatePeriods(heatmeter);
        if (!VALID.equals(heatmeterValidate.getStatus())){
            return heatmeterValidate;
        }

        //connection
        heatmeterValidate = validateConnections(heatmeter);
        if (!VALID.equals(heatmeterValidate.getStatus())){
            return heatmeterValidate;
        }

        //payloads
        heatmeterValidate = validatePayloads(heatmeter);
        if (!VALID.equals(heatmeterValidate.getStatus())){
            return heatmeterValidate;
        }
//
//        //consumption
//        return validateConsumptions(heatmeter);

        return new HeatmeterValidate(VALID);
    }

    public HeatmeterValidate validatePeriods(Heatmeter heatmeter){
        boolean hasOpenOperation = false;
        boolean hasOpenAdjustment = false;

        List<HeatmeterPeriod> periods = heatmeter.getPeriods();
        for (int i = 0; i < periods.size(); i++) {
            HeatmeterPeriod p1 = periods.get(i);

            if (p1.getBeginDate() == null){
                return new HeatmeterValidate(ERROR_PERIOD_BEGIN_DATE_REQUIRED);
            }

            if (p1.getEndDate() != null && p1.getBeginDate().after(p1.getEndDate())){
                return new HeatmeterValidate(ERROR_PERIOD_BEGIN_DATE_AFTER_END_DATE, p1);
            }

            if (p1.getType() == null){
                return new HeatmeterValidate(ERROR_PERIOD_TYPE_REQUIRED);
            }else {
                if (p1.getEndDate() == null){
                    //Одновременно без даты окончания может быть не более двух периодов: один период функционирования
                    //и какой либо другой (пока возможен только период юстировки).
                    if (OPERATING.equals(p1.getSubType())){
                        if (hasOpenOperation){
                            return new HeatmeterValidate(ERROR_PERIOD_MORE_THAN_TWO_OPEN_OPERATION, p1);
                        }

                        hasOpenOperation = true;
                    }else {
                        if (ADJUSTMENT.equals(p1.getSubType())) {
                            if (hasOpenAdjustment) {
                                return new HeatmeterValidate(ERROR_PERIOD_MORE_THAN_TWO_OPEN_ADJUSTMENT, p1);
                            }

                            hasOpenAdjustment = true;
                        }
                    }
                }
            }

            //период юстировки должен полностью принадлежать периоду функционирования
            if (ADJUSTMENT.equals(p1.getSubType())) {
                boolean encloses = false;

                for (HeatmeterPeriod p2 : periods){
                    if (OPERATING.equals(p2.getSubType())
                            && isSameMonth(p1.getBeginOm(), p2.getBeginOm())
                            && p2.isEncloses(p1)){
                        encloses = true;
                        break;
                    }
                }

                if (!encloses){
                    return new HeatmeterValidate(ERROR_PERIOD_OPERATION_MUST_ENCLOSES_ADJUSTMENT, p1);
                }
            }

            //однотипные периоды не могут пересекаться
            if (i < periods.size() - 1) {
                for (int j = i+1; j < periods.size(); j++) {
                    HeatmeterPeriod p2 = periods.get(j);

                    if (p1.getType() != null && p1.getType().equals(p2.getType())
                            && isSameMonth(p1.getBeginOm(), p2.getBeginOm())
                            && p2.getBeginDate() != null
                            && p1.isConnected(p2)){

                        return new HeatmeterValidate(ERROR_PERIOD_INTERSECTION, p1, p2);
                    }
                }
            }
        }

        return new HeatmeterValidate(VALID);
    }

    public HeatmeterValidate validateConnections(Heatmeter heatmeter){
        List<HeatmeterConnection> connections = heatmeter.getConnections();

        if (heatmeter.getId() == null){
            if (connections.isEmpty()){
                return new HeatmeterValidate(ERROR_CONNECTION_AT_LEAST_ONE_CONNECTION);
            }
        }

        for (int i = 0; i < connections.size(); ++i){
            HeatmeterConnection c1 = connections.get(i);
            HeatmeterPeriod p = c1.getPeriod();

            if (p.getBeginDate() == null){
                return new HeatmeterValidate(ERROR_CONNECTION_BEGIN_DATE_REQUIRED);
            }

            if (p.getEndDate() != null && p.getBeginDate().after(p.getEndDate())){
                return new HeatmeterValidate(ERROR_CONNECTION_BEGIN_DATE_AFTER_END_DATE, p);
            }

            if (c1.getBuildingCodeId() == null){
                return new HeatmeterValidate(ERROR_CONNECTION_NOT_FOUND);
            }

            //периоды подключений по одному адресу не должны пересекаться
            if (i < connections.size() - 1) {
                for (int j = i + 1; j < connections.size(); ++j){
                    HeatmeterConnection c2 = connections.get(j);
                    HeatmeterPeriod p2 = c2.getPeriod();

                    if(isSameMonth(p.getBeginOm(), p.getBeginOm())
                            && c1.getBuildingCodeId().equals(c2.getBuildingCodeId())
                            && p2.getBeginDate() != null
                            && p.isConnected(p2)){
                        return new HeatmeterValidate(ERROR_CONNECTION_INTERSECTION, p, p2);
                    }
                }
            }
        }

        return new HeatmeterValidate(VALID);
    }

    public HeatmeterValidate validatePayloads(Heatmeter heatmeter){
        List<HeatmeterPayload> payloads = heatmeter.getPayloads();
        for (int i = 0; i < payloads.size(); ++i){
            HeatmeterPayload hp = payloads.get(i);
            HeatmeterPeriod p = hp.getPeriod();

            if (p.getBeginDate() == null){
                return new HeatmeterValidate(ERROR_PAYLOAD_BEGIN_DATE_REQUIRED);
            }

            if (p.getEndDate() != null && p.getBeginDate().after(p.getEndDate())){
                return new HeatmeterValidate(ERROR_PAYLOAD_BEGIN_DATE_AFTER_END_DATE, p);
            }

            if (hp.getPayload1() == null || hp.getPayload2() == null || hp.getPayload3() == null){
                return new HeatmeterValidate(ERROR_PAYLOAD_VALUES_REQUIRED);
            }

            if (hp.getPayload1().add(hp.getPayload2()).add(hp.getPayload3()).doubleValue() != 100){
                return new HeatmeterValidate(ERROR_PAYLOAD_SUM_100, p);
            }

            //периоды распределений не должны пересекаться
            if (i < payloads.size() - 1) {
                for (int j = i + 1; j < payloads.size(); ++j){
                    HeatmeterPayload hp2 = payloads.get(j);
                    HeatmeterPeriod p2 = hp2.getPeriod();

                    if(isSameMonth(p.getBeginOm(), p2.getBeginOm())
                            && p2.getBeginDate() != null
                            && p.isConnected(p2)){
                        return new HeatmeterValidate(ERROR_PAYLOAD_INTERSECTION, p, p2);
                    }
                }
            }
        }

        return new HeatmeterValidate(VALID);
    }
//
//    public HeatmeterValidate validateConsumptions(Heatmeter heatmeter){
//        List<HeatmeterConsumption> consumptions = heatmeter.getConsumptions();
//        for (int i = 0; i < consumptions.size(); ++i){
//            HeatmeterConsumption c1 = consumptions.get(i);
//
//            if (c1.getReadoutDate() == null) {
//                return new HeatmeterValidate(ERROR_CONSUMPTION_READOUT_DATE_REQUIRED);
//            }
//
//            if (c1.getConsumption() == null){
//                return new HeatmeterValidate(ERROR_CONSUMPTION_VALUE_REQUIRED);
//            }
//
//            if (i < consumptions.size() - 1) {
//                for (int j = i + 1; j < consumptions.size(); ++j){
//                    HeatmeterConsumption c2 = consumptions.get(j);
//
//                    if(isSameMonth(c1.getOm(), c2.getOm())
//                            && DateUtil.isTheSameDay(c1.getReadoutDate(), c2.getReadoutDate())){
//                        return new HeatmeterValidate(ERROR_CONSUMPTION_INTERSECTION, c1.getReadoutDate());
//                    }
//                }
//            }
//        }
//
//        return new HeatmeterValidate(VALID);
//    }
//
//
}
