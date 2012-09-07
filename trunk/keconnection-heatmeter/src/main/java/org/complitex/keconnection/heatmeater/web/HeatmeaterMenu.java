package org.complitex.keconnection.heatmeater.web;

import org.complitex.template.web.template.ResourceTemplateMenu;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 07.09.12 14:39
 */
public class HeatmeaterMenu extends ResourceTemplateMenu {
    public HeatmeaterMenu() {
        add("list", HeatmeaterList.class);
    }
}
