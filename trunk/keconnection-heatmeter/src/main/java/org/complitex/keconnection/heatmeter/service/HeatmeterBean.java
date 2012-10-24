package org.complitex.keconnection.heatmeter.service;

import com.google.common.collect.ImmutableMap;
import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.util.IdListUtil;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConnection;
import org.complitex.keconnection.heatmeter.entity.HeatmeterType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;
import org.complitex.keconnection.heatmeter.entity.HeatmeterBindingStatus;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 05.09.12 17:14
 */
@XmlMapper
@Stateless
public class HeatmeterBean extends AbstractBean {

    @EJB
    private HeatmeterConnectionBean heatmeterConnectionBean;

    public void save(Heatmeter heatmeter) {
        if (heatmeter.getId() == null) {
            sqlSession().insert("insertHeatmeter", heatmeter);

            //heatmeter codes
            for (HeatmeterConnection heatmeterConnection : heatmeter.getConnections()) {
                heatmeterConnection.setHeatmeterId(heatmeter.getId());

                heatmeterConnectionBean.save(heatmeterConnection);
            }
        } else {
            sqlSession().update("updateHeatmeter", heatmeter);

            //heatmeter codes in db
            List<HeatmeterConnection> db = heatmeterConnectionBean.getHeatmeterConnections(heatmeter.getId());

            //delete heatmeter codes
            for (HeatmeterConnection heatmeterConnection : IdListUtil.getDiff(db, heatmeter.getConnections())) {
                heatmeterConnectionBean.delete(heatmeterConnection.getId());
            }

            //save heatmeter codes
            for (HeatmeterConnection heatmeterConnection : heatmeter.getConnections()) {
                heatmeterConnection.setHeatmeterId(heatmeter.getId());

                heatmeterConnectionBean.save(heatmeterConnection);
            }
        }
    }

    public Heatmeter getHeatmeter(Long id) {
        return sqlSession().selectOne("selectHeatmeter", id);
    }

    private void addUnboundStatusParameter(FilterWrapper<Heatmeter> filter) {
        filter.addMapEntry("unboundBindingStatus", HeatmeterBindingStatus.UNBOUND);
    }

    public List<Heatmeter> getHeatmeters(FilterWrapper<Heatmeter> filterWrapper) {
        addUnboundStatusParameter(filterWrapper);
        return sqlSession().selectList("selectHeatmeters", filterWrapper);
    }

    public int getHeatmeterCount(FilterWrapper<Heatmeter> filterWrapper) {
        addUnboundStatusParameter(filterWrapper);
        return sqlSession().selectOne("selectHeatmetersCount", filterWrapper);
    }

    public void delete(Long id) {
        sqlSession().delete("deleteHeatmeter", id);
    }

    public boolean isExist(Integer ls, Long buildingCodeId, Long organizationId) {
        return sqlSession().selectOne("isExistHeatmeter", ImmutableMap.of("ls", ls, "buildingCodeId", buildingCodeId,
                "organizationId", organizationId));
    }

    public Heatmeter getHeatmeterByLs(Integer ls, Long organizationId) {
        return sqlSession().selectOne("selectHeatmeterByLs", ImmutableMap.of("ls", ls, "organizationId", organizationId));
    }

    public void updateHeatmeterType(final Long id, final HeatmeterType type) {
        sqlSession().update("updateHeatmeterType", new HashMap<String, Object>() {

            {
                put("id", id);
                put("type", type);
            }
        });
    }

    public boolean isOnlyHeatmeterForBuildingCode(long heatmeterId, long buildingCodeId) {
        int result = sqlSession().selectOne("isOnlyHeatmeterForBuildingCode",
                ImmutableMap.of("buildingCodeId", buildingCodeId, "heatmeterId", heatmeterId));
        return result == 0;
    }
}
