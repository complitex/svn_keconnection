package org.complitex.keconnection.heatmeter.entity;

import java.util.Date;

import static org.complitex.dictionary.util.DateUtil.format;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.10.12 13:11
 */
public class HeatmeterValidate {
    private HeatmeterValidateStatus status;

    private Date p1BeginDate;
    private Date p1EndDate;

    private Date p2BeginDate;
    private Date p2EndDate;

    public HeatmeterValidate(HeatmeterValidateStatus status) {
        this.status = status;
    }

    public HeatmeterValidate(HeatmeterValidateStatus status, Date p1BeginDate) {
        this.status = status;
        this.p1BeginDate = p1BeginDate;
    }

    public HeatmeterValidate(HeatmeterValidateStatus status, Date p1BeginDate, Date p1EndDate) {
        this.status = status;
        this.p1BeginDate = p1BeginDate;
        this.p1EndDate = p1EndDate;
    }

    public HeatmeterValidate(HeatmeterValidateStatus status, Date p1BeginDate, Date p1EndDate, Date p2BeginDate, Date p2EndDate) {
        this.status = status;
        this.p1BeginDate = p1BeginDate;
        this.p1EndDate = p1EndDate;
        this.p2BeginDate = p2BeginDate;
        this.p2EndDate = p2EndDate;
    }

    @Override
    public String toString() {
        String s = "";

        if (p1BeginDate != null ){
            s =  format(p1BeginDate, p1EndDate);
        }

        if(p2BeginDate != null){
            s += ", " + format(p2BeginDate, p2EndDate);
        }

        return s;
    }

    public HeatmeterValidateStatus getStatus() {
        return status;
    }

    public Date getP1BeginDate() {
        return p1BeginDate;
    }

    public Date getP1EndDate() {
        return p1EndDate;
    }

    public Date getP2BeginDate() {
        return p2BeginDate;
    }

    public Date getP2EndDate() {
        return p2EndDate;
    }
}
