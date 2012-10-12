/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.entity;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 *
 * @author Artem
 */
public class ExternalHeatmeter implements Serializable {

    private String id;
    private String number;

    public ExternalHeatmeter() {
    }

    public ExternalHeatmeter(String id, String number) {
        this.id = id;
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return MessageFormat.format("'{'id: {0}, number: {1}'}'", getId(), getNumber());
    }
}
