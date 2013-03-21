package org.complitex.keconnection.heatmeter.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 07.11.12 18:43
 */
public class HeatmeterInput extends HeatmeterPeriod {
    private BigDecimal value;
    private List<HeatmeterConsumption> consumptions = new ArrayList<>();

    public HeatmeterInput() {
        super(INPUT);
    }

    public HeatmeterInput(Long heatmeterId, Date beginOm) {
        super(INPUT);

        setHeatmeterId(heatmeterId);
        setBeginOm(beginOm);
    }

    @Override
    public boolean isSameValue(HeatmeterPeriod p) {
        if (p instanceof HeatmeterInput){
            return super.isSameValue(p) && (Objects.equals(value, ((HeatmeterInput) p).getValue()));
        }

        return super.isSameValue(p);
    }

    public HeatmeterConsumption getSumConsumption(){
        HeatmeterConsumption consumption = new HeatmeterConsumption();

        for (HeatmeterConsumption c : consumptions){
            consumption.setConsumption1(consumption.getConsumption1().add(c.getConsumption1()));
            consumption.setConsumption2(consumption.getConsumption2().add(c.getConsumption2()));
            consumption.setConsumption3(consumption.getConsumption3().add(c.getConsumption3()));
        }

        return consumption;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public List<HeatmeterConsumption> getConsumptions() {
        return consumptions;
    }

    public void setConsumptions(List<HeatmeterConsumption> consumptions) {
        this.consumptions = consumptions;
    }
}
