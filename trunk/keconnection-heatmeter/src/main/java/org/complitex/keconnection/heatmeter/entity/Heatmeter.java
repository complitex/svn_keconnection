package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.ILongId;

import java.util.List;

/**
 * @author  Anatoly A. Ivanov java@inheaven.ru
 * Date: 04.09.12 15:10
 *
 * Теплосчетчик
 */
public class Heatmeter implements ILongId{
    private Long id; //Идентификатор
    private Integer ls; //Номер л/с счетчика
    private HeatmeterType type; //Тип счетчика
    private Long buildingCodeId; //Ссылка на код дома
    private List<HeatmeterPeriod> periods;

    private HeatmeterPeriodType status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLs() {
        return ls;
    }

    public void setLs(Integer ls) {
        this.ls = ls;
    }

    public HeatmeterType getType() {
        return type;
    }

    public void setType(HeatmeterType type) {
        this.type = type;
    }

    public Long getBuildingCodeId() {
        return buildingCodeId;
    }

    public void setBuildingCodeId(Long buildingCodeId) {
        this.buildingCodeId = buildingCodeId;
    }

    public List<HeatmeterPeriod> getPeriods() {
        return periods;
    }

    public void setPeriods(List<HeatmeterPeriod> periods) {
        this.periods = periods;
    }

    public HeatmeterPeriodType getStatus() {
        return status;
    }

    public void setStatus(HeatmeterPeriodType status) {
        this.status = status;
    }
}
