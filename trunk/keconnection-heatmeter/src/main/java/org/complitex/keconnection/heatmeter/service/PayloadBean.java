package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractListBean;
import org.complitex.keconnection.heatmeter.entity.Payload;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.09.12 18:36
 */
@XmlMapper
@Stateless
public class PayloadBean extends AbstractListBean<Payload> {
    public Payload getPayload(Long id){
        return sqlSession().selectOne("selectPayload", id);
    }

    public List<Payload> getPayloads(FilterWrapper<Payload> filterWrapper){
        return sqlSession().selectList("selectPayloads", filterWrapper);
    }

    public int getPayloadsCount(FilterWrapper<Payload> filterWrapper){
        return sqlSession().selectOne("selectPayloadsCount", filterWrapper);
    }

    public void save(Payload payload){
        if (payload.getId() == null){
            sqlSession().insert("insertPayload", payload);
        }else {
            sqlSession().update("updatePayload", payload);
        }
    }

    public boolean isExist(Long heatmeterId){
        return sqlSession().selectOne("isExistPayload", heatmeterId);
    }

    public void deleteByTablegramId(Long tablegramId){
        sqlSession().delete("deletePayloadByTablegramId", tablegramId);
    }

    @Override
    public List<Payload> getList(FilterWrapper<Payload> filterWrapper) {
        return getPayloads(filterWrapper);
    }

    @Override
    public Integer getCount(FilterWrapper<Payload> filterWrapper) {
        return getPayloadsCount(filterWrapper);
    }
}