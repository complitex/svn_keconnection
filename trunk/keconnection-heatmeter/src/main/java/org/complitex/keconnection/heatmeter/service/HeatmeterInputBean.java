/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.service;

import com.google.common.collect.ImmutableMap;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.HeatmeterInput;

/**
 *
 * @author Artem
 */
@XmlMapper
@Stateless
public class HeatmeterInputBean extends AbstractBean {

    @EJB
    private HeatmeterPeriodBean heatmeterPeriodBean;

    @Transactional
    public void save(HeatmeterInput heatmeterInput) {
        if (heatmeterInput.getId() == null) {
            sqlSession().insert("insertHeatmeterInput", heatmeterInput);
        } else {
            sqlSession().update("updateHeatmeterInput", heatmeterInput);
        }
        heatmeterInput.getPeriod().setAttributeId(heatmeterInput.getId());
        heatmeterPeriodBean.save(heatmeterInput.getPeriod());
    }

    public List<HeatmeterInput> getList(long heatmeterId, Date operatingMonth) {
        List<HeatmeterInput> inputs = sqlSession().selectList("selectHeatmeterInputsByHeatmeterId",
                ImmutableMap.of("heatmeterId", heatmeterId, "operatingMonth", operatingMonth));
        for (HeatmeterInput input : inputs) {
            input.addNewConsumptionIfNecessary();
        }
        return inputs;
    }
}
