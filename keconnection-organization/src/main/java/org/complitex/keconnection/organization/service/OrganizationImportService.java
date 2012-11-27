/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.service;

import org.complitex.dictionary.util.DateUtil;
import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Strings;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.dictionary.converter.BooleanConverter;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.service.AbstractImportService;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.IImportListener;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.exception.ImportFileNotFoundException;
import org.complitex.dictionary.service.exception.ImportFileReadException;
import org.complitex.dictionary.util.AttributeUtil;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.keconnection.organization.entity.OrganizationImport;
import org.complitex.keconnection.organization.service.exception.RootOrganizationNotFound;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.complitex.keconnection.organization_type.strategy.KeConnectionOrganizationTypeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.complitex.keconnection.organization.entity.OrganizationImportFile.*;

/**
 *
 * @author Artem
 */
@Stateless
public class OrganizationImportService extends AbstractImportService {

    private static final Logger log = LoggerFactory.getLogger(OrganizationImportService.class);
    @EJB
    private OrganizationImportBean organizationImportBean;
    @EJB(name = IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME)
    private IKeConnectionOrganizationStrategy organizationStrategy;
    @EJB
    private LocaleBean localeBean;

    /**
     * ID CODE SHORT_NAME NAME HLEVEL
     */
    public void process(IImportListener listener, long localeId, Date beginOm)
            throws ImportFileNotFoundException, ImportFileReadException, RootOrganizationNotFound {

        organizationImportBean.delete();

        listener.beginImport(ORGANIZATION, getRecordCount(ORGANIZATION));

        CSVReader reader = getCsvReader(ORGANIZATION);

        int recordIndex = 0;

        try {
            String[] line;
            while ((line = reader.readNext()) != null) {
                final long organizationId = Long.parseLong(line[0].trim());
                final String code = line[1].trim();
                final String shortName = line[2].trim();
                final String fullName = line[3].trim();
                final String hlevelString = line[4];
                final Long hlevel = Strings.isNullOrEmpty(hlevelString) ? null : Long.parseLong(hlevelString);
                organizationImportBean.importOrganization(new OrganizationImport(organizationId, code, shortName, fullName, hlevel));
                recordIndex++;
            }
        } catch (IOException e) {
            throw new ImportFileReadException(e, ORGANIZATION.getFileName(), recordIndex);
        } catch (NumberFormatException e) {
            throw new ImportFileReadException(e, ORGANIZATION.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }

        final long systemLocaleId = localeBean.getSystemLocaleObject().getId();
        recordIndex = 0;

        final Queue<Long> workQueue = new LinkedList<>();

        //find root of organization tree
        List<OrganizationImport> rootOrgs = organizationImportBean.find(null);
        if (rootOrgs == null || rootOrgs.isEmpty()) {
            throw new RootOrganizationNotFound();
        }

        for (OrganizationImport root : rootOrgs) {
            workQueue.add(root.getOrganizationId());
        }

        while (!workQueue.isEmpty()) {
            recordIndex++;

            final long externalOrganizationId = workQueue.poll();

            //put children in work queue
            List<OrganizationImport> orgs = organizationImportBean.find(externalOrganizationId);
            if (orgs != null && !orgs.isEmpty()) {
                for (OrganizationImport oi : orgs) {
                    workQueue.add(oi.getOrganizationId());
                }
            }

            //handle organization
            final OrganizationImport organization = organizationImportBean.findById(externalOrganizationId);

            // Ищем по organization id в базе.
            DomainObject newObject = null;
            DomainObject oldObject = null;

            final Long objectId = organizationStrategy.getObjectId(externalOrganizationId);
            if (objectId != null) {
                oldObject = organizationStrategy.findById(objectId, true);
                if (oldObject != null) { // нашли
                    newObject = CloneUtil.cloneObject(oldObject);
                }
            }
            if (newObject == null) {
                newObject = organizationStrategy.newInstance();
                newObject.setExternalId(externalOrganizationId);
            }

            //code
            AttributeUtil.setStringValue(newObject.getAttribute(IKeConnectionOrganizationStrategy.CODE),
                    organization.getCode().toUpperCase(), localeId);
            if (AttributeUtil.getSystemStringCultureValue(newObject.getAttribute(IKeConnectionOrganizationStrategy.CODE)) == null) {
                AttributeUtil.setStringValue(newObject.getAttribute(IKeConnectionOrganizationStrategy.CODE),
                        organization.getCode().toUpperCase(), systemLocaleId);
            }

            //short name
            AttributeUtil.setStringValue(newObject.getAttribute(IKeConnectionOrganizationStrategy.SHORT_NAME),
                    organization.getShortName().toUpperCase(), localeId);
            if (AttributeUtil.getSystemStringCultureValue(newObject.getAttribute(IKeConnectionOrganizationStrategy.SHORT_NAME)) == null) {
                AttributeUtil.setStringValue(newObject.getAttribute(IKeConnectionOrganizationStrategy.SHORT_NAME),
                        organization.getShortName().toUpperCase(), systemLocaleId);
            }

            //full name
            AttributeUtil.setStringValue(newObject.getAttribute(IKeConnectionOrganizationStrategy.NAME),
                    organization.getFullName().toUpperCase(), localeId);
            if (AttributeUtil.getSystemStringCultureValue(newObject.getAttribute(IKeConnectionOrganizationStrategy.NAME)) == null) {
                AttributeUtil.setStringValue(newObject.getAttribute(IKeConnectionOrganizationStrategy.NAME),
                        organization.getFullName().toUpperCase(), systemLocaleId);
            }

            //parent
            Long parentId = organization.getHlevel();
            if (parentId != null) {
                long parentObjectId = organizationStrategy.getObjectId(parentId);
                newObject.getAttribute(IKeConnectionOrganizationStrategy.USER_ORGANIZATION_PARENT).
                        setValueId(parentObjectId);
            }

            //type
            addOrganizationTypes(newObject);

            //Readiness to close operating month. Only for servicing organizations.
            addReadyCloseOperatingMonthFlag(newObject, systemLocaleId);

            final Date currentDate = DateUtil.getCurrentDate();
            if (oldObject == null) {
                organizationStrategy.insert(newObject, currentDate);
            } else {
                organizationStrategy.update(oldObject, newObject, currentDate);
            }

            //add operating month entry if necessary. Only for servicing organizations.
            addOperatingMonth(newObject.getId(), beginOm);

            listener.recordProcessed(ORGANIZATION, recordIndex);
        }

        listener.completeImport(ORGANIZATION, recordIndex);
    }

    private void addOrganizationTypes(DomainObject organization) {
        organization.removeAttribute(IKeConnectionOrganizationStrategy.ORGANIZATION_TYPE);
        organization.addAttribute(newOrganizationTypeAttribute(1L, KeConnectionOrganizationTypeStrategy.SERVICING_ORGANIZATION));
        organization.addAttribute(newOrganizationTypeAttribute(2L, KeConnectionOrganizationTypeStrategy.USER_ORGANIZATION_TYPE));
    }

    private Attribute newOrganizationTypeAttribute(long attributeId, long organizationTypeId) {
        Attribute a = new Attribute();
        a.setAttributeId(attributeId);
        a.setAttributeTypeId(IKeConnectionOrganizationStrategy.ORGANIZATION_TYPE);
        a.setValueTypeId(IKeConnectionOrganizationStrategy.ORGANIZATION_TYPE);
        a.setValueId(organizationTypeId);
        return a;
    }

    private void addReadyCloseOperatingMonthFlag(DomainObject organization, long systemLocaleId) {
        final Attribute attribute = organization.getAttribute(IKeConnectionOrganizationStrategy.READY_CLOSE_OPER_MONTH);
        String value = AttributeUtil.getSystemStringCultureValue(attribute);
        if (Strings.isNullOrEmpty(value)) {
            value = new BooleanConverter().toString(Boolean.FALSE);
            AttributeUtil.setStringValue(attribute, value, systemLocaleId);
        }
    }

    private void addOperatingMonth(long organizationId, Date currentDate) {
        if (!organizationImportBean.operatingMonthExists(organizationId)) {
            organizationImportBean.insertOperatingMonth(organizationId, currentDate);
        }
    }
}
