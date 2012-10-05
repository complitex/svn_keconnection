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

    private static final Logger log = LoggerFactory.getLogger(ExternalHeatmeterService.class);
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

    public static class ExternalHeatmeterAndStatus implements Serializable {

        final ExternalHeatmeter heatmeter;
        final HeatmeterBindingStatus status;

        ExternalHeatmeterAndStatus(ExternalHeatmeter heatmeter, HeatmeterBindingStatus status) {
            this.heatmeter = heatmeter;
            this.status = status;
        }

        @Override
        public String toString() {
            String s = "External heatmeter info: {heatmeter: ";
            s += heatmeter == null ? "null"
                    : "{id: " + heatmeter.getId() + ", number: " + heatmeter.getNumber() + "}";
            s += ", status: " + status + "}";
            return s;
        }
    }

    public ExternalHeatmeterAndStatus fetchExternalHeatmeter(long heatmeterId, Integer ls,
            String organizationCode, int buildingCode, Date date) throws DBException {
        ExternalHeatmeter externalHeatmeter = null;
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
                    List<ExternalHeatmeter> externalHeatmeters = (List<ExternalHeatmeter>) params.get("details");
                    if (externalHeatmeters == null || externalHeatmeters.isEmpty()) {
                        log.error("{}. Result code is 1 but external heatmeter data is null or empty."
                                + "Heatmeter id: {}, ls: {}",
                                new Object[]{FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE, heatmeterId, ls});
                        logError(heatmeterId, "result_code_inconsistent", FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE);
                        status = HeatmeterBindingStatus.BINDING_ERROR;
                    } else if (externalHeatmeters.size() > 1) {
                        log.warn("{}. Size of list of external heatmeters is more than 1. Only first entry will be used."
                                + "Heatmeter id: {}, ls: {}",
                                new Object[]{FETCH_EXTERNAL_HEATMETER_STORED_PROCEDURE, heatmeterId, ls});
                    } else {
                        externalHeatmeter = externalHeatmeters.get(0);
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
        return new ExternalHeatmeterAndStatus(externalHeatmeter, status);
    }
}
