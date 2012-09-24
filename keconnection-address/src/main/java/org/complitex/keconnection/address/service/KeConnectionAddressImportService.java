/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.address.service;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Strings;
import org.complitex.address.entity.AddressImportFile;
import org.complitex.address.service.AddressImportService;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building_address.BuildingAddressStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.AbstractImportService;
import org.complitex.dictionary.service.IImportListener;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.exception.ImportDuplicateException;
import org.complitex.dictionary.service.exception.ImportFileNotFoundException;
import org.complitex.dictionary.service.exception.ImportFileReadException;
import org.complitex.dictionary.service.exception.ImportObjectLinkException;
import org.complitex.dictionary.util.AttributeUtil;
import org.complitex.dictionary.util.BuildingNumberConverter;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.keconnection.address.strategy.building.KeConnectionBuildingStrategy;
import org.complitex.keconnection.address.strategy.building.entity.BuildingOrganizationAssociation;
import org.complitex.keconnection.address.strategy.building.entity.KeConnectionBuilding;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;

import static org.complitex.address.entity.AddressImportFile.BUILDING;
import static org.complitex.dictionary.util.StringUtil.removeWhiteSpaces;
import static org.complitex.dictionary.util.StringUtil.toCyrillic;

/**
 *
 * @author Artem
 */
@Stateless
public class KeConnectionAddressImportService extends AbstractImportService {

    private static final Logger log = LoggerFactory.getLogger(KeConnectionAddressImportService.class);
    @EJB
    private AddressImportService addressImportService;
    @EJB
    private LocaleBean localeBean;
    @EJB
    private DistrictStrategy districtStrategy;
    @EJB
    private StreetStrategy streetStrategy;
    @EJB
    private KeConnectionBuildingStrategy buildingStrategy;
    @EJB(name = IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME)
    private IKeConnectionOrganizationStrategy organizationStrategy;

    public void process(AddressImportFile addressFile, IImportListener listener, long localeId)
            throws ImportFileNotFoundException, ImportFileReadException, ImportObjectLinkException, ImportDuplicateException {
        switch (addressFile) {
            case COUNTRY:
                addressImportService.importCountry(listener, localeId);
                break;
            case REGION:
                addressImportService.importRegion(listener, localeId);
                break;
            case CITY_TYPE:
                addressImportService.importCityType(listener, localeId);
                break;
            case CITY:
                addressImportService.importCity(listener, localeId);
                break;
            case DISTRICT:
                addressImportService.importDistrict(listener, localeId);
                break;
            case STREET_TYPE:
                addressImportService.importStreetType(listener, localeId);
                break;
            case STREET:
                addressImportService.importStreet(listener, localeId);
                break;
            case BUILDING:
                importBuilding(listener, localeId);
                break;
        }
    }

    private String prepareBuildingNumber(long rowNumber, String importNumber) {
        if (importNumber == null) {
            throw new NullPointerException("Imported number is null. Row: " + rowNumber);
        }
        return BuildingNumberConverter.convert(importNumber.trim()).toUpperCase();
    }

    private String prepareBuildingCorp(String importCorp) {
        if (Strings.isNullOrEmpty(importCorp)) {
            return null;
        }
        return removeWhiteSpaces(toCyrillic(importCorp.trim())).toUpperCase();
    }

    /**
     * ID DISTR_ID STREET_ID NUM PART GEK CODE
     */
    private void importBuilding(IImportListener listener, long localeId) throws ImportFileNotFoundException,
            ImportFileReadException, ImportObjectLinkException {
        listener.beginImport(BUILDING, getRecordCount(BUILDING));

        CSVReader reader = getCsvReader(BUILDING);

        final long systemLocaleId = localeBean.getSystemLocaleObject().getId();
        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                final long streetExternalId = Long.parseLong(line[2].trim());

                Long streetId = streetStrategy.getObjectId(streetExternalId);
                if (streetId == null) {
                    throw new ImportObjectLinkException(BUILDING.getFileName(), recordIndex, line[2]);
                }

                final String number = prepareBuildingNumber(recordIndex, line[3]);
                final String corp = prepareBuildingCorp(line[4]);

                final Long existingBuildingId = buildingStrategy.checkForExistingAddress(null,
                        number, corp, null, BuildingAddressStrategy.PARENT_STREET_ENTITY_ID, streetId,
                        localeBean.getSystemLocale());

                KeConnectionBuilding building = null;

                if (existingBuildingId != null) {
                    //дом уже существует
                    building = buildingStrategy.findById(existingBuildingId, true);
                } else {
                    //такого дома еще нет
                    building = buildingStrategy.newInstance();

                    //DISTRICT_ID
                    final long districtExternalId = Long.parseLong(line[1].trim());
                    Long districtId = districtStrategy.getObjectId(districtExternalId);
                    if (districtId == null) {
                        throw new ImportObjectLinkException(BUILDING.getFileName(), recordIndex, line[1]);
                    }
                    building.getAttribute(BuildingStrategy.DISTRICT).setValueId(districtId);

                    DomainObject buildingAddress = building.getPrimaryAddress();

                    //STREET_ID
                    buildingAddress.setParentEntityId(BuildingAddressStrategy.PARENT_STREET_ENTITY_ID);
                    buildingAddress.setParentId(streetId);

                    //Номер дома
                    final Attribute numberAttribute = buildingAddress.getAttribute(BuildingAddressStrategy.NUMBER);
                    AttributeUtil.setStringValue(numberAttribute, number, localeId);
                    if (AttributeUtil.getSystemStringCultureValue(numberAttribute) == null) {
                        AttributeUtil.setStringValue(numberAttribute, number, systemLocaleId);
                    }

                    //Корпус
                    if (corp != null) {
                        final Attribute corpAttribute = buildingAddress.getAttribute(BuildingAddressStrategy.CORP);
                        AttributeUtil.setStringValue(corpAttribute, corp, localeId);
                        if (AttributeUtil.getSystemStringCultureValue(corpAttribute) == null) {
                            AttributeUtil.setStringValue(corpAttribute, corp, systemLocaleId);
                        }
                    }
                }

                //Обработка пар обсл. организация - код дома
                {
                    final long gekId = Long.parseLong(line[5].trim());
                    final String buildingCode = line[6].trim();

                    final Long organizationId = organizationStrategy.getObjectId(gekId);
                    if (organizationId == null) {
                        throw new ImportObjectLinkException(BUILDING.getFileName(), recordIndex, line[5]);
                    }

                    BuildingOrganizationAssociation association = new BuildingOrganizationAssociation();
                    association.setOrganizationId(organizationId);
                    association.setBuildingCode(buildingCode);

                    //добавлен код дома
                    if (existingBuildingId == null) { // новый дом
                        building.getBuildingOrganizationAssociationList().add(association);
                        buildingStrategy.insert(building, DateUtil.getCurrentDate());
                    } else { // существующий дом, но возможно добавлен новый код дома
                        buildingStrategy.addBuildingOrganizationAssociation(building, association);
                    }
                }
                listener.recordProcessed(BUILDING, recordIndex);
            }

            listener.completeImport(BUILDING, recordIndex);
        } catch (IOException e) {
            throw new ImportFileReadException(e, BUILDING.getFileName(), recordIndex);
        } catch (NumberFormatException e) {
            throw new ImportFileReadException(e, BUILDING.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
