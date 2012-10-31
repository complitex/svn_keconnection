package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.IDateRange;

import java.util.Date;

import static org.complitex.dictionary.util.DateUtil.format;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.10.12 13:11
 */
public class HeatmeterValidate {
    private HeatmeterValidateStatus status;

    private IDateRange dr1;
    private IDateRange dr2;

    private Date date;

    public HeatmeterValidate(HeatmeterValidateStatus status) {
        this.status = status;
    }

    public HeatmeterValidate(HeatmeterValidateStatus status, Date date) {
        this.status = status;
        this.date = date;
    }

    public HeatmeterValidate(HeatmeterValidateStatus status, IDateRange dr1) {
        this.status = status;
        this.dr1 = dr1;
    }

    public HeatmeterValidate(HeatmeterValidateStatus status, IDateRange dr1, IDateRange dr2) {
        this.status = status;
        this.dr1 = dr1;
        this.dr2 = dr2;
    }

    @Override
    public String toString() {
        String s = "";

        if (dr1 != null && dr1.getBeginDate() != null){
            s =  format(dr1.getBeginDate(), dr1.getEndDate());
        }

        if(dr2 != null && dr2.getBeginDate() != null){
            s += ", " + format(dr2.getBeginDate(), dr2.getEndDate());
        }

        if (date != null){
            s = format(date);
        }

        return s;
    }

    public HeatmeterValidateStatus getStatus() {
        return status;
    }

    public IDateRange getDr1() {
        return dr1;
    }

    public IDateRange getDr2() {
        return dr2;
    }
}
