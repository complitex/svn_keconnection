package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterBindingStatus;
import org.complitex.keconnection.heatmeter.entity.HeatmeterType;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 05.09.12 17:14
 */
@XmlMapper
@Stateless
public class HeatmeterBean extends AbstractBean {

    public static final String PAYLOAD_BEGIN_DATE_FILTER_PARAM = "payloadBeginDate";
    public static final String CONSUMPTION_READOUT_DATE = "consumptionReadoutDate";
    @EJB
    private HeatmeterPeriodBean heatmeterPeriodBean;
    @EJB
    private HeatmeterConnectionBean heatmeterConnectionBean;
    @EJB
    private HeatmeterPayloadBean heatmeterPayloadBean;
    @EJB
    private HeatmeterInputBean heatmeterInputBean;
    @EJB
    private HeatmeterConsumptionBean heatmeterConsumptionBean;
    @EJB(name = IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME)
    private IKeConnectionOrganizationStrategy organizationStrategy;

    @Transactional
    public void save(Heatmeter heatmeter) {
        saveHeatmeterInfo(heatmeter);

//        save(heatmeterPeriodBean, heatmeter.getId(), heatmeter.getPeriods());
//        save(heatmeterConnectionBean, heatmeter.getId(), heatmeter.getConnections());
//        save(heatmeterPayloadBean, heatmeter.getId(), heatmeter.getPayloads());
//        save(heatmeterConsumptionBean, heatmeter.getId(), heatmeter.getConsumptions());
    }

    @Transactional
    public void saveHeatmeterInfo(Heatmeter heatmeter) {
        if (heatmeter.getId() == null) {
            sqlSession().insert("insertHeatmeter", heatmeter);
        } else {
            sqlSession().update("updateHeatmeter", heatmeter);
        }
    }

//    private <T extends IHeatmeterEntity> void save(IHeatmeterEntityBean<T> bean, Long heatmeterId, List<T> list){
//        if (heatmeterId != null) {
//            List<T> db = bean.getList(heatmeterId);
//
//            for (T object : IdListUtil.getDiff(db, list)) {
//                bean.delete(object.getId());
//            }
//        }
//
//        for (T object : list) {
//            object.setHeatmeterId(heatmeterId);
//
//            bean.save(object);
//        }
//    }
    public Heatmeter getHeatmeter(Long id) {
        return sqlSession().selectOne("selectHeatmeter", id);
    }

    public Heatmeter getHeatmeterForBinding(long id) {
        return sqlSession().selectOne("selectHeatmeterForBinding", id);
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
        return sqlSession().selectOne("isExistHeatmeter", of("ls", ls, "buildingCodeId", buildingCodeId,
                "organizationId", organizationId));
    }

    public Heatmeter getHeatmeterByLs(Integer ls, Long organizationId) {
        return sqlSession().selectOne("selectHeatmeterByLs", of("ls", ls, "organizationId", organizationId));
    }

    public void updateHeatmeterType(final Long id, final HeatmeterType type) {
        sqlSession().update("updateHeatmeterType", of("id", id, "type", type));
    }

    public boolean isOnlyHeatmeterForBuildingCode(long heatmeterId, long buildingCodeId) {
        int result = sqlSession().selectOne("isOnlyHeatmeterForBuildingCode",
                of("buildingCodeId", buildingCodeId, "heatmeterId", heatmeterId));
        return result == 0;
    }
}
