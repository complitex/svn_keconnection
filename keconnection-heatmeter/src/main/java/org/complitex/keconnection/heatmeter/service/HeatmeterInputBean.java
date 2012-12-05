package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConsumption;
import org.complitex.keconnection.heatmeter.entity.HeatmeterInput;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
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
}
