package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.ILongId;

import java.math.BigDecimal;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 07.11.12 18:43
 */
public class HeatmeterInput implements ILongId{
    private Long id;
    private BigDecimal value;

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
