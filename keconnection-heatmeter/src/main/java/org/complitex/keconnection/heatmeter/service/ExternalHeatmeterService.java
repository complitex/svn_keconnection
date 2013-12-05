/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import org.apache.ibatis.session.SqlSession;
import org.complitex.dictionary.entity.Log.EVENT;
import org.complitex.dictionary.oracle.OracleErrors;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.LogBean;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.keconnection.heatmeter.Module;
import org.complitex.keconnection.heatmeter.entity.ExternalHeatmeter;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterBindingStatus;
import org.complitex.keconnection.heatmeter.service.exception.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ExternalHeatmeterService {

    private final Logger log = LoggerFactory.getLogger(ExternalHeatmeterService.class);
    private static final String MAPPING_NAMESPACE = ExternalHeatmeterService.class.getName();
    private static final String RESOURCE_BUNDLE = ExternalHeatmeterService.class.getName();
    private static final String FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE = "Z$RUNTIME_PROV_UTL.GETHEATMETERS";
    @EJB
    private RemoteSqlSessionFactoryBean sqlSessionFactoryBean;
    @EJB
    private LogBean logBean;
    @EJB
    private LocaleBean localeBean;

    protected SqlSession sqlSession() {
        return sqlSessionFactoryBean.getSqlSessionManager();
    }

    protected void logError(long heatmeterId, String key, Object... params) {
        logError(heatmeterId, ResourceUtil.getFormatString(RESOURCE_BUNDLE, key, getLocale(), params));
    }

    protected void logError(long heatmeterId, String message) {
        logBean.error(Module.NAME, ExternalHeatmeterService.class, Heatmeter.class, heatmeterId, EVENT.GETTING_DATA,
                message);
    }

    protected Locale getLocale() {
        return localeBean.getSystemLocale();
    }

    public static class ExternalHeatmetersAndStatus implements Serializable {

        public final List<ExternalHeatmeter> heatmeters;
        public final HeatmeterBindingStatus status;

        private ExternalHeatmetersAndStatus(List<ExternalHeatmeter> heatmeters, HeatmeterBindingStatus status) {
            this.heatmeters = heatmeters;
            this.status = status;
        }

        @Override
        public String toString() {
            String s = "External heatmeters: ";
            s += heatmeters != null ? heatmeters.toString() : "[]";
            s += ", status: " + status;
            return s;
        }
    }

    //TODO: remove after testing.
//    public ExternalHeatmetersAndStatus fetchExternalHeatmetersTest(long heatmeterId, Integer ls,
//            String organizationCode, int buildingCode, Date date) throws DBException {
//        return new ExternalHeatmetersAndStatus(ImmutableList.of(new ExternalHeatmeter("1", "#1")),
//                HeatmeterBindingStatus.BOUND);
//    }
    public ExternalHeatmetersAndStatus fetchExternalHeatmeters(long heatmeterId, Integer ls,
            String organizationCode, int buildingCode, Date date) throws DBException {
        List<ExternalHeatmeter> externalHeatmeters = null;
        HeatmeterBindingStatus status = null;

        Map<String, Object> params = new HashMap<>();
        params.put("pDepCode", organizationCode);
        params.put("pHouseCode", buildingCode);
        params.put("pDate", date);

        long startTime = 0;
        if (log.isDebugEnabled()) {
            startTime = System.nanoTime();
        }
        try {
            sqlSession().selectOne(MAPPING_NAMESPACE + ".fetchExternalHeatmeter", params);
        } catch (Exception e) {
            if (!OracleErrors.isCursorClosedError(e)) {
                throw new DBException(e);
            }
        } finally {
            log.info("{}. Heatmeter id: {}, ls: {}, parameters : {}",
                    new Object[]{FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE, heatmeterId, ls, params});
            if (log.isDebugEnabled()) {
                log.debug("{}. Time of operation: {} sec.", new Object[]{FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE,
                            (System.nanoTime() - startTime) / 1000000000F});
            }
        }

        Integer resultCode = (Integer) params.get("resultCode");
        if (resultCode == null) {
            log.error("{}. Result code is null. Heatmeter id: {}, ls: {}",
                    new Object[]{FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE, heatmeterId, ls});
            logError(heatmeterId, "result_code_unexpected", FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE, "null");
            status = HeatmeterBindingStatus.BINDING_ERROR;
        } else {
            switch (resultCode) {
                case 1:
                    externalHeatmeters = (List<ExternalHeatmeter>) params.get("externalInfo");
                    if (externalHeatmeters == null || externalHeatmeters.isEmpty()) {
                        status = HeatmeterBindingStatus.NO_EXTERNAL_HEATMETERS;
                    } else if (externalHeatmeters.size() > 1) {
                        status = HeatmeterBindingStatus.MORE_ONE_EXTERNAL_HEATMETER;
                    } else {
                        status = HeatmeterBindingStatus.BOUND;
                    }
                    break;
                case -1:
                    status = HeatmeterBindingStatus.BUILDING_NOT_FOUND;
                    break;
                case -2:
                    status = HeatmeterBindingStatus.ORGANIZATION_NOT_FOUND;
                    break;
                default:
                    log.error("{}. Unexpected result code: {}. Heatmeter id: {}, ls: {}",
                            new Object[]{FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE, resultCode, heatmeterId, ls});
                    logError(heatmeterId, "result_code_unexpected", FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE, resultCode);
                    status = HeatmeterBindingStatus.BINDING_ERROR;
            }
        }
        return new ExternalHeatmetersAndStatus(externalHeatmeters, status);
    }
}
