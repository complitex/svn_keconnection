package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.OperatingMonth;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.10.12 18:22
 */
@XmlMapper
@Stateless
public class OperatingMonthBean extends AbstractBean{
    public void save(OperatingMonth operatingMonth){
        if (operatingMonth.getId() == null){
            sqlSession().insert("insertOperatingMonth", operatingMonth);
        }else {
            sqlSession().update("updateOperatingMonth", operatingMonth);
        }
    }

    public OperatingMonth getOperatingMonth(Long id){
        return sqlSession().selectOne("selectOperatingMonth", id);
    }

    public List<OperatingMonth> getOperatingMonths(FilterWrapper<OperatingMonth> filterWrapper){
        return sqlSession().selectList("selectOperatingMonths", filterWrapper);
    }

    public Integer getOperatingMonthsCount(FilterWrapper<OperatingMonth> filterWrapper){
        return sqlSession().selectOne("selectOperatingMonthsCount", filterWrapper);
    }
}
