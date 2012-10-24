/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.component.heatmeter.bind;

import java.util.Locale;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterBindingStatus;
import org.complitex.keconnection.heatmeter.service.exception.CriticalHeatmeterBindException;
import org.complitex.keconnection.heatmeter.service.exception.DBException;
import org.complitex.keconnection.heatmeter.service.exception.HeatmeterBindException;

/**
 *
 * @author Artem
 */
public class HeatmeterBindError {

    private static final String RESOURCE_BUNDLE = HeatmeterBindError.class.getName();

    private HeatmeterBindError() {
    }

    private static AddressRendererBean addressRendererBean() {
        return EjbBeanLocator.getBean(AddressRendererBean.class);
    }

    public static String message(Heatmeter heatmeter, Exception ex, Locale locale) {
        try {
            throw ex;
        } catch (HeatmeterBindException e) {
            return bindingError(heatmeter, e.getStatus(), locale);
        } catch (DBException e) {
            return getString("heatmeter_bind_db_error", locale);
        } catch (CriticalHeatmeterBindException e) {
            return getStringFormat("critical_heatmeter_bind_error", locale, e.getCause());
        } catch (Exception e) {
            return getStringFormat("critical_heatmeter_bind_error", locale, e);
        }
    }

    private static String getString(String key, Locale locale) {
        return getStringFormat(key, locale);
    }

    private static String getStringFormat(String key, Locale locale, Object... params) {
        return ResourceUtil.getFormatString(RESOURCE_BUNDLE, key, locale, params);
    }

    private static String bindingError(Heatmeter heatmeter, HeatmeterBindingStatus status, Locale locale) {
        final long id = heatmeter.getId();
        final int ls = heatmeter.getLs();

        String message;
        switch (status) {
            case BINDING_ERROR: {
                message = getStringFormat("heatmeter_bind_error", locale, id, ls);
            }
            break;
            case BUILDING_NOT_FOUND: {
                final long buildingId = heatmeter.getConnections().get(0).getBuildingId();
                message = getStringFormat("heatmeter_bind_building_not_found", locale, id, ls,
                        addressRendererBean().displayBuildingSimple(buildingId, locale));
            }
            break;
            case ORGANIZATION_NOT_FOUND: {
                final long buildingId = heatmeter.getConnections().get(0).getBuildingId();
                message = getStringFormat("heatmeter_bind_organization_not_found", locale, id, ls,
                        addressRendererBean().displayBuildingSimple(buildingId, locale));
            }
            break;
            case MORE_ONE_EXTERNAL_HEATMETER: {
                final long buildingId = heatmeter.getConnections().get(0).getBuildingId();
                message = getStringFormat("heatmeter_bind_more_one_external", locale, id, ls,
                        addressRendererBean().displayBuildingSimple(buildingId, locale));
            }
            break;
            case NO_EXTERNAL_HEATMETERS: {
                final long buildingId = heatmeter.getConnections().get(0).getBuildingId();
                message = getStringFormat("heatmeter_bind_no_external", locale, id, ls,
                        addressRendererBean().displayBuildingSimple(buildingId, locale));
            }
            break;
            default:
                throw new IllegalStateException("Impossible code path.");
        }
        return message;
    }
}
