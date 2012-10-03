/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.service;

import java.util.List;
import javax.ejb.Stateless;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.HeatmeterCorrection;

/**
 *
 * @author Artem
 */
@XmlMapper
@Stateless
public class HeatmeterCorrectionBean extends AbstractBean {

    @Transactional
    public void insert(HeatmeterCorrection correction) {
        sqlSession().insert("insert", correction);
    }

    @Transactional
    public void updateBindingStatus(HeatmeterCorrection correction) {
        sqlSession().update("updateBindingStatus", correction);
    }

    @Transactional
    public void makeHistory(long heatmeterId) {
        sqlSession().update("makeHistory", heatmeterId);
    }

    public HeatmeterCorrection findById(long heatmeterId) {
        List<HeatmeterCorrection> corrections = sqlSession().selectList("findById", heatmeterId);
        if (corrections.isEmpty()) {
            return null;
        } else if (corrections.size() > 1) {
            throw new IllegalStateException("More one non-history heatmeter correction found for heatmeter id: " + heatmeterId);
        }
        return corrections.get(0);
    }
}
