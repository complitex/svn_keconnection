package org.complitex.keconnection.heatmeter.web;

import org.complitex.template.web.template.ResourceTemplateMenu;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 07.09.12 14:39
 */
public class HeatmeterMenu extends ResourceTemplateMenu {

    public HeatmeterMenu() {
        add("heatmeter_list", HeatmeterList.class);
        add("tablegram_list", TablegramList.class);
        //add("payload_list", PayloadList.class);
    }
}
