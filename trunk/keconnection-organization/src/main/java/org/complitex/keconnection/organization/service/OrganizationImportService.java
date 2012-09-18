/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.service;

import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.util.DateUtil;
import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Strings;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.dictionary.entity.AbstractImportService;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.IImportListener;
import org.complitex.dictionary.service.exception.ImportFileNotFoundException;
import org.complitex.dictionary.service.exception.ImportFileReadException;
import org.complitex.keconnection.organization.enity.OrganizationImport;
import org.complitex.keconnection.organization.service.exception.MoreOneRootOrganizationException;
import org.complitex.keconnection.organization.service.exception.RootOrganizationNotFound;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.complitex.keconnection.organization_type.strategy.KeConnectionOrganizationTypeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.complitex.keconnection.organization.enity.OrganizationImportFile.*;

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

    private void setValue(Attribute attribute, String value, long localeId) {
        for (StringCulture string : attribute.getLocalizedValues()) {
            if (string.getLocaleId().equals(localeId)) {
                string.setValue(value);
            }
        }
    }

    /**
     * ID CODE SHORT_NAME NAME HLEVEL
     */
    public void process(IImportListener listener, long localeId)
            throws ImportFileNotFoundException, ImportFileReadException, RootOrganizationNotFound,
            MoreOneRootOrganizationException {

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

        recordIndex = 0;

        final Queue<Long> workQueue = new LinkedList<>();

        //find root of organization tree
        List<OrganizationImport> rootOrgs = organizationImportBean.find(null);
        if (rootOrgs == null || rootOrgs.isEmpty()) {
            throw new RootOrganizationNotFound();
        }

        if (rootOrgs.size() > 1) {
            final String[] organizationNames = new String[rootOrgs.size()];
            for (int i = 0, length = rootOrgs.size(); i < length; i++) {
                organizationNames[i] = rootOrgs.get(i).getFullName();
            }
            throw new MoreOneRootOrganizationException(organizationNames);
        }

        final long rootOrgId = rootOrgs.get(0).getOrganizationId();
        workQueue.add(rootOrgId);

        //TODO: comment as work around for multiple root organizations case
        /*
        for (OrganizationImport root : rootOrgs) {
        workQueue.add(root.getOrganizationId());
        }*/

        while (!workQueue.isEmpty()) {
            final long organizationId = workQueue.poll();

            //put children in work queue
            List<OrganizationImport> orgs = organizationImportBean.find(organizationId);
            if (orgs != null && !orgs.isEmpty()) {
                for (OrganizationImport oi : orgs) {
                    workQueue.add(oi.getOrganizationId());
                }
            }

            //handle organization
            final OrganizationImport organization = organizationImportBean.findById(organizationId);

            // Ищем по organization id в базе.
            final Long objectId = organizationStrategy.getObjectId(organizationId);
            if (objectId != null) {
                DomainObject oldObject = organizationStrategy.findById(objectId, true);
                if (oldObject != null) { // нашли
                    //TODO: пока ничего не обновляем
                }
            } else {
                // не нашли, создаём объект заполняем его атрибуты и сохраняем.
                DomainObject object = organizationStrategy.newInstance();
                object.setExternalId(organizationId);
                //code
                setValue(object.getAttribute(IKeConnectionOrganizationStrategy.CODE),
                        organization.getCode(), localeId);
                //short name
                setValue(object.getAttribute(IKeConnectionOrganizationStrategy.SHORT_NAME),
                        organization.getShortName(), localeId);
                //full name
                setValue(object.getAttribute(IKeConnectionOrganizationStrategy.NAME),
                        organization.getFullName(), localeId);
                //parent
                Long parentId = organization.getHlevel();
                if (parentId != null) {
                    long parentObjectId = organizationStrategy.getObjectId(parentId);
                    object.getAttribute(IKeConnectionOrganizationStrategy.USER_ORGANIZATION_PARENT).
                            setValueId(parentObjectId);
                }

                //type
                object.getAttribute(IKeConnectionOrganizationStrategy.ORGANIZATION_TYPE).
                        setValueId(KeConnectionOrganizationTypeStrategy.SERVICING_ORGANIZATION);
                organizationStrategy.insert(object, DateUtil.getCurrentDate());
                listener.recordProcessed(ORGANIZATION, recordIndex);
                recordIndex++;
            }
        }

        listener.completeImport(ORGANIZATION, recordIndex);
    }
}
