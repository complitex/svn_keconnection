package org.complitex.keconnection.heatmeter.web.component;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;

import java.util.Date;

import static org.complitex.dictionary.util.DateUtil.isSameMonth;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 16:25
 */
public class AbstractHeatmeterEditPanel extends Panel{
    private IModel<Heatmeter> model;
    private IModel<Date> om;

    public AbstractHeatmeterEditPanel(String id, final IModel<Heatmeter> model, final IModel<Date> om) {
        super(id);

        this.model = model;
        this.om = om;
    }

    protected boolean isActiveOm(){
        return om.getObject() == null || isSameMonth(model.getObject().getOm(), om.getObject());
    }

    public IModel<Heatmeter> getModel() {
        return model;
    }

    public IModel<Date> getOm() {
        return om;
    }
}
