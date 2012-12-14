package org.complitex.keconnection.heatmeter.service;

import com.google.common.collect.Lists;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.keconnection.heatmeter.entity.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.complitex.dictionary.util.DateUtil.*;
import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodSubType.ADJUSTMENT;
import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodSubType.OPERATING;
import static org.complitex.keconnection.heatmeter.entity.HeatmeterValidateStatus.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.10.12 12:58
 */
@Stateless
public class HeatmeterService {
    @EJB
    private SessionBean sessionBean;

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

        //inputs
        heatmeterValidate = validateInputs(heatmeter);
        if (!VALID.equals(heatmeterValidate.getStatus())){
            return heatmeterValidate;
        }

        return new HeatmeterValidate(VALID);
    }

    public HeatmeterValidate validatePeriods(Heatmeter heatmeter){
        boolean hasOpenOperation = false;
        boolean hasOpenAdjustment = false;

        List<HeatmeterOperation> operations = heatmeter.getOperations();
        for (int i = 0; i < operations.size(); i++) {
            HeatmeterPeriod p1 = operations.get(i);

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

                for (HeatmeterPeriod p2 : operations){
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
            if (i < operations.size() - 1) {
                for (int j = i+1; j < operations.size(); j++) {
                    HeatmeterPeriod p2 = operations.get(j);

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

        List<Long> organizationIds = sessionBean.getUserOrganizationTreeObjectIds();

        for (int i = 0; i < connections.size(); ++i){
            HeatmeterConnection c1 = connections.get(i);

            if (!sessionBean.isAdmin() && !organizationIds.contains(c1.getOrganizationId())){
                return new HeatmeterValidate(ERROR_CONNECTION_NOT_USER_ORGANIZATION, c1);
            }

            if (c1.getBeginDate() == null){
                return new HeatmeterValidate(ERROR_CONNECTION_BEGIN_DATE_REQUIRED);
            }

            if (c1.getEndDate() != null && c1.getBeginDate().after(c1.getEndDate())){
                return new HeatmeterValidate(ERROR_CONNECTION_BEGIN_DATE_AFTER_END_DATE, c1);
            }

            if (c1.getObjectId() == null){
                return new HeatmeterValidate(ERROR_CONNECTION_NOT_FOUND);
            }

            //периоды подключений по одному адресу не должны пересекаться
            if (i < connections.size() - 1) {
                for (int j = i + 1; j < connections.size(); ++j){
                    HeatmeterConnection c2 = connections.get(j);

                    if(isSameMonth(c1.getBeginOm(), c1.getBeginOm())
                            && c1.getObjectId().equals(c2.getObjectId())
                            && c2.getBeginDate() != null
                            && c1.isConnected(c2)){
                        return new HeatmeterValidate(ERROR_CONNECTION_INTERSECTION, c1, c2);
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

            if (hp.getBeginDate() == null){
                return new HeatmeterValidate(ERROR_PAYLOAD_BEGIN_DATE_REQUIRED);
            }

            if (hp.getEndDate() != null && hp.getBeginDate().after(hp.getEndDate())){
                return new HeatmeterValidate(ERROR_PAYLOAD_BEGIN_DATE_AFTER_END_DATE, hp);
            }

            if (hp.getPayload1() == null || hp.getPayload2() == null || hp.getPayload3() == null){
                return new HeatmeterValidate(ERROR_PAYLOAD_VALUES_REQUIRED);
            }

            if (hp.getPayload1().add(hp.getPayload2()).add(hp.getPayload3()).doubleValue() != 100){
                return new HeatmeterValidate(ERROR_PAYLOAD_SUM_100, hp);
            }

            //периоды распределений не должны пересекаться
            if (i < payloads.size() - 1) {
                for (int j = i + 1; j < payloads.size(); ++j){
                    HeatmeterPayload hp2 = payloads.get(j);

                    if(isSameMonth(hp.getBeginOm(), hp2.getBeginOm())
                            && hp2.getBeginDate() != null
                            && hp.isConnected(hp2)){
                        return new HeatmeterValidate(ERROR_PAYLOAD_INTERSECTION, hp, hp2);
                    }
                }
            }
        }

        return new HeatmeterValidate(VALID);
    }

    public HeatmeterValidate validateInputs(Heatmeter heatmeter){
        List<HeatmeterInput> inputs = heatmeter.getInputs();
        for (int i = 0; i < inputs.size(); ++i){
            HeatmeterInput c1 = inputs.get(i);

            if (c1.getEndDate() == null) {
                return new HeatmeterValidate(ERROR_INPUT_END_DATE_REQUIRED);
            }

            if (c1.getValue() == null){
                return new HeatmeterValidate(ERROR_INPUT_VALUE_REQUIRED);
            }

            if (i < inputs.size() - 1) {
                for (int j = i + 1; j < inputs.size(); ++j){
                    HeatmeterInput c2 = inputs.get(j);

                    if (DateUtil.isTheSameDay(c1.getEndDate(), c2.getEndDate()))
                        if (isSameMonth(c1.getBeginOm(), c2.getBeginOm())) {
                            return new HeatmeterValidate(ERROR_INPUT_INTERSECTION, c1.getEndDate());
                        }
                }
            }
        }

        return new HeatmeterValidate(VALID);
    }
    
    public void calculateConsumptions(Iterable<HeatmeterPayload> payloads, Iterable<HeatmeterInput> inputs) {
        List<HeatmeterPayload> payloadsList = Lists.newArrayList(payloads);

        //sort payload in ascending order by begin date
        Collections.sort(payloadsList, new Comparator<HeatmeterPayload>() {

            @Override
            public int compare(HeatmeterPayload p1, HeatmeterPayload p2) {
                return p1.getBeginDate().compareTo(p2.getBeginDate());
            }
        });

        for (HeatmeterInput input : inputs) {
            calculateConsumption(payloadsList, input);
        }
    }

    private void calculateConsumption(List<HeatmeterPayload> payloads, HeatmeterInput input) {
        HeatmeterConsumption c = input.getFirstConsumption();
        if (c.getId() == null) { // new consumption
            c.setBeginDate(input.getBeginDate());
            c.setEndDate(input.getEndDate());
            c.setOm(input.getEndOm());
        }
        c.setConsumption1(BigDecimal.ZERO);
        c.setConsumption2(BigDecimal.ZERO);
        c.setConsumption3(BigDecimal.ZERO);

        int inputPeriodLength = getDaysDiff(input.getBeginDate(), input.getEndDate());

        if (inputPeriodLength != 0) {
            for (HeatmeterPayload p : payloads) {
                if (!p.getBeginDate().after(input.getBeginDate())
                        && p.getEndDate().after(input.getBeginDate())) {
                    Date subPeriodEndDate = getMin(p.getEndDate(), input.getEndDate());
                    int subPeriodLength = getDaysDiff(input.getBeginDate(), subPeriodEndDate);
                    addSubPeriodCalculations(input.getValue(), inputPeriodLength, subPeriodLength, c,
                            p.getPayload2(), p.getPayload3());
                } else if (p.getBeginDate().after(input.getBeginDate())
                        && p.getBeginDate().before(input.getEndDate())) {
                    Date subPeriodEndDate = getMin(p.getEndDate(), input.getEndDate());
                    int subPeriodLength = getDaysDiff(p.getBeginDate(), subPeriodEndDate);
                    addSubPeriodCalculations(input.getValue(), inputPeriodLength, subPeriodLength, c,
                            p.getPayload2(), p.getPayload3());
                }
            }
            c.setConsumption1(input.getValue().subtract(c.getConsumption2()).subtract(c.getConsumption3()));
        }
    }

    private void addSubPeriodCalculations(BigDecimal value, int periodLength, int subPeriodLength,
            HeatmeterConsumption consumption, BigDecimal payload2, BigDecimal payload3) {
        consumption.setConsumption2(consumption.getConsumption2().add(BigDecimal.valueOf(
                (value.doubleValue() * subPeriodLength * payload2.doubleValue()) / (periodLength * 100))));

        consumption.setConsumption3(consumption.getConsumption3().add(BigDecimal.valueOf(
                (value.doubleValue() * subPeriodLength * payload3.doubleValue()) / (periodLength * 100))));
    }
}