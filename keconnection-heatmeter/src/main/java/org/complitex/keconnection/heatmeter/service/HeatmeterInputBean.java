package org.complitex.keconnection.heatmeter.service;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConsumption;
import org.complitex.keconnection.heatmeter.entity.HeatmeterInput;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPayload;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static org.complitex.dictionary.util.DateUtil.getDaysDiff;
import static org.complitex.dictionary.util.DateUtil.getMin;
import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.INPUT;

/**
 *
 * @author Artem
 */
@XmlMapper
@Stateless
public class HeatmeterInputBean extends HeatmeterPeriodBean<HeatmeterInput> {

    @EJB
    private HeatmeterConsumptionBean heatmeterConsumptionBean;

    @Override
    public HeatmeterPeriodType getType() {
        return INPUT;
    }

    @Transactional
    @Override
    public void insertAdditionalInfo(HeatmeterInput info) {
        sqlSession().insert("insertHeatmeterInput", info);
        updateConsumptions(info.getId(), info.getConsumptions());
    }

    @Transactional
    @Override
    public void updateAdditionalInfo(HeatmeterInput info) {
        sqlSession().update("updateHeatmeterInput", info);
        updateConsumptions(info.getId(), info.getConsumptions());
    }

    private void updateConsumptions(long inputId, List<HeatmeterConsumption> consumptions) {
        for (HeatmeterConsumption consumption : consumptions) {
            consumption.setHeatmeterInputId(inputId);
            heatmeterConsumptionBean.save(consumption);
        }
    }

    public List<HeatmeterInput> getHeatmeterInputs(Long heatmeterId, Date om) {
        return sqlSession().selectList("selectHeatmeterInputsByOm", of("heatmeterId", heatmeterId, "om", om));
    }

    //todo move to HeatmeterInput/ConsumptionService
    public void calculateConsumptions(Iterable<HeatmeterPayload> payloads, Iterable<HeatmeterInput> inputs) {
        //filter out non existing payloads
        List<HeatmeterPayload> payloadsList = Lists.newArrayList(Iterables.filter(payloads, new Predicate<HeatmeterPayload>() {

            @Override
            public boolean apply(HeatmeterPayload payload) {
                return payload.getId() != null;
            }
        }));

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
