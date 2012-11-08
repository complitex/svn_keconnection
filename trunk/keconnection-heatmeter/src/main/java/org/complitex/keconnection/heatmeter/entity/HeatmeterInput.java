package org.complitex.keconnection.heatmeter.entity;

import java.math.BigDecimal;

import static org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.INPUT;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 07.11.12 18:43
 */
public class HeatmeterInput extends HeatmeterAttribute {
    private Long id;
    private BigDecimal value;

    @Override
    public HeatmeterPeriodType getType() {
        return INPUT;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
