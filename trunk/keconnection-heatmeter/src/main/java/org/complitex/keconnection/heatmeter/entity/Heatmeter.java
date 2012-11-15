package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.ILongId;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author  Anatoly A. Ivanov java@inheaven.ru
 * Date: 04.09.12 15:10
 *
 * Теплосчетчик
 */
public class Heatmeter implements ILongId {
    private Long id; //Идентификатор
    private Integer ls; //Номер л/с счетчика
    private Long organizationId; //Организация ПУ
    private HeatmeterType type; //Тип счетчика
    private Boolean calculating; //Участвует в расчетах

    private Date om;

    private List<HeatmeterConnection> connections = new ArrayList<>(); //Список кодов домов
    private List<HeatmeterPeriod> periods = new ArrayList<>(); //Список периодов
    private List<HeatmeterPayload> payloads = new ArrayList<>(); //Список распределений
    private List<HeatmeterInput> inputs = new ArrayList<>();
    private List<HeatmeterConsumption> consumptions = new ArrayList<>();// Список расходов

    private HeatmeterStatus status;
    private HeatmeterBindingStatus bindingStatus;

    public boolean isConnectedToSingleBuildingCode() {
        return getBuildingCodeIds().size() == 1;
    }

    public Long getFirstBuildingCodeId() {
        return connections == null || connections.isEmpty() ? null : connections.get(0).getBuildingCodeId();
    }

    public Set<Long> getBuildingCodeIds() {
        Set<Long> buildingCodeIds = new HashSet<>();
        for (HeatmeterConnection hc : connections) {
            buildingCodeIds.add(hc.getBuildingCodeId());
        }
        return buildingCodeIds;
    }

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

    public Boolean getCalculating() {
        return calculating;
    }

    public void setCalculating(Boolean calculating) {
        this.calculating = calculating;
    }

    public Date getOm() {
        return om;
    }

    public void setOm(Date om) {
        this.om = om;
    }

    public List<HeatmeterConnection> getConnections() {
        return connections;
    }

    public void setConnections(List<HeatmeterConnection> connections) {
        this.connections = connections;
    }

    public List<HeatmeterPeriod> getPeriods() {
        return periods;
    }

    public void setPeriods(List<HeatmeterPeriod> periods) {
        this.periods = periods;
    }

    public List<HeatmeterPayload> getPayloads() {
        return payloads;
    }

    public void setPayloads(List<HeatmeterPayload> payloads) {
        this.payloads = payloads;
    }

    public List<HeatmeterInput> getInputs() {
        return inputs;
    }

    public void setInputs(List<HeatmeterInput> inputs) {
        this.inputs = inputs;
    }

    public List<HeatmeterConsumption> getConsumptions() {
        return consumptions;
    }

    public void setConsumptions(List<HeatmeterConsumption> consumptions) {
        this.consumptions = consumptions;
    }

    public HeatmeterStatus getStatus() {
        return status;
    }

    public void setStatus(HeatmeterStatus status) {
        this.status = status;
    }

    public HeatmeterBindingStatus getBindingStatus() {
        return bindingStatus;
    }

    public void setBindingStatus(HeatmeterBindingStatus bindingStatus) {
        this.bindingStatus = bindingStatus;
    }
}
