package org.complitex.keconnection.heatmeter.web.component;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;

import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.10.12 16:25
 */
public class AbstractHeatmeterEditPanel extends Panel{
    private IModel<Heatmeter> model;
    private IModel<Date> operatingMonthModel;

    public AbstractHeatmeterEditPanel(String id, final IModel<Heatmeter> model, final IModel<Date> operatingMonthModel) {
        super(id);

        this.model = model;
        this.operatingMonthModel = operatingMonthModel;
    }

    protected boolean isCurrentOperationMonth(){
//        return isSameMonth(getFirstDayOfCurrentMonth(), operatingMonthModel.getObject());
        return true;
    }

    public IModel<Heatmeter> getModel() {
        return model;
    }

    public IModel<Date> getOperatingMonthModel() {
        return operatingMonthModel;
    }
}
