package org.complitex.keconnection.heatmeter.service;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConsumption;
import org.complitex.keconnection.heatmeter.entity.HeatmeterInput;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPayload;

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

/**
 *
 * @author Artem
 */
@XmlMapper
@Stateless
public class HeatmeterInputBean extends HeatmeterAttributeBean<HeatmeterInput> {

    @EJB
    private HeatmeterPeriodBean heatmeterPeriodBean;
    @EJB
    private HeatmeterConsumptionBean heatmeterConsumptionBean;

    @Transactional
    public void save(HeatmeterInput heatmeterInput) {
        if (heatmeterInput.getId() == null) {
            sqlSession().insert("insertHeatmeterInput", heatmeterInput);
        } else {
            sqlSession().update("updateHeatmeterInput", heatmeterInput);
        }

        //update period
        heatmeterInput.getPeriod().setAttributeId(heatmeterInput.getId());
        heatmeterPeriodBean.save(heatmeterInput.getPeriod());

        //update consumptions
        for (HeatmeterConsumption consumption : heatmeterInput.getConsumptions()) {
            consumption.setHeatmeterInputId(heatmeterInput.getId());
            heatmeterConsumptionBean.save(consumption);
        }
    }

    public List<HeatmeterInput> getList(Long heatmeterId, Date om) {
        List<HeatmeterInput> inputs = sqlSession().selectList("selectHeatmeterInputsByOm",
                of("heatmeterId", heatmeterId, "om", om));

        //todo fix check empty collection in UI
        for (HeatmeterInput input : inputs) {
            input.addNewConsumptionIfNecessary();
        }

        return inputs;
    }

    @Override
    public void delete(Long id) {
        sqlSession().delete("deleteHeatmeterInput", id);
    }

    public void calculateConsumptionForNewInput(List<HeatmeterPayload> payloads, HeatmeterInput newInput) {
        payloads = Lists.newArrayList(Iterables.filter(payloads, new Predicate<HeatmeterPayload>() {

            @Override
            public boolean apply(HeatmeterPayload payload) {
                return payload.getId() != null;
            }
        }));

        Collections.sort(payloads, new Comparator<HeatmeterPayload>() {

            @Override
            public int compare(HeatmeterPayload p1, HeatmeterPayload p2) {
                return p1.getPeriod().getBeginDate().compareTo(p2.getPeriod().getBeginDate());
            }
        });

        HeatmeterConsumption c = newInput.getFirstConsumption();
        c.setBeginDate(newInput.getPeriod().getBeginDate());
        c.setEndDate(newInput.getPeriod().getEndDate());
        c.setOm(newInput.getPeriod().getEndOm());
        c.setConsumption1(BigDecimal.ZERO);
        c.setConsumption2(BigDecimal.ZERO);
        c.setConsumption3(BigDecimal.ZERO);

        int inutPeriodLength = getDaysDiff(newInput.getPeriod().getBeginDate(), newInput.getPeriod().getEndDate());

        for (HeatmeterPayload payload : payloads) {
            if (payload.getPeriod().getBeginDate().before(newInput.getPeriod().getBeginDate())
                    && payload.getPeriod().getEndDate().after(newInput.getPeriod().getBeginDate())) {
                Date subPeriodEndDate = getMin(payload.getPeriod().getEndDate(), newInput.getPeriod().getEndDate());
                int subPeriodLength = getDaysDiff(newInput.getPeriod().getBeginDate(), subPeriodEndDate);
                addSubPeriodCalculations(newInput.getValue(), inutPeriodLength, subPeriodLength, c,
                        payload.getPayload2(), payload.getPayload3());
            } else if (payload.getPeriod().getBeginDate().after(newInput.getPeriod().getBeginDate())
                    && payload.getPeriod().getBeginDate().before(newInput.getPeriod().getEndDate())) {
                Date subPeriodEndDate = getMin(payload.getPeriod().getEndDate(), newInput.getPeriod().getEndDate());
                int subPeriodLength = getDaysDiff(payload.getPeriod().getBeginDate(), subPeriodEndDate);
                addSubPeriodCalculations(newInput.getValue(), inutPeriodLength, subPeriodLength, c,
                        payload.getPayload2(), payload.getPayload3());
            }
        }
        c.setConsumption1(newInput.getValue().subtract(c.getConsumption2()).subtract(c.getConsumption3()));
    }

    private void addSubPeriodCalculations(BigDecimal value, int periodLength, int subPeriodLength,
            HeatmeterConsumption consumption, BigDecimal payload2, BigDecimal payload3) {
        consumption.setConsumption2(consumption.getConsumption2().add(BigDecimal.valueOf(
                (value.doubleValue() * subPeriodLength * payload2.doubleValue()) / (periodLength * 100))));

        consumption.setConsumption3(consumption.getConsumption3().add(BigDecimal.valueOf(
                (value.doubleValue() * subPeriodLength * payload3.doubleValue()) / (periodLength * 100))));
    }
}
