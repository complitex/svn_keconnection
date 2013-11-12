package org.complitex.keconnection.heatmeter.service;

import com.google.common.collect.ImmutableSet;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.service.exception.ConcurrentModificationException;
import org.complitex.dictionary.web.DictionaryFwSession;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterBindingStatus;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriod;
import org.complitex.keconnection.heatmeter.entity.HeatmeterType;
import org.complitex.keconnection.organization.strategy.KeConnectionOrganizationStrategy;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private KeConnectionOrganizationStrategy organizationStrategy;

    @Transactional
    public void save(Heatmeter heatmeter) throws ConcurrentModificationException {
        Long heatmeterId = heatmeter.getId();
        Date om = heatmeter.getOm();

        //save heatmeter
        if (heatmeterId == null) {
            sqlSession().insert("insertHeatmeter", heatmeter);

            heatmeterId = heatmeter.getId();
        } else {
            if (isModified(heatmeter)){
                throw new ConcurrentModificationException();
            }

            sqlSession().update("updateHeatmeter", heatmeter);
        }

        //save periods
        operationBean.save(heatmeterId, om, heatmeter.getOperations());
        connectionBean.save(heatmeterId, om, heatmeter.getConnections());
        payloadBean.save(heatmeterId, om, heatmeter.getPayloads());
        inputBean.save(heatmeterId, om, heatmeter.getInputs());
    }

    public boolean isModified(Heatmeter heatmeter){
        if (heatmeter.getId() == null){
            return false;
        }

        Heatmeter dbHeatmeter = getHeatmeter(heatmeter.getId());

        if (!heatmeter.getUpdated().equals(dbHeatmeter.getUpdated())){
            return true;
        }

        List<HeatmeterPeriod> dbPeriods = dbHeatmeter.getPeriods();
        List<HeatmeterPeriod> periods = heatmeter.getPeriods();

        for (HeatmeterPeriod dbPeriod : dbPeriods){
            for (HeatmeterPeriod period : periods){
                if (period.getId() != null && period.getId().equals(dbPeriod.getId())
                        && period.getType().equals(dbPeriod.getType())
                        && !period.getUpdated().equals(dbPeriod.getUpdated())){

                    return true;
                }
            }
        }

        return false;
    }

    public Heatmeter getHeatmeter(Long id) {
        return sqlSession().selectOne("selectHeatmeter", id);
    }

    public Heatmeter getHeatmeterForBinding(long id) {
        return sqlSession().selectOne("selectHeatmeterForBinding", id);
    }

    private void addUnboundStatusParameter(FilterWrapper<Heatmeter> filter) {
        filter.add("unboundBindingStatus", HeatmeterBindingStatus.UNBOUND);
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

    private static final Set<String> SEARCH_STATE_ENTITIES = ImmutableSet.of("country", "region", "city", "street", "building");

    public SearchComponentState restoreSearchState(DictionaryFwSession session) {
        SearchComponentState searchComponentState = new SearchComponentState();

        for (Map.Entry<String, DomainObject> searchFilterEntry : session.getGlobalSearchComponentState().entrySet()) {
            final String searchFilter = searchFilterEntry.getKey();
            final DomainObject filterObject = searchFilterEntry.getValue();
            if (SEARCH_STATE_ENTITIES.contains(searchFilter)) {
                if (filterObject != null && filterObject.getId() != null && filterObject.getId() > 0) {
                    searchComponentState.put(searchFilter, filterObject);
                }
            }
        }

        return searchComponentState;
    }

    public void storeSearchState(DictionaryFwSession session, SearchComponentState searchComponentState) {
        SearchComponentState globalSearchComponentState = session.getGlobalSearchComponentState();
        globalSearchComponentState.updateState(searchComponentState);
        session.storeGlobalSearchComponentState();
    }
}
