package org.complitex.keconnection.address.service;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.base.Strings;
import org.complitex.address.entity.AddressImportFile;
import org.complitex.address.service.AddressImportService;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building_address.BuildingAddressStrategy;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.AbstractImportService;
import org.complitex.dictionary.service.IImportListener;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.exception.ImportDuplicateException;
import org.complitex.dictionary.service.exception.ImportFileNotFoundException;
import org.complitex.dictionary.service.exception.ImportFileReadException;
import org.complitex.dictionary.service.exception.ImportObjectLinkException;
import org.complitex.dictionary.util.*;
import org.complitex.keconnection.address.entity.BuildingImport;
import org.complitex.keconnection.address.entity.BuildingPartImport;
import org.complitex.keconnection.address.strategy.building.KeConnectionBuildingStrategy;
import org.complitex.keconnection.address.strategy.building.entity.BuildingCode;
import org.complitex.keconnection.address.strategy.building.entity.KeConnectionBuilding;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.complitex.address.entity.AddressImportFile.BUILDING;
import static org.complitex.address.entity.AddressImportFile.STREET;
import static org.complitex.dictionary.util.StringUtil.removeWhiteSpaces;
import static org.complitex.dictionary.util.StringUtil.toCyrillic;

/**
 *
 * @author Artem
 */
@Stateless
public class KeConnectionAddressImportService extends AbstractImportService {
    private static final Logger log = LoggerFactory.getLogger(KeConnectionAddressImportService.class);
    private static final String RESOURCE_BUNDLE = KeConnectionAddressImportService.class.getName();

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

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    @EJB
    private KeConnectionBuildingImportBean buildingImportBean;

    public void process(AddressImportFile addressFile, IImportListener listener, long localeId, Date beginDate)
            throws ImportFileNotFoundException, ImportFileReadException, ImportObjectLinkException, ImportDuplicateException {
        switch (addressFile) {
            case COUNTRY:
                addressImportService.importCountry(listener, localeId, beginDate);
                break;
            case REGION:
                addressImportService.importRegion(listener, localeId, beginDate);
                break;
            case CITY_TYPE:
                addressImportService.importCityType(listener, localeId, beginDate);
                break;
            case CITY:
                addressImportService.importCity(listener, localeId, beginDate);
                break;
            case DISTRICT:
                addressImportService.importDistrict(listener, localeId, beginDate);
                break;
            case STREET_TYPE:
                addressImportService.importStreetType(listener, localeId, beginDate);
                break;
            case STREET:
                importStreet(listener, localeId, beginDate);
                break;
            case BUILDING:
                importBuilding(listener, localeId, beginDate);
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
    private void importBuilding(IImportListener listener, long localeId, Date beginDate) throws ImportFileNotFoundException,
            ImportFileReadException, ImportObjectLinkException {

        buildingImportBean.delete();

        listener.beginImport(BUILDING, getRecordCount(BUILDING));

        CSVReader reader = getCsvReader(BUILDING);

        int recordIndex = 0;

        try {
            String[] line;
            while ((line = reader.readNext()) != null) {
                recordIndex++;
                final long buildingPartId = Long.parseLong(line[0].trim());
                final long distrId = Long.parseLong(line[1].trim());
                final long streetId = Long.parseLong(line[2].trim());
                final String num = line[3].trim();
                final String part = line[4].trim();
                final long gek = Long.parseLong(line[5].trim());
                final String code = line[6].trim();
                buildingImportBean.saveOrUpdate(buildingPartId, distrId, streetId, num, part, gek, code);
            }
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, BUILDING.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }

        final long systemLocaleId = localeBean.getSystemLocaleObject().getId();
        recordIndex = 0;
        final int batch = 100;

        List<BuildingImport> imports;
        while ((imports = buildingImportBean.find(batch)) != null && !imports.isEmpty()) {
            for (BuildingImport b : imports) {
                recordIndex++;

                final long streetExternalId = b.getStreetId();

                Long streetId = streetStrategy.getObjectId(streetExternalId);
                if (streetId == null) {
                    listener.warn(BUILDING, ResourceUtil.getFormatString(RESOURCE_BUNDLE, "building_street_not_found_warn",
                            localeBean.getLocale(localeId), b.getNum(), b.getBuildingPartId(), streetExternalId));
                    continue;
                }

                KeConnectionBuilding building = buildingStrategy.newInstance();

                //DISTRICT_ID
                final long districtExternalId = b.getDistrId();
                Long districtId = districtStrategy.getObjectId(districtExternalId);
                if (districtId == null) {
                    throw new ImportObjectLinkException(BUILDING.getFileName(), recordIndex, String.valueOf(b.getDistrId()));
                }
                building.getAttribute(BuildingStrategy.DISTRICT).setValueId(districtId);

                DomainObject buildingAddress = building.getPrimaryAddress();

                //STREET_ID
                buildingAddress.setParentEntityId(BuildingAddressStrategy.PARENT_STREET_ENTITY_ID);
                buildingAddress.setParentId(streetId);


                //Номер дома
                final String number = prepareBuildingNumber(recordIndex, b.getNum());
                final Attribute numberAttribute = buildingAddress.getAttribute(BuildingAddressStrategy.NUMBER);
                AttributeUtil.setStringValue(numberAttribute, number, localeId);
                if (AttributeUtil.getSystemStringCultureValue(numberAttribute) == null) {
                    AttributeUtil.setStringValue(numberAttribute, number, systemLocaleId);
                }

                //Корпус дома
                final String corp = prepareBuildingCorp(b.getPart());
                if (corp != null) {
                    final Attribute corpAttribute = buildingAddress.getAttribute(BuildingAddressStrategy.CORP);
                    AttributeUtil.setStringValue(corpAttribute, corp, localeId);
                    if (AttributeUtil.getSystemStringCultureValue(corpAttribute) == null) {
                        AttributeUtil.setStringValue(corpAttribute, corp, systemLocaleId);
                    }
                }

                //Обработка пар обсл. организация - код дома
                {
                    Set<Long> subjectIds = new HashSet<>();

                    for (BuildingPartImport part : b.getBuildingParts()) {
                        final long gekId = part.getGek();
                        final String buildingCode = part.getCode();

                        final Long organizationId = organizationStrategy.getObjectId(gekId);
                        if (organizationId == null) {
                            throw new ImportObjectLinkException(BUILDING.getFileName(), recordIndex, String.valueOf(gekId));
                        }

                        Integer buildingCodeInt = null;
                        try {
                            buildingCodeInt = StringUtil.parseInt(buildingCode);
                        } catch (NumberFormatException e) {
                        }

                        if (buildingCodeInt == null) {
                            listener.warn(BUILDING, ResourceUtil.getFormatString(RESOURCE_BUNDLE, "building_code_format_warn",
                                    localeBean.getLocale(localeId), b.getNum(), b.getBuildingPartId(), buildingCode));
                            continue;
                        } else {
                            BuildingCode association = new BuildingCode();
                            association.setOrganizationId(organizationId);
                            association.setBuildingCode(buildingCodeInt);
                            building.getBuildingCodeList().add(association);
                            subjectIds.add(organizationId);
                        }
                    }
                    building.setSubjectIds(subjectIds);
                }
                buildingStrategy.insert(building, beginDate);
                listener.recordProcessed(BUILDING, recordIndex);
            }
            buildingImportBean.markProcessed(imports);
        }

        listener.completeImport(BUILDING, recordIndex);
    }

    /**
     * STREET_ID	CITY_ID	STREET_TYPE_ID	Название улицы
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void importStreet(IImportListener listener, long localeId, Date beginDate)
            throws ImportFileNotFoundException, ImportFileReadException, ImportObjectLinkException, ImportDuplicateException {
        listener.beginImport(STREET, getRecordCount(STREET));

        CSVReader reader = getCsvReader(STREET);

        final long systemLocaleId = localeBean.getSystemLocaleObject().getId();
        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                final String externalId = line[0].trim();

                DomainObject newObject = null;
                DomainObject oldObject = null;

                // Ищем по externalId в базе.
                Long objectId = streetStrategy.getObjectId(Long.valueOf(externalId));
                if (objectId != null) {
                    oldObject = streetStrategy.findById(objectId, true);
                    if (oldObject != null) {
                        newObject = CloneUtil.cloneObject(oldObject);
                    }
                }
                if (newObject == null) {
                    newObject = streetStrategy.newInstance();
                    newObject.setExternalId(externalId);
                }

                //name
                final String name = line[3].trim().toUpperCase();
                AttributeUtil.setStringValue(newObject.getAttribute(StreetStrategy.NAME), name, localeId);
                if (AttributeUtil.getSystemStringCultureValue(newObject.getAttribute(StreetStrategy.NAME)) == null) {
                    AttributeUtil.setStringValue(newObject.getAttribute(StreetStrategy.NAME), name, systemLocaleId);
                }

                //CITY_ID
                Long cityId = cityStrategy.getObjectId(Long.parseLong(line[1].trim()));
                if (cityId == null) {
                    throw new ImportObjectLinkException(STREET.getFileName(), recordIndex, line[1]);
                }
                newObject.setParentEntityId(StreetStrategy.PARENT_ENTITY_ID);
                newObject.setParentId(cityId);

                //STREET_TYPE_ID
                Long streetTypeId = streetTypeStrategy.getObjectId(Long.parseLong(line[2].trim()));
                if (streetTypeId == null) {
                    throw new ImportObjectLinkException(STREET.getFileName(), recordIndex, line[2]);
                }
                newObject.getAttribute(StreetStrategy.STREET_TYPE).setValueId(streetTypeId);

                // сначала ищем улицу в системе с таким названием, типом и родителем(городом)
                final Long existingStreetId = streetStrategy.performDefaultValidation(newObject, localeBean.getSystemLocale());
                if (existingStreetId != null) {
                    // нашли дубликат
                    DomainObject existingStreet = streetStrategy.findById(existingStreetId, true);
                    String existingStreetExternalId = existingStreet.getExternalId();
                    listener.warn(STREET, ResourceUtil.getFormatString(RESOURCE_BUNDLE, "street_duplicate_warn",
                            localeBean.getLocale(localeId),
                            line[3], externalId, existingStreetId, existingStreetExternalId));
                } else {
                    if (oldObject == null) {
                        streetStrategy.insert(newObject, beginDate);
                    } else {
                        streetStrategy.update(oldObject, newObject, beginDate);
                    }
                    listener.recordProcessed(STREET, recordIndex);
                }
            }

            listener.completeImport(STREET, recordIndex);
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, STREET.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
