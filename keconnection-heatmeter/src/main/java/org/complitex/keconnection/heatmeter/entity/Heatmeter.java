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
    private Long organizationId; //Организация ПУ
    private HeatmeterType type; //Тип счетчика
    private List<HeatmeterCode> heatmeterCodes; //Список кодов домов
    private List<HeatmeterPeriod> periods; //Список периодов

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

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public HeatmeterType getType() {
        return type;
    }

    public void setType(HeatmeterType type) {
        this.type = type;
    }

    public List<HeatmeterCode> getHeatmeterCodes() {
        return heatmeterCodes;
    }

    public void setHeatmeterCodes(List<HeatmeterCode> heatmeterCodes) {
        this.heatmeterCodes = heatmeterCodes;
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
