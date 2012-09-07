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
    private Integer gek; //Код структурной единицы
    private Integer dom; //Код дома
    private String ul; //Название улицы вместе с типом
    private String ndom; //Номер дома
    private Integer lotop0; //Номер л/с первого счетчика в доме
    private Integer lotop1;
    private Integer lotop2;
    private Integer lotop3;
    private Integer lotop4;

    private Long organizationId; //Идентификатор структурной единицы
    private Long buildingId; //Идентификатор дома

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGek() {
        return gek;
    }

    public void setGek(Integer gek) {
        this.gek = gek;
    }

    public Integer getDom() {
        return dom;
    }

    public void setDom(Integer dom) {
        this.dom = dom;
    }

    public String getUl() {
        return ul;
    }

    public void setUl(String ul) {
        this.ul = ul;
    }

    public String getNdom() {
        return ndom;
    }

    public void setNdom(String ndom) {
        this.ndom = ndom;
    }

    public Integer getLotop0() {
        return lotop0;
    }

    public void setLotop0(Integer lotop0) {
        this.lotop0 = lotop0;
    }

    public Integer getLotop1() {
        return lotop1;
    }

    public void setLotop1(Integer lotop1) {
        this.lotop1 = lotop1;
    }

    public Integer getLotop2() {
        return lotop2;
    }

    public void setLotop2(Integer lotop2) {
        this.lotop2 = lotop2;
    }

    public Integer getLotop3() {
        return lotop3;
    }

    public void setLotop3(Integer lotop3) {
        this.lotop3 = lotop3;
    }

    public Integer getLotop4() {
        return lotop4;
    }

    public void setLotop4(Integer lotop4) {
        this.lotop4 = lotop4;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }
}
