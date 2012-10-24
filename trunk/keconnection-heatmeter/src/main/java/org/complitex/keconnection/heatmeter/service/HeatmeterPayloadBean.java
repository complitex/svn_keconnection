package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractListBean;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPayload;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.09.12 18:36
 */
@XmlMapper
@Stateless
public class HeatmeterPayloadBean extends AbstractListBean<HeatmeterPayload> {
    public HeatmeterPayload getHeatmeterPayload(Long id){
        return sqlSession().selectOne("selectHeatmeterPayload", id);
    }

    public List<HeatmeterPayload> getHeatmeterPayloads(FilterWrapper<HeatmeterPayload> filterWrapper){
        return sqlSession().selectList("selectHeatmeterPayloads", filterWrapper);
    }

    public int getPayloadsCount(FilterWrapper<HeatmeterPayload> filterWrapper){
        return sqlSession().selectOne("selectHeatmeterPayloadsCount", filterWrapper);
    }

    public void save(HeatmeterPayload heatmeterPayload){
        if (heatmeterPayload.getId() == null){
            sqlSession().insert("insertHeatmeterPayload", heatmeterPayload);
        }else {
            sqlSession().update("updateHeatmeterPayload", heatmeterPayload);
        }
    }

    public boolean isExist(Long heatmeterId){
        return sqlSession().selectOne("isExistHeatmeterPayload", heatmeterId);
    }

    public void deleteByTablegramId(Long tablegramId){
        sqlSession().delete("deletePayloadByTablegramId", tablegramId);
    }

    @Override
    public List<HeatmeterPayload> getList(FilterWrapper<HeatmeterPayload> filterWrapper) {
        return getHeatmeterPayloads(filterWrapper);
    }

    @Override
    public Integer getCount(FilterWrapper<HeatmeterPayload> filterWrapper) {
        return getPayloadsCount(filterWrapper);
    }
}
