/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.service;

import org.apache.wicket.util.string.Strings;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.BuildingCode;
import org.complitex.dictionary.service.IProcessListener;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.keconnection.heatmeter.entity.ExternalHeatmeter;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterBindingStatus;
import org.complitex.keconnection.heatmeter.entity.HeatmeterCorrection;
import org.complitex.keconnection.heatmeter.service.ExternalHeatmeterService.ExternalHeatmetersAndStatus;
import org.complitex.keconnection.heatmeter.service.exception.CriticalHeatmeterBindException;
import org.complitex.keconnection.heatmeter.service.exception.DBException;
import org.complitex.keconnection.heatmeter.service.exception.HeatmeterBindException;
import org.complitex.keconnection.organization.strategy.KeConnectionOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.transaction.UserTransaction;
import java.util.*;

import static org.complitex.dictionary.util.DateUtil.*;

/**
 *
 * @author Artem
 */
@Singleton
@TransactionManagement(TransactionManagementType.BEAN)
public class HeatmeterBindService {

    private final Logger log = LoggerFactory.getLogger(HeatmeterBindService.class);
    private static final int BINDING_BATCH = 1000;
    @EJB
    private HeatmeterBindBean heatmeterBindBean;
    @EJB
    private HeatmeterBean heatmeterBean;
    @Resource
    private UserTransaction userTransaction;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeConnectionOrganizationStrategy organizationStrategy;

    @EJB
    private BuildingStrategy buildingStrategy;
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
                        heatmeter = heatmeterBean.getHeatmeterForBinding(heatmeterId);

                        if (heatmeter != null) {
                            if (heatmeter.isConnectedToSingleBuildingCode()) {
                                // подключен только к одному коду дома
                                final long buildingCodeId = heatmeter.getFirstBuildingCodeId();
                                if (heatmeterBean.isOnlyHeatmeterForBuildingCode(heatmeterId, buildingCodeId)) {
                                    // это единственный счетчик, подключенный к коду дома

                                    //fetch external heatmeters.
                                    ExternalHeatmetersAndStatus ehas =
                                            fetchExternalHeatmeters(buildingCodeId, heatmeterId, heatmeter.getLs());
                                    final HeatmeterBindingStatus status = ehas.status;
                                    final ExternalHeatmeter externalHeatmeter =
                                            ehas.heatmeters != null && ehas.heatmeters.size() == 1
                                            ? ehas.heatmeters.get(0) : null;

                                    //обновить соответствие
                                    updateHeatmeterCorrection(heatmeter, externalHeatmeter, status);

                                    //сообщить в UI об ошибках
                                    if (status == HeatmeterBindingStatus.BOUND
                                            || status == HeatmeterBindingStatus.NO_EXTERNAL_HEATMETERS) {
                                        listener.processed(heatmeter);
                                    } else {
                                        listener.error(heatmeter, new HeatmeterBindException(status));
                                    }
                                } else {
                                    // к данному коду дома подключенно более одного счетчика
                                    updateHeatmeterCorrection(heatmeter, null,
                                            HeatmeterBindingStatus.MORE_ONE_EXTERNAL_HEATMETER);

                                    //сообщить в UI об ошибках
                                    listener.error(heatmeter, new HeatmeterBindException(
                                            HeatmeterBindingStatus.MORE_ONE_EXTERNAL_HEATMETER));
                                }
                            } else {
                                // счетчик подключен к нескольким кодам домов

                                boolean bound = true;
                                LinkedList<ExternalHeatmeter> externalHeatmeters = new LinkedList<>();

                                // делаем запросы для всех кодов домов
                                List<ExternalHeatmetersAndStatus> ehass = new ArrayList<>();
                                for (long buildingCodeId : heatmeter.getBuildingCodeIds()) {
                                    ehass.add(fetchExternalHeatmeters(buildingCodeId, heatmeterId, heatmeter.getLs()));
                                }

                                //1. все запросы возвращают один внешний счетчик
                                {
                                    for (ExternalHeatmetersAndStatus ehas : ehass) {
                                        if (ehas.status == HeatmeterBindingStatus.BOUND) {
                                            externalHeatmeters.add(ehas.heatmeters.get(0));
                                        } else {
                                            bound = false;
                                            break;
                                        }
                                    }

                                    if (bound) {
                                        // все возвращаемые счетчики имеют одинаковый ID

                                        ExternalHeatmeter first = externalHeatmeters.getFirst();
                                        for (ExternalHeatmeter e : externalHeatmeters) {
                                            if (!first.getId().equals(e.getId())) {
                                                bound = false;
                                                break;
                                            }
                                        }

                                        if (bound) {
                                            updateHeatmeterCorrection(heatmeter, first, HeatmeterBindingStatus.BOUND);
                                        }
                                    }
                                }

                                if (!bound) {
                                    bound = true;
                                    
                                    //2. все запросы возвращают пустой курсор
                                    {
                                        for (ExternalHeatmetersAndStatus ehas : ehass) {
                                            if (ehas.status != HeatmeterBindingStatus.NO_EXTERNAL_HEATMETERS) {
                                                bound = false;
                                                break;
                                            }
                                        }

                                        if (bound) {
                                            updateHeatmeterCorrection(heatmeter, null,
                                                    HeatmeterBindingStatus.NO_EXTERNAL_HEATMETERS);
                                        }
                                    }
                                }

                                //сообщить в UI об ошибках
                                if (!bound) {
                                    listener.error(heatmeter, new HeatmeterBindException(
                                            HeatmeterBindingStatus.MORE_ONE_EXTERNAL_HEATMETER));
                                } else {
                                    listener.processed(heatmeter);
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

    private ExternalHeatmetersAndStatus fetchExternalHeatmeters(long buildingCodeId, long heatmeterId, int ls)
            throws DBException {
        final BuildingCode buildingCodeObj = buildingStrategy.getBuildingCodeById(buildingCodeId);
        final long organizationId = buildingCodeObj.getOrganizationId();
        final int buildingCode = buildingCodeObj.getBuildingCode();
        final String organizationCode = organizationStrategy.getCode(organizationId);

        return externalHeatmeterService.fetchExternalHeatmeters(
                heatmeterId, ls, organizationCode, buildingCode,
                getDateParameter());
    }

    public void updateHeatmeterCorrection(Heatmeter heatmeter, ExternalHeatmeter externalHeatmeter,
            HeatmeterBindingStatus status) {
        final long heatmeterId = heatmeter.getId();
        HeatmeterCorrection correction = heatmeterCorrectionBean.findByHeatmeterId(heatmeterId);
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
        return newDate(getYear(currentDate), getMonth(currentDate) + 1);
    }

    public List<ExternalHeatmeter> getExternalHeatmeters(Heatmeter heatmeter) throws DBException,
            HeatmeterBindException {
        HeatmeterBindingStatus errorStatus = null;
        Map<String, ExternalHeatmeter> externalHeatmeters = new HashMap<>();
        for (long buildingCodeId : heatmeter.getBuildingCodeIds()) {
            ExternalHeatmetersAndStatus ehas = fetchExternalHeatmeters(buildingCodeId, heatmeter.getId(), heatmeter.getLs());
            if (ehas.heatmeters != null && !ehas.heatmeters.isEmpty()) {
                for (ExternalHeatmeter e : ehas.heatmeters) {
                    externalHeatmeters.put(e.getId(), e);
                }
            } else if (errorStatus == null) {
                errorStatus = ehas.status;
                break;
            }
        }

        if (errorStatus != null) {
            throw new HeatmeterBindException(errorStatus);
        }

        return new ArrayList<>(externalHeatmeters.values());
    }
}
