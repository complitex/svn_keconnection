package org.complitex.keconnection.heatmeter.entity;

import java.util.Date;

import static org.complitex.dictionary.util.DateUtil.format;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.10.12 13:11
 */
public class HeatmeterValidate {
    private HeatmeterValidateStatus status;

    private HeatmeterPeriod p1;
    private HeatmeterPeriod p2;

    private Date date;

    public HeatmeterValidate(HeatmeterValidateStatus status) {
        this.status = status;
    }

    public HeatmeterValidate(HeatmeterValidateStatus status, Date date) {
        this.status = status;
        this.date = date;
    }

    public HeatmeterValidate(HeatmeterValidateStatus status, HeatmeterPeriod p1) {
        this.status = status;
        this.p1 = p1;
    }

    public HeatmeterValidate(HeatmeterValidateStatus status, HeatmeterPeriod p1, HeatmeterPeriod p2) {
        this.status = status;
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public String toString() {
        String s = "";

        if (p1 != null && p1.getBeginDate() != null){
            s =  format(p1.getBeginDate(), p1.getEndDate());
        }

        if(p2 != null && p2.getBeginDate() != null){
            s += ", " + format(p2.getBeginDate(), p2.getEndDate());
        }

        if (date != null){
            s = format(date);
        }

        return s;
    }

    public HeatmeterValidateStatus getStatus() {
        return status;
    }

    public HeatmeterPeriod getP1() {
        return p1;
    }

    public HeatmeterPeriod getP2() {
        return p2;
    }
}
