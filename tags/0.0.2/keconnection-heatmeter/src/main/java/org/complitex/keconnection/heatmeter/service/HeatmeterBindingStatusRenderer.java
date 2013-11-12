/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.service;

import java.util.Locale;
import javax.ejb.Stateless;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.keconnection.heatmeter.entity.HeatmeterBindingStatus;

/**
 *
 * @author Artem
 */
@Stateless
public class HeatmeterBindingStatusRenderer {

    private static final String RESOURCE_BUNDLE = HeatmeterBindingStatusRenderer.class.getName();

    public String render(HeatmeterBindingStatus status, Locale locale) {
        if (status == null) {
            status = HeatmeterBindingStatus.UNBOUND;
        }
        return ResourceUtil.getString(RESOURCE_BUNDLE, status.name(), locale);
    }
}
