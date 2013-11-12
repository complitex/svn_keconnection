package org.complitex.keconnection.heatmeter.entity;

import org.complitex.dictionary.entity.IConfig;

import static org.complitex.dictionary.entity.DictionaryConfig.IMPORT_FILE_STORAGE_DIR;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 26.09.12 18:31
 */
public enum HeatmeterConfig implements IConfig{
    IMPORT_HEATMETER_DIR(IMPORT_FILE_STORAGE_DIR.getDefaultValue() + "\\heatmeter"),
    IMPORT_PAYLOAD_DIR(IMPORT_FILE_STORAGE_DIR.getDefaultValue() + "\\payload");

    private String defaultValue;

    private HeatmeterConfig(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getGroupKey() {
        return "heatmeter";
    }
}
