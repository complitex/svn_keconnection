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

    //TODO: remove after testing:
//    @Transactional
//    public void save(HeatmeterInput input) {
//        boolean isNew = input.getId() == null;
//
//        super.save(input);
//        
//        if (isNew) {
//            sqlSession().insert("insertHeatmeterInput", input);
//        } else {
//            sqlSession().update("updateHeatmeterInput", input);
//        }
//
//        //update consumptions
//        for (HeatmeterConsumption consumption : input.getConsumptions()) {
//            consumption.setHeatmeterInputId(input.getId());
//            heatmeterConsumptionBean.save(consumption);
//        }
//    }
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
                return p1.getBeginDate().compareTo(p2.getBeginDate());
            }
        });

        HeatmeterConsumption c = newInput.getFirstConsumption();
        c.setBeginDate(newInput.getBeginDate());
        c.setEndDate(newInput.getEndDate());
        c.setOm(newInput.getEndOm());
        c.setConsumption1(BigDecimal.ZERO);
        c.setConsumption2(BigDecimal.ZERO);
        c.setConsumption3(BigDecimal.ZERO);

        int initPeriodLength = getDaysDiff(newInput.getBeginDate(), newInput.getEndDate());

        for (HeatmeterPayload p : payloads) {
            if (p.getBeginDate().before(newInput.getBeginDate())
                    && p.getEndDate().after(newInput.getBeginDate())) {
                Date subPeriodEndDate = getMin(p.getEndDate(), newInput.getEndDate());
                int subPeriodLength = getDaysDiff(newInput.getBeginDate(), subPeriodEndDate);
                addSubPeriodCalculations(newInput.getValue(), initPeriodLength, subPeriodLength, c,
                        p.getPayload2(), p.getPayload3());
            } else if (p.getBeginDate().after(newInput.getBeginDate())
                    && p.getBeginDate().before(newInput.getEndDate())) {
                Date subPeriodEndDate = getMin(p.getEndDate(), newInput.getEndDate());
                int subPeriodLength = getDaysDiff(p.getBeginDate(), subPeriodEndDate);
                addSubPeriodCalculations(newInput.getValue(), initPeriodLength, subPeriodLength, c,
                        p.getPayload2(), p.getPayload3());
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
