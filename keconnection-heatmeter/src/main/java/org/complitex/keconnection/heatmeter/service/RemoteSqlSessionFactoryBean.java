/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.service;

import java.io.IOException;
import java.io.Reader;
import javax.ejb.Singleton;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@Singleton
public class RemoteSqlSessionFactoryBean {

    private final Logger log = LoggerFactory.getLogger(RemoteSqlSessionFactoryBean.class);
    public static final String CONFIGURATION_FILE_NAME = "mybatis-remote-config.xml";
    private SqlSessionManager sqlSessionManager;

    public SqlSessionManager getSqlSessionManager() {
        if (sqlSessionManager == null) {
            sqlSessionManager = createSqlSessionManager();
        }
        return sqlSessionManager;
    }

    private SqlSessionManager createSqlSessionManager() {
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader(getConfigurationFileName());
            return SqlSessionManager.newInstance(reader);
        } catch (Exception e) {
            log.error("Remote configuration couldn't be created.");
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("Could not close reader.", e);
                }
            }
        }
    }

    protected String getConfigurationFileName() {
        return CONFIGURATION_FILE_NAME;
    }
}
