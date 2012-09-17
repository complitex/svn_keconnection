package org.complitex.keconnection.heatmeater.entity;

import org.complitex.dictionary.entity.ILongId;

/**
 * @author  Anatoly A. Ivanov java@inheaven.ru
 * Date: 04.09.12 15:10
 *
 * Теплосчетчик
 */
public class Heatmeater implements ILongId{
    private Long id; //Идентификатор
    private Integer ls; //Номер л/с счетчика
    private HeatmeterType type; //Тип счетчика
    private Long buildingCodeId; //Ссылка на код дома

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
}
