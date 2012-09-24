package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.Payload;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.09.12 18:36
 */
@XmlMapper
@Stateless
public class PayloadBean extends AbstractBean {
    public List<Payload> getPayloads(FilterWrapper<Payload> filterWrapper){
        return sqlSession().selectList("selectPayloads", filterWrapper);
    }

    public int getPayloadsCount(FilterWrapper<Payload> filterWrapper){
        return sqlSession().selectOne("selectPayloadsCount", filterWrapper);
    }
}
