/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.service;

import org.apache.ibatis.session.SqlSession;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.keconnection.heatmeter.service.ExternalHeatmeterService.ExternalHeatmetersAndStatus;
import org.complitex.keconnection.heatmeter.service.exception.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public class HeatmeterBindServiceTest {

    private final Logger log = LoggerFactory.getLogger(HeatmeterBindServiceTest.class);
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
            LoggerFactory.getLogger(HeatmeterBindServiceTest.class).error(message);
        }
    }

    private static ExternalHeatmeterService newExternalHeatmeterService() {
        return new TestExternalHeatmeterService();
    }

    private static ExternalHeatmetersAndStatus fetchExternalHeatmeters(long heatmeterId, Integer ls,
            String organizationCode, int buildingCode, Date date) throws DBException {
        return newExternalHeatmeterService().fetchExternalHeatmeters(heatmeterId, ls, organizationCode, buildingCode, date);
    }

    public static void main(String[] args) throws DBException {
        ExternalHeatmetersAndStatus info = fetchExternalHeatmeters(1, 1, "1", 1, DateUtil.getCurrentDate());
        LoggerFactory.getLogger(HeatmeterBindServiceTest.class).info(info.toString());
    }
}
