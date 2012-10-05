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

/**
 *
 * @author Artem
 */
@XmlMapper
@Stateless
public class HeatmeterBindBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = HeatmeterBindBean.class.getName();

    @Transactional
    public void fillHeatmeterBind() {
        delete();
        sqlSession().insert(MAPPING_NAMESPACE + ".fill");
    }

    @Transactional
    public void delete() {
        sqlSession().delete(MAPPING_NAMESPACE + ".delete");
    }

    public List<Long> getBatch(int count) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".getBatch", count);
    }

    @Transactional
    public void markProcessed(List<Long> batch) {
        if (batch != null && !batch.isEmpty()) {
            sqlSession().update(MAPPING_NAMESPACE + ".markProcessed", batch);
        }
    }
}
