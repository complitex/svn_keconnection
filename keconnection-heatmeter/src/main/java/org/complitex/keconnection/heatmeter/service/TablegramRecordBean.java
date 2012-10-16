package org.complitex.keconnection.heatmeter.service;

import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.mybatis.XmlMapper;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.TablegramRecord;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.10.12 16:56
 */
@XmlMapper
@Stateless
public class TablegramRecordBean extends AbstractBean{
    public void save(TablegramRecord tablegramRecord){
        if (tablegramRecord.getId() == null){
            sqlSession().insert("insertTablegramRecord", tablegramRecord);
        }else {
            sqlSession().update("updateTablegramRecord", tablegramRecord);
        }
    }

    public List<TablegramRecord> getTablegramRecords(FilterWrapper<TablegramRecord> filterWrapper){
        return sqlSession().selectList("selectTablegramRecords", filterWrapper);
    }

    public Integer getTablegramRecordsCount(FilterWrapper<TablegramRecord> filterWrapper){
        return sqlSession().selectOne("selectTablegramRecordsCount", filterWrapper);
    }

    public List<TablegramRecord> getTablegramRecords(Long tablegramId){
        return sqlSession().selectList("selectTablegramRecordIdByTablegramId", tablegramId);
    }

    public void rollback(Long tablegramId){
        sqlSession().update("rollbackTablegramStatus", tablegramId);
    }

}
