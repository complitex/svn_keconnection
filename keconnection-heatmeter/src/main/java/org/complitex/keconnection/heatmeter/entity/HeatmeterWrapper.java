package org.complitex.keconnection.heatmeter.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.09.12 16:08
 */
public class HeatmeterWrapper {
    private int row;
    private String organizationCode;
    private String buildingCode;
    private String ls;
    private Heatmeter heatmeter;

    public HeatmeterWrapper(int row, String organizationCode, String buildingCode, String ls) {
        this.row = row;
        this.organizationCode = organizationCode;
        this.buildingCode = buildingCode;
        this.ls = ls;

        heatmeter = new Heatmeter();
        heatmeter.setLs(Integer.parseInt(ls));
    }

    @Override
    public String toString() {
        return  "row=" + row +
                ", organizationCode='" + organizationCode + '\'' +
                ", buildingCode='" + buildingCode + '\'' +
                ", ls='" + ls + '\'';
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getLs() {
        return ls;
    }

    public void setLs(String ls) {
        this.ls = ls;
    }

    public Heatmeter getHeatmeter() {
        return heatmeter;
    }

    public void setHeatmeter(Heatmeter heatmeter) {
        this.heatmeter = heatmeter;
    }
}
