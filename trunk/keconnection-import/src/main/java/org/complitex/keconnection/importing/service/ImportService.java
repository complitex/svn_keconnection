/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.importing.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;
import org.complitex.address.entity.AddressImportFile;
import org.complitex.dictionary.entity.DictionaryConfig;
import org.complitex.dictionary.entity.IImportFile;
import org.complitex.dictionary.entity.ImportMessage;
import org.complitex.dictionary.entity.Log;
import org.complitex.dictionary.service.ConfigBean;
import org.complitex.dictionary.service.IImportListener;
import org.complitex.dictionary.service.LogBean;
import org.complitex.dictionary.service.exception.AbstractException;
import org.complitex.dictionary.service.exception.ImportCriticalException;
import org.complitex.keconnection.address.service.KeConnectionAddressImportService;
import org.complitex.keconnection.importing.Module;
import org.complitex.keconnection.organization.enity.OrganizationImportFile;
import org.complitex.keconnection.organization.service.OrganizationImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);
    @Resource
    private UserTransaction userTransaction;
    @EJB
    private ConfigBean configBean;
    @EJB
    private LogBean logBean;
    @EJB
    private OrganizationImportService organizationImportService;
    @EJB
    private KeConnectionAddressImportService addressImportService;
    private volatile boolean processing;
    private volatile boolean error;
    private volatile boolean success;
    private volatile String errorMessage;
    private final Map<IImportFile, ImportMessage> messageMap =
            Collections.synchronizedMap(new LinkedHashMap<IImportFile, ImportMessage>());

    private class ImportListener implements IImportListener {

        private final long localeId;

        ImportListener(long localeId) {
            this.localeId = localeId;
        }

        @Override
        public void beginImport(IImportFile importFile, int recordCount) {
            messageMap.put(importFile, new ImportMessage(importFile, recordCount, 0));
        }

        @Override
        public void recordProcessed(IImportFile importFile, int recordIndex) {
            messageMap.get(importFile).setIndex(recordIndex);
        }

        @Override
        public void completeImport(IImportFile importFile, int recordCount) {
            messageMap.get(importFile).setCompleted();
            logBean.info(Module.NAME, ImportService.class, importFile.getClass(), null, Log.EVENT.CREATE,
                    "Имя файла: {0}, количество записей: {1}, Идентификатор локали: {2}",
                    importFile.getFileName(), recordCount, localeId);
        }
    };

    private static class ImportFileComparator implements Comparator<IImportFile> {

        @Override
        public int compare(IImportFile o1, IImportFile o2) {
            if (o1 instanceof OrganizationImportFile) {
                return -1;
            } else if (o2 instanceof OrganizationImportFile) {
                return 1;
            } else {
                final int ord1 = ((Enum) o1).ordinal();
                final int ord2 = ((Enum) o2).ordinal();
                return ord1 < ord2 ? -1 : (ord1 > ord2 ? 1 : 0);
            }
        }
    }

    public boolean isProcessing() {
        return processing;
    }

    public boolean isError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ImportMessage getMessage(IImportFile importFile) {
        return messageMap.get(importFile);
    }

    private void init() {
        messageMap.clear();
        processing = true;
        error = false;
        success = false;
        errorMessage = null;
    }

    @Asynchronous
    public <T extends IImportFile> void process(Set<T> importFiles, long localeId) {
        if (processing) {
            return;
        }

        init();
        configBean.getString(DictionaryConfig.IMPORT_FILE_STORAGE_DIR, true); //reload config cache

        final ImportListener listener = new ImportListener(localeId);

        //sort import files in right order
        SortedSet<T> sortedImportFiles = new TreeSet<>(new ImportFileComparator());
        sortedImportFiles.addAll(importFiles);

        try {
            for (T importFile : sortedImportFiles) {
                userTransaction.begin();

                if (importFile instanceof OrganizationImportFile) {
                    //import organizations
                    organizationImportService.process(listener, localeId);
                } else if (importFile instanceof AddressImportFile) {
                    addressImportService.process((AddressImportFile) importFile, listener, localeId);
                }

                userTransaction.commit();
            }
            success = true;
        } catch (Exception e) {
            log.error("Import error.", e);

            try {
                userTransaction.rollback();
            } catch (Exception e1) {
                log.error("Couldn't rollback transaction.", e1);
            }

            error = true;
            errorMessage = e instanceof AbstractException ? e.getMessage() : new ImportCriticalException(e).getMessage();

            logBean.error(Module.NAME, ImportService.class, null, null, Log.EVENT.CREATE, errorMessage);
        } finally {
            processing = false;
        }
    }
}
