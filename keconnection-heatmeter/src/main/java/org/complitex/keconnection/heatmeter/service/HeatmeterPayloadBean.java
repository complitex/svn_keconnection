package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.Transactional;
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
    public HeatmeterPayload get(Long id){
        return sqlSession().selectOne("selectHeatmeterPayload", id);
    }

    @Transactional
    public void save(HeatmeterPayload heatmeterPayload){
        if (heatmeterPayload.getId() == null){
            sqlSession().insert("insertHeatmeterPayload", heatmeterPayload);
        }else {
            sqlSession().update("updateHeatmeterPayload", heatmeterPayload);
        }
    }

    public void delete(Long id) {
        sqlSession().delete("deleteHeatmeterPayload", id);
    }

    public boolean isExist(Long heatmeterId){
        return sqlSession().selectOne("isExistHeatmeterPayload", heatmeterId);
    }

    public void deleteByTablegramId(Long tablegramId){
        sqlSession().delete("deletePayloadByTablegramId", tablegramId);
    }

    public List<HeatmeterPayload> getList(FilterWrapper<HeatmeterPayload> filterWrapper) {
        return sqlSession().selectList("selectHeatmeterPayloads", filterWrapper);
    }

    public Integer getCount(FilterWrapper<HeatmeterPayload> filterWrapper) {
        return sqlSession().selectOne("selectHeatmeterPayloadsCount", filterWrapper);
    }

    public List<HeatmeterPayload> getList(Long heatmeterId) {
        return sqlSession().selectList("selectHeatmeterPayloadsByHeatmeterId", heatmeterId);
    }
}
