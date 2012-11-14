/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.HeatmeterInput;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;

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

    public List<HeatmeterInput> getList(long heatmeterId, Date om) {
        List<HeatmeterInput> inputs = sqlSession().selectList("selectHeatmeterInputsByOm",
                of("heatmeterId", heatmeterId, "om", om));

        //todo fix check empty collection in UI
        for (HeatmeterInput input : inputs) {
            input.addNewConsumptionIfNecessary();
        }

        return inputs;
    }
}
