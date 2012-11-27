package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPayload;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;

import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.PAYLOAD;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.09.12 18:36
 */
@XmlMapper
@Stateless
public class HeatmeterPayloadBean extends HeatmeterPeriodBean {
    @Override
    public HeatmeterPeriodType getType() {
        return PAYLOAD;
    }

    @Transactional
    public void save(HeatmeterPayload payload) {
        boolean isNew = payload.getId() == null;

        super.save(payload);

        if (isNew) {
            sqlSession().insert("insertHeatmeterPayload", payload);
        } else {
            sqlSession().update("updateHeatmeterPayload", payload);
        }
    }

    public boolean isExist(Long heatmeterId) {
        return sqlSession().selectOne("isExistHeatmeterPayload", heatmeterId);
    }

    public void deleteByTablegramId(Long tablegramId) {
        sqlSession().delete("deletePayloadByTablegramId", tablegramId);
    }

    public List<HeatmeterPayload> getHeatmeterPayloads(Long heatmeterId, Date om) {
        return sqlSession().selectList("selectHeatmeterPayloadsByOm", of("heatmeterId", heatmeterId, "om", om));
    }
}
