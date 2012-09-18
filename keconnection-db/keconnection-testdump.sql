-- Set mysql user-defined variable - system locale id.
SELECT (@system_locale_id := `id`) FROM `locales` WHERE `system` = 1;

-- Servicing organizations
insert into organization(object_id) values (1),(2);
insert into organization_string_culture(id, locale_id, `value`) values 
(1,1,UPPER('Обсл. организация №1')), (1,2,UPPER('Обсл. организация №1')), (2,@system_locale_id, UPPER('1')),
(3,1,UPPER('Обсл. организация №2')), (3,2,UPPER('Обсл. организация №2')), (4,@system_locale_id, UPPER('2'));
insert into organization_attribute(attribute_id, object_id, attribute_type_id, value_id, value_type_id) values
(1,1,900,1,900), (1,1,901,2,901), (1,1,904,4,904),
(1,2,900,3,900), (1,2,901,4,901), (1,2,904,4,904);

-- config
insert into config(`name`, `value`) values ('IMPORT_FILE_STORAGE_DIR', 'D:\\Artem\\Projects\\org.complitex\\storage\\keconnection_import\\kiev');