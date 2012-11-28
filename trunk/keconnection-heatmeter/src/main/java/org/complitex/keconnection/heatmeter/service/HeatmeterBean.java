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
import java.util.Date;
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
    public static final String PAYLOAD1_FILTER_PARAM = "payload1";
    public static final String PAYLOAD2_FILTER_PARAM = "payload2";
    public static final String PAYLOAD3_FILTER_PARAM = "payload3";
    public static final String INPUT_READOUT_DATE_FILTER_PARAM = "inputReadoutDate";
    public static final String INPUT_FILTER_PARAM = "input";
    public static final String CONSUMPTION1_FILTER_PARAM = "consumption1";
    
    @EJB
    private HeatmeterOperationBean operationBean;

    @EJB
    private HeatmeterConnectionBean connectionBean;

    @EJB
    private HeatmeterPayloadBean payloadBean;

    @EJB
    private HeatmeterInputBean inputBean;

    @EJB
    private HeatmeterConsumptionBean consumptionBean;

    @EJB(name = IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME)
    private IKeConnectionOrganizationStrategy organizationStrategy;

    @Transactional
    public void save(Heatmeter heatmeter) {
        if (heatmeter.getId() == null) {
            sqlSession().insert("insertHeatmeter", heatmeter);
        } else {
            sqlSession().update("updateHeatmeter", heatmeter);
        }
    }

    @Transactional
    public void save(Heatmeter heatmeter, Date om) {
        save(heatmeter);

        Long heatmeterId = heatmeter.getId();

        operationBean.save(heatmeterId, om, heatmeter.getOperations());
        connectionBean.save(heatmeterId, om, heatmeter.getConnections());
        payloadBean.save(heatmeterId, om, heatmeter.getPayloads());
        inputBean.save(heatmeterId, om, heatmeter.getInputs());
    }

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

    public boolean isExist(Integer ls, Long buildingCodeId) {
        return sqlSession().selectOne("isExistHeatmeter", of("ls", ls, "buildingCodeId", buildingCodeId));
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

    public Date getMinOm(Long heatmeterId){
        return sqlSession().selectOne("selectHeatmeterMinOm", heatmeterId);
    }
}
