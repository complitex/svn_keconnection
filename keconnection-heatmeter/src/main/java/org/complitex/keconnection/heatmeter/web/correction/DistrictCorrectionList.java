/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.correction;

import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.keconnection.heatmeter.entity.Correction;
import org.complitex.keconnection.heatmeter.entity.example.CorrectionExample;
import org.complitex.keconnection.heatmeter.service.AddressCorrectionBean;
import org.complitex.keconnection.heatmeter.web.AddressRenderer;

/**
 *
 * @author Artem
 */
public class DistrictCorrectionList extends AddressCorrectionList {

    @EJB
    private AddressCorrectionBean addressCorrectionBean;

    public DistrictCorrectionList() {
        super("district");
    }

    @Override
    protected List<? extends Correction> find(CorrectionExample example) {
        return addressCorrectionBean.findDistricts(example);
    }

    @Override
    protected IModel<String> getTitleModel() {
        return new StringResourceModel("title", this, null);
    }

    @Override
    protected String displayCorrection(Correction correction) {
        String city = null;
        if (correction.getParent() != null) {
            city = correction.getParent().getCorrection();
        }
        return AddressRenderer.displayAddress(null, city, correction.getCorrection(), getLocale());
    }
}
