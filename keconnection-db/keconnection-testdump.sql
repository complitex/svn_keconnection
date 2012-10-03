-- Set mysql user-defined variable - system locale id.
SELECT (@system_locale_id := `id`) FROM `locales` WHERE `system` = 1;

-- Servicing organizations
insert into organization(object_id) values (10),(11);
insert into organization_string_culture(id, locale_id, `value`) values 
(10,1,UPPER('Обсл. организация №1')), (10,2,UPPER('Обсл. организация №1')), (11,@system_locale_id, UPPER('10')),
(12,1,UPPER('Обсл. организация №2')), (12,2,UPPER('Обсл. организация №2')), (13,@system_locale_id, UPPER('11'));
insert into organization_attribute(attribute_id, object_id, attribute_type_id, value_id, value_type_id) values
(1,10,900,10,900), (1,10,901,11,901), (1,10,904,4,904),
(1,11,900,12,900), (1,11,901,13,901), (1,11,904,4,904);

-- Service provider and calculation module organizations
insert into organization(object_id) values (20),(21);
insert into organization_string_culture(id, locale_id, value) values 
(20, 1, UPPER('Service provider #1')), (20,2,UPPER('Service provider #1')), (21,@system_locale_id, UPPER('20')),
(22, 1, UPPER('Calculation module #1')), (22, 2, UPPER('Calculation module #1')), (23,@system_locale_id, UPPER('21'));
insert into organization_attribute(attribute_id, object_id, attribute_type_id, value_id, value_type_id) values
-- service providers:
(1,20,900,20,900), (1,20,901,21,901), (1,20,902,3,902), (1,20,904,5,904),
-- calculation modules:
(1,21,900,22,900), (1,21,901,23,901), (1,21,904,2,904);

-- User organizations
insert into organization(object_id) values (30), (31);
insert into organization_string_culture(id, locale_id, value) values 
(30,@system_locale_id, UPPER('User organization #1')),(31,@system_locale_id, UPPER('30')),
(32,@system_locale_id,UPPER('User organization #2')),(33,@system_locale_id, UPPER('31'));
insert into organization_attribute(attribute_id, object_id, attribute_type_id, value_id, value_type_id) values
(1,30,900,30,900), (1,30,901,31,901), (1,30,904,1,904),
(1,31,900,32,900), (1,31,901,33,901), (1,31,903,30,903), (1,31,904,1,904);

-- Address corrections
-- Calculation module's corrections
INSERT INTO `street_type_correction`(`id`, `object_id`, `correction`, `organization_id`, `organization_code`, `internal_organization_id`) VALUES
(1,10000,UPPER('Б-Р'),21,'1',0), (2,10001,UPPER('М'),21,'1',0), (3,10002,UPPER('М-Н'),21,'1',0), (4,10003,UPPER('ПЕР'),21,'1',0), (5,10004,UPPER('ПЛ'),21,'1',0), (6,10005,UPPER('П'),21,'1',0),
(7,10006,UPPER('ПОС'),21,'1',0), (8,10007,UPPER('ПР-Д'),21,'1',0), (9,10008,UPPER('ПРОСП'),21,'1',0), (10,10009,UPPER('СП'),21,'1',0), (11,10010,UPPER('Т'),21,'1',0), (12,10011,UPPER('ТУП'),21,'1',0),
(13,10012,UPPER('УЛ'),21,'1',0), (14,10013,UPPER('ШОССЕ'),21,'1',0), (15,10014,UPPER('НАБ'),21,'1',0), (16,10015,UPPER('В-Д'),21,'1',0), (17,10016,UPPER('СТ'),21,'1',0);

insert into city_correction(id, organization_id, correction, object_id, internal_organization_id) values (1,21,UPPER('Новосибирск'),1,0);
insert into street_correction(id, organization_id, correction, object_id, internal_organization_id, parent_id, street_type_correction_id) values (1,21,UPPER('Терешковой'),1,0,1,13);
insert into building_correction(organization_id, correction, correction_corp, object_id, internal_organization_id, parent_id) values (21,UPPER('10'),UPPER('1'),3,0,1);

insert into city_correction(id, organization_id, correction, object_id, internal_organization_id) values (2,21,UPPER('Харьков'),3,0);
insert into street_correction(id, organization_id, correction, object_id, internal_organization_id, parent_id, street_type_correction_id) values (2,21,UPPER('Косиора'),4,0,2,13);
insert into building_correction(organization_id, correction, correction_corp, object_id, internal_organization_id, parent_id) values (21,UPPER('154А'),UPPER(''),6,0,2);

insert into street_correction(id, organization_id, correction, object_id, internal_organization_id, parent_id, street_type_correction_id) 
    values (3,21,UPPER('ФРАНТИШЕКА КРАЛА'),5,0,2,13);
insert into building_correction(organization_id, correction, correction_corp, object_id, internal_organization_id, parent_id) values (21,UPPER('25А'),UPPER(''),7,0,3);
insert into street_correction(id, organization_id, correction, object_id, internal_organization_id, parent_id, street_type_correction_id) 
    values (4,21,UPPER('ФРАНТИШЕКА КРАЛА'),5,0,2,13);
insert into building_correction(organization_id, correction, correction_corp, object_id, internal_organization_id, parent_id) values (21,UPPER('23'),UPPER(''),8,0,4);

insert into district_correction(organization_id, correction, object_id, internal_organization_id, parent_id) values (21,UPPER('Центральный'),3,0,2);

-- config
insert into config(`name`, `value`) values ('IMPORT_FILE_STORAGE_DIR', 'D:\\Artem\\Projects\\org.complitex\\storage\\keconnection_import\\kiev');