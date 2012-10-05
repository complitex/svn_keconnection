/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.service;

import java.util.Date;
import java.util.Locale;
import org.apache.ibatis.session.SqlSession;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.keconnection.heatmeter.service.ExternalHeatmeterService.ExternalHeatmeterAndStatus;
import org.complitex.keconnection.heatmeter.service.exception.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public class HeatmeterBindServiceTest {

    private static final Logger log = LoggerFactory.getLogger(HeatmeterBindServiceTest.class);
    private static RemoteSqlSessionFactoryBean sqlSessionFactoryBean = new RemoteSqlSessionFactoryBean() {

        @Override
        protected String getConfigurationFileName() {
            return "mybatis-remote-config-test.xml";
        }
    };

    protected static class TestExternalHeatmeterService extends ExternalHeatmeterService {

        @Override
        protected SqlSession sqlSession() {
            return sqlSessionFactoryBean.getSqlSessionManager().openSession(false);
        }

        @Override
        protected Locale getLocale() {
            return new Locale("ru");
        }

        @Override
        protected void logError(long heatmeterId, String message) {
            log.error(message);
        }
    }

    private static ExternalHeatmeterService newExternalHeatmeterService() {
        return new TestExternalHeatmeterService();
    }

    private static ExternalHeatmeterAndStatus fetchExternalHeatmeter(long heatmeterId, Integer ls,
            String organizationCode, int buildingCode, Date date) throws DBException {
        return newExternalHeatmeterService().fetchExternalHeatmeter(heatmeterId, ls, organizationCode, buildingCode, date);
    }

    public static void main(String[] args) throws DBException {
        ExternalHeatmeterAndStatus info = fetchExternalHeatmeter(1, 1, "1", 1, DateUtil.getCurrentDate());
        log.info(info.toString());
    }
}
