/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.service;

import java.util.List;
import javax.ejb.Stateless;
import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.HeatmeterCorrection;
import org.complitex.keconnection.heatmeter.entity.HeatmeterCorrectionView;

/**
 *
 * @author Artem
 */
@XmlMapper
@Stateless
public class HeatmeterCorrectionBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = HeatmeterCorrectionBean.class.getName();

    @Transactional
    public void insert(HeatmeterCorrection correction) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insert", correction);
    }

    @Transactional
    public void updateBindingDate(HeatmeterCorrection correction) {
        sqlSession().update(MAPPING_NAMESPACE + ".updateBindingDate", correction);
    }

    @Transactional
    public void markHistory(long heatmeterId) {
        sqlSession().update(MAPPING_NAMESPACE + ".markHistory", heatmeterId);
    }

    public HeatmeterCorrection findById(long heatmeterId) {
        List<HeatmeterCorrection> corrections = sqlSession().selectList(MAPPING_NAMESPACE + ".findById", heatmeterId);
        if (corrections == null || corrections.isEmpty()) {
            return null;
        } else if (corrections.size() > 1) {
            throw new IllegalStateException("More one non-history heatmeter correction found for heatmeter id: " + heatmeterId);
        }
        return corrections.get(0);
    }

    public List<HeatmeterCorrectionView> find(FilterWrapper<HeatmeterCorrectionView> filter) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".find", filter);
    }

    public int count(FilterWrapper<HeatmeterCorrectionView> filter) {
        return sqlSession().selectOne(MAPPING_NAMESPACE + ".count", filter);
    }
}
