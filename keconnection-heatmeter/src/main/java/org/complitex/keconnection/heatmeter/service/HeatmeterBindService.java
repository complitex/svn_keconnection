/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.service;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.service.IProcessListener;
import static org.complitex.dictionary.util.DateUtil.*;
import org.complitex.keconnection.address.strategy.building.KeConnectionBuildingStrategy;
import org.complitex.keconnection.address.strategy.building.entity.BuildingCode;
import org.complitex.keconnection.heatmeter.entity.ExternalHeatmeter;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterBindingStatus;
import org.complitex.keconnection.heatmeter.entity.HeatmeterCorrection;
import org.complitex.keconnection.heatmeter.service.ExternalHeatmeterService.ExternalHeatmeterAndStatus;
import org.complitex.keconnection.heatmeter.service.ExternalHeatmeterService.ExternalHeatmetersAndStatus;
import org.complitex.keconnection.heatmeter.service.exception.CriticalHeatmeterBindException;
import org.complitex.keconnection.heatmeter.service.exception.DBException;
import org.complitex.keconnection.heatmeter.service.exception.HeatmeterBindException;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class HeatmeterBindService {

    private static final Logger log = LoggerFactory.getLogger(HeatmeterBindService.class);
    private static final int BINDING_BATCH = 1000;
    @EJB
    private HeatmeterBindBean heatmeterBindBean;
    @EJB
    private HeatmeterBean heatmeterBean;
    @Resource
    private UserTransaction userTransaction;
    @EJB(name = IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME)
    private IKeConnectionOrganizationStrategy organizationStrategy;
    @EJB
    private KeConnectionBuildingStrategy buildingStrategy;
    @EJB
    private HeatmeterCorrectionBean heatmeterCorrectionBean;
    @EJB
    private ExternalHeatmeterService externalHeatmeterService;
    private volatile boolean processing;

    public boolean isProcessing() {
        return processing;
    }

    @Asynchronous
    public void bindAll(IProcessListener<Heatmeter> listener) {
        if (processing) {
            return;
        }

        processing = true;

        Heatmeter heatmeter = null;
        try {
            heatmeterBindBean.fillHeatmeterBind();

            List<Long> batch;
            while ((batch = heatmeterBindBean.getBatch(BINDING_BATCH)) != null && !batch.isEmpty()) {
                try {
                    userTransaction.begin();

                    for (long heatmeterId : batch) {
                        heatmeter = heatmeterBean.getHeatmeter(heatmeterId);

                        if (heatmeter != null && heatmeter.getHeatmeterCodes() != null
                                && heatmeter.getHeatmeterCodes().size() == 1) {
                            Long buildingCodeId = heatmeter.getHeatmeterCodes().get(0).getBuildingCodeId();
                            if (buildingCodeId != null) {
                                BuildingCode buildingCodeObj = buildingStrategy.getBuildingCodeById(buildingCodeId);
                                if (buildingCodeObj != null) {
                                    final long organizationId = buildingCodeObj.getOrganizationId();
                                    final int buildingCode = buildingCodeObj.getBuildingCode();
                                    final String organizationCode = organizationStrategy.getUniqueCode(organizationId);

                                    //fetch external heatmeter.
                                    ExternalHeatmeterAndStatus ehs = externalHeatmeterService.fetchExternalHeatmeter(
                                            heatmeterId, heatmeter.getLs(), organizationCode, buildingCode,
                                            getDateParameter());

                                    final ExternalHeatmeter externalHeatmeter = ehs.heatmeter;
                                    final HeatmeterBindingStatus status = ehs.status;

                                    updateHeatmeterCorrection(heatmeter, externalHeatmeter, status);

                                    if (status == HeatmeterBindingStatus.BOUND) {
                                        listener.processed(heatmeter);
                                    } else {
                                        listener.error(heatmeter, new HeatmeterBindException(status));
                                    }
                                }
                            }
                        }
                    }
                    heatmeterBindBean.markProcessed(batch);
                    userTransaction.commit();
                } catch (Exception e) {
                    try {
                        userTransaction.rollback();
                    } catch (Exception ex) {
                        log.error("Couldn't rollback transaction.", ex);
                    }

                    throw e;
                }
            }
        } catch (DBException e) {
            log.error("DB exception during heatmeter binding process.", e);
            listener.error(heatmeter, e);
        } catch (Exception e) {
            log.error("Critical exception during heatmeter binding process.", e);
            listener.error(heatmeter, new CriticalHeatmeterBindException(e));
        } finally {
            processing = false;
            listener.done();

            try {
                heatmeterBindBean.delete();
            } catch (Exception e) {
                log.error("Couldn't clean heatmeter_bind table.", e);
            }
        }
    }
    
    public void updateHeatmeterCorrection(Heatmeter heatmeter, ExternalHeatmeter externalHeatmeter, 
            HeatmeterBindingStatus status) {
        final long heatmeterId = heatmeter.getId();
        HeatmeterCorrection correction = heatmeterCorrectionBean.findById(heatmeterId);
        HeatmeterCorrection newCorrection = null;
        if (externalHeatmeter == null) {
            if (correction == null) {
                newCorrection = new HeatmeterCorrection(heatmeterId);
                newCorrection.setBindingStatus(status);
            } else {
                if (status != correction.getBindingStatus()) {
                    newCorrection = new HeatmeterCorrection(heatmeterId);
                    newCorrection.setBindingStatus(status);
                }
            }
        } else {
            if (correction == null) {
                newCorrection = new HeatmeterCorrection(heatmeterId,
                        externalHeatmeter.getId(), externalHeatmeter.getNumber(), status);
            } else {
                if (!Strings.isEqual(externalHeatmeter.getId(), correction.getExternalHeatmeterId())
                        || !Strings.isEqual(externalHeatmeter.getNumber(), correction.getHeatmeterNumber())) {
                    newCorrection = new HeatmeterCorrection(heatmeterId,
                            externalHeatmeter.getId(), externalHeatmeter.getNumber(), HeatmeterBindingStatus.BOUND);
                }
            }
        }

        final Date bindingDate = getCurrentDate();
        if (newCorrection != null) {
            heatmeterCorrectionBean.markHistory(heatmeterId);
            newCorrection.setBindingDate(bindingDate);
            heatmeterCorrectionBean.insert(newCorrection);
        } else if (correction != null) {
            correction.setBindingDate(bindingDate);
            heatmeterCorrectionBean.updateBindingDate(correction);
        }
    }

    private Date getDateParameter() {
        final Date currentDate = getCurrentDate();
        return getFirstDayOfMonth(getYear(currentDate), getMonth(currentDate) + 1);
    }

    public List<ExternalHeatmeter> getExternalHeatmeters(Heatmeter heatmeter) throws DBException, HeatmeterBindException {
        if (heatmeter != null && heatmeter.getHeatmeterCodes() != null
                && heatmeter.getHeatmeterCodes().size() == 1) {
            Long buildingCodeId = heatmeter.getHeatmeterCodes().get(0).getBuildingCodeId();
            if (buildingCodeId != null) {
                BuildingCode buildingCodeObj = buildingStrategy.getBuildingCodeById(buildingCodeId);
                if (buildingCodeObj != null) {
                    final long organizationId = buildingCodeObj.getOrganizationId();
                    final int buildingCode = buildingCodeObj.getBuildingCode();
                    final String organizationCode = organizationStrategy.getUniqueCode(organizationId);

                    ExternalHeatmetersAndStatus ehs = externalHeatmeterService.fetchExternalHeatmeters(
                            heatmeter.getId(), heatmeter.getLs(), organizationCode, buildingCode, getDateParameter());
                    if (ehs.heatmeters != null && !ehs.heatmeters.isEmpty()) {
                        return ehs.heatmeters;
                    }

                    throw new HeatmeterBindException(ehs.status);
                }
            }
        }
        return null;
    }
}
