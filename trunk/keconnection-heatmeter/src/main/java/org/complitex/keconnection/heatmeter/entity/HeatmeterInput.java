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

    public HeatmeterConsumption getFirstConsumption() {
        return consumptions.get(0);
    }

    public void addNewConsumptionIfNecessary() {
        if (consumptions.isEmpty()) {
            consumptions.add(new HeatmeterConsumption());
        }
    }
}
