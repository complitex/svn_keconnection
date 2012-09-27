package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.IImportFile;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.09.12 18:30
 */
public class PayloadImportFile implements IImportFile{
    private String fileName;

    public PayloadImportFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String name() {
        return fileName;
    }
}
