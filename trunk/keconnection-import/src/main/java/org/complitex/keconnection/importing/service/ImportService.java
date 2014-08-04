package org.complitex.keconnection.importing.service;

import org.complitex.address.entity.AddressImportFile;
import org.complitex.address.service.AddressImportService;
import org.complitex.dictionary.entity.DictionaryConfig;
import org.complitex.dictionary.entity.IImportFile;
import org.complitex.dictionary.entity.ImportMessage;
import org.complitex.dictionary.entity.Log;
import org.complitex.dictionary.service.ConfigBean;
import org.complitex.dictionary.service.IImportListener;
import org.complitex.dictionary.service.LogBean;
import org.complitex.dictionary.service.exception.AbstractException;
import org.complitex.dictionary.service.exception.ImportCriticalException;
import org.complitex.keconnection.heatmeter.entity.HeatmeterImportFile;
import org.complitex.keconnection.heatmeter.entity.PayloadImportFile;
import org.complitex.keconnection.heatmeter.service.HeatmeterImportService;
import org.complitex.keconnection.heatmeter.service.TablegramImportService;
import org.complitex.keconnection.importing.Module;
import org.complitex.keconnection.organization.entity.OrganizationImportFile;
import org.complitex.keconnection.organization.service.OrganizationImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.transaction.UserTransaction;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Artem
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportService {
    private final Logger log = LoggerFactory.getLogger(ImportService.class);

    @Resource
    private UserTransaction userTransaction;

    @EJB
    private ConfigBean configBean;

    @EJB
    private LogBean logBean;

    @EJB
    private OrganizationImportService organizationImportService;

    @EJB
    private AddressImportService addressImportService;

    @EJB
    private HeatmeterImportService heatmeterImportService;

    @EJB
    private TablegramImportService tablegramImportService;

    private volatile boolean processing;
    private volatile boolean error;
    private volatile boolean success;
    private volatile String errorMessage;
    private final Map<IImportFile, ImportMessage> messageMap =
            Collections.synchronizedMap(new LinkedHashMap<IImportFile, ImportMessage>());
    private final Queue<String> warnings = new ConcurrentLinkedQueue<>();

    private class ImportListener implements IImportListener {

        private final Locale locale;

        ImportListener(Locale locale) {
            this.locale = locale;
        }

        @Override
        public void beginImport(IImportFile importFile, int recordCount) {
            messageMap.put(importFile, new ImportMessage(importFile, recordCount, 0));
        }

        @Override
        public void recordProcessed(IImportFile importFile, int recordIndex) {
            messageMap.get(importFile).setIndex(recordIndex);
            messageMap.get(importFile).incProcessed();
        }

        @Override
        public void completeImport(IImportFile importFile, int recordCount) {
            messageMap.get(importFile).setCompleted();
            logBean.info(Module.NAME, ImportService.class, importFile.getClass(), null, Log.EVENT.CREATE,
                    "Имя файла: {0}, количество записей: {1}, Идентификатор локали: {2}",
                    importFile.getFileName(), recordCount, locale);
        }

        @Override
        public void warn(IImportFile importFile, String message) {
            //update warnings in UI
            warnings.add(message);

            //update warnings in logs
            if (importFile == AddressImportFile.STREET) {
                String warning = MessageFormat.format("Предупреждение при импорте файла улиц {0}: {1}",
                        AddressImportFile.STREET.getFileName(), message);
                log.warn(warning);
                logBean.warn(Module.NAME, ImportService.class, AddressImportFile.class, null, Log.EVENT.CREATE, warning);
            } else if (importFile == AddressImportFile.BUILDING) {
                String warning = MessageFormat.format("Предупреждение при импорте файла домов {0}: {1}",
                        AddressImportFile.BUILDING.getFileName(), message);
                log.warn(warning);
                logBean.warn(Module.NAME, ImportService.class, AddressImportFile.class, null, Log.EVENT.CREATE, warning);
            }
        }
    }

    private static class ImportFileComparator implements Comparator<IImportFile> {

        @Override
        public int compare(IImportFile o1, IImportFile o2) {
            if (o1 instanceof OrganizationImportFile) {
                return -1;
            } else if (o2 instanceof OrganizationImportFile) {
                return 1;
            } else if (o1 instanceof Enum && o2 instanceof Enum){
                final int ord1 = ((Enum) o1).ordinal();
                final int ord2 = ((Enum) o2).ordinal();
                return ord1 < ord2 ? -1 : (ord1 > ord2 ? 1 : 0);
            }

            return 0;
        }
    }

    public String getNextWarning() {
        return warnings.poll();
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
        warnings.clear();
        processing = true;
        error = false;
        success = false;
        errorMessage = null;
    }

    @Asynchronous
    public <T extends IImportFile> void process(List<T> importFiles, Locale locale, Date beginOm, Date beginDate) {
        if (processing) {
            return;
        }

        init();
        configBean.getString(DictionaryConfig.IMPORT_FILE_STORAGE_DIR, true); //reload config cache

        final ImportListener listener = new ImportListener(locale);

        //sort import files in right order
//        SortedSet<T> sortedImportFiles = new TreeSet<>(new ImportFileComparator());
//        sortedImportFiles.addAll(importFiles);

        try {
            for (T importFile : importFiles) {
                userTransaction.begin();

                if (importFile instanceof OrganizationImportFile) { //import organizations
                    organizationImportService.process(listener, locale, beginOm, beginDate);
                } else if (importFile instanceof AddressImportFile) { //import addresses
                    addressImportService.process((AddressImportFile) importFile, listener, locale, beginDate);
                } else if (importFile instanceof HeatmeterImportFile){ //import heatmeter
                    heatmeterImportService.process(importFile, listener, beginOm, beginDate);
                } else if (importFile instanceof PayloadImportFile){ //import payload
                    tablegramImportService.process(importFile, listener, beginDate);
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

            success = false;
            error = true;
            errorMessage = e instanceof AbstractException ? e.getMessage() : new ImportCriticalException(e).getMessage();

            logBean.error(Module.NAME, ImportService.class, null, null, Log.EVENT.CREATE, errorMessage);
        } finally {
            processing = false;
        }
    }
}
