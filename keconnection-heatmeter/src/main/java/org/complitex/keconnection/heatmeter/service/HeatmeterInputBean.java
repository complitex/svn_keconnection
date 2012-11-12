/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.service;

import com.google.common.collect.ImmutableMap;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
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

    public List<HeatmeterInput> getList(long heatmeterId, Date operatingMonth) {
        return sqlSession().selectList("selectHeatmeterInputsByHeatmeterId",
                ImmutableMap.of("heatmeterId", heatmeterId, "operatingMonth", operatingMonth));
    }
}
