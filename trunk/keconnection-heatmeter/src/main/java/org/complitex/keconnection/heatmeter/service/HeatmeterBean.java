package org.complitex.keconnection.heatmeter.service;

import com.google.common.collect.ImmutableMap;
import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.util.IdListUtil;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterCode;
import org.complitex.keconnection.heatmeter.entity.HeatmeterType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 05.09.12 17:14
 */
@XmlMapper
@Stateless
public class HeatmeterBean extends AbstractBean{
    @EJB
    private HeatmeterCodeBean heatmeterCodeBean;

    public void save(Heatmeter heatmeter){
        if (heatmeter.getId() == null){
            sqlSession().insert("insertHeatmeter", heatmeter);

            //heatmeter codes
            for (HeatmeterCode heatmeterCode : heatmeter.getHeatmeterCodes()){
                heatmeterCodeBean.save(heatmeterCode);
            }
        }else {
            sqlSession().update("updateHeatmeter", heatmeter);

            //heatmeter codes in db
            List<HeatmeterCode> db = heatmeterCodeBean.getHeatmeterCodes(heatmeter.getId());

            //delete heatmeter codes
            for(HeatmeterCode heatmeterCode : IdListUtil.getDiff(db, heatmeter.getHeatmeterCodes())){
                heatmeterCodeBean.delete(heatmeterCode.getId());
            }

            //save heatmeter codes
            for (HeatmeterCode heatmeterCode : heatmeter.getHeatmeterCodes()){
                heatmeterCodeBean.save(heatmeterCode);
            }
        }
    }

    public Heatmeter getHeatmeter(Long id){
        return sqlSession().selectOne("selectHeatmeter", id);
    }

    public List<Heatmeter> getHeatmeters(FilterWrapper<Heatmeter> filterWrapper){
        return sqlSession().selectList("selectHeatmeters", filterWrapper);
    }

    public int getHeatmeterCount(FilterWrapper<Heatmeter> filterWrapper){
        return sqlSession().selectOne("selectHeatmetersCount", filterWrapper);
    }

    public void delete(Long id){
        sqlSession().delete("deleteHeatmeter", id);
    }

    public boolean isExist(Integer ls, Long buildingCodeId, Long organizationId){
        return sqlSession().selectOne("isExistHeatmeter", ImmutableMap.of("ls", ls, "buildingCodeId", buildingCodeId,
                "organizationId", organizationId));
    }

    public Heatmeter getHeatmeterByLs(Integer ls, Long organizationId){
        return sqlSession().selectOne("selectHeatmeterByLs", ImmutableMap.of("ls", ls, "organizationId", organizationId));
    }

    public void updateHeatmeterType(final Long id, final HeatmeterType type){
        sqlSession().update("updateHeatmeterType", new HashMap<String, Object>(){{
            put("id", id);
            put("type", type);
        }});
    }
}
