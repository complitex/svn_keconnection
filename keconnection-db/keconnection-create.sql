/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- ------------------------------
-- Building organization association
-- ------------------------------

DROP TABLE IF EXISTS `building_code`;
CREATE TABLE `building_code` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'ID обслуживающей организации',
  `code` VARCHAR(10) NOT NULL COMMENT 'Код дома для данной обслуживающей организации',
  `building_id` BIGINT(20) NOT NULL COMMENT 'ID дома',
  PRIMARY KEY (`id`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_building_id` (`building_id`),
  CONSTRAINT `fk_building_code__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_building_code__building` FOREIGN KEY (`building_id`) REFERENCES `building` (`object_id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Код дома';

-- ------------------------------
-- Auxiliary tables for organization import
-- ------------------------------

DROP TABLE IF EXISTS `organization_import`;
CREATE TABLE `organization_import` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `organization_id` BIGINT(20) NOT NULL COMMENT 'ID организации',
  `code` VARCHAR(100) NOT NULL COMMENT 'Код организации',
  `short_name` VARCHAR(100) NOT NULL COMMENT 'Короткое название организации',
  `full_name` VARCHAR(500) NOT NULL COMMENT 'Полное название организации',
  `hlevel` BIGINT(20) COMMENT 'Ссылка на вышестоящую организацию',
  PRIMARY KEY (`pk_id`),
  KEY `key_organization_id` (`organization_id`),
  KEY `key_hlevel` (`hlevel`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Вспомогательная таблица для импорта организаций';

-- ------------------------------
-- Auxiliary tables for building import
-- ------------------------------

DROP TABLE IF EXISTS `building_import`;
CREATE TABLE `building_import` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `distr_id` BIGINT(20) NOT NULL COMMENT 'ID района',
  `street_id` BIGINT(20) NOT NULL COMMENT 'ID улицы',
  `num` VARCHAR(10) NOT NULL COMMENT 'Номер дома',
  `part` VARCHAR(10) NOT NULL COMMENT 'Корпус дома',
  `processed` TINYINT(1) NOT NULL default 0 COMMENT 'Индикатор импорта',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_building_import` (`street_id`, `num`, `part`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Вспомогательная таблица для импорта домов';

DROP TABLE IF EXISTS `building_part_import`;
CREATE TABLE `building_part_import` (
  `id` BIGINT(20) NOT NULL COMMENT 'ID части дома',
  `gek` BIGINT(20) COMMENT 'ID организации',
  `code` VARCHAR(10) COMMENT 'Код дома',
  `building_import_id` BIGINT(20) NOT NULL COMMENT 'Ссылка на building_import запись',
  PRIMARY KEY (`id`),
  KEY `key_building_import_id` (`building_import_id`),
  CONSTRAINT `fk_building_part_import__building_import` FOREIGN KEY (`building_import_id`) REFERENCES `building_import` (`id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Вспомогательная таблица для импорта домов';

-- ------------------------------
-- Tarif group
-- ------------------------------
DROP TABLE IF EXISTS `tarif_group`;

CREATE TABLE `tarif_group` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата завершения периода действия параметров объекта',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус объекта: ACTIVE, INACTIVE или ARCHIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_tarif_group__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_tarif_group__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Тарифная группа';

DROP TABLE IF EXISTS `tarif_group_attribute`;

CREATE TABLE `tarif_group_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута. Возможные значения: 3200 - НАЗВАНИЕ, 3201 - КОД ТАРИФНОЙ ГРУППЫ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `value_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа. Возможные значение: 3200 - STRING_CULTURE, 3201 - STRING',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия параметров атрибута',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус атрибута: ACTIVE, INACTIVE или ARCHIVE',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_tarif_group_attribute__tarif_group` FOREIGN KEY (`object_id`) REFERENCES `tarif_group`(`object_id`),
  CONSTRAINT `fk_tarif_group_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
  REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_tarif_group_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
  REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Атрибуты объекта тарифная группа';

DROP TABLE IF EXISTS `tarif_group_string_culture`;

CREATE TABLE `tarif_group_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор значения',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_tarif_group_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Локализированное значение атрибута тарифной группы';

-- ------------------------------
-- Tarif
-- ------------------------------
DROP TABLE IF EXISTS `tarif`;

CREATE TABLE `tarif` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата завершения периода действия параметров объекта',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус объекта: ACTIVE, INACTIVE или ARCHIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_tarif__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_tarif__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Тарифная группа';

DROP TABLE IF EXISTS `tarif_attribute`;

CREATE TABLE `tarif_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута. Возможные значения: 3300 - НАЗВАНИЕ, 3301 - КОД ТАРИФА, 3302 - ССЫЛКА НА ТАРИФНУЮ ГРУППУ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `value_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа. Возможные значение: 3300 - STRING_CULTURE, 3301 - STRING, 3302 - tarif_group',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия параметров атрибута',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус атрибута: ACTIVE, INACTIVE или ARCHIVE',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_tarif_attribute__tarif` FOREIGN KEY (`object_id`) REFERENCES `tarif`(`object_id`),
  CONSTRAINT `fk_tarif_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
  REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_tarif_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
  REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Атрибуты объекта тариф';

DROP TABLE IF EXISTS `tarif_string_culture`;

CREATE TABLE `tarif_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор значения',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_tarif_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Локализированное значение атрибута тарифа';

-- ------------------------------
-- Heatmeter Type
-- ------------------------------
DROP TABLE IF EXISTS `heatmeter_type`;

CREATE TABLE `heatmeter_type` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата завершения периода действия параметров объекта',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус объекта: ACTIVE, INACTIVE или ARCHIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_heatmeter_type__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_heatmeter_type__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Тип теплосчетчика';

DROP TABLE IF EXISTS `heatmeter_type_attribute`;

CREATE TABLE `heatmeter_type_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута. Возможные значения: 3400 - НАЗВАНИЕ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `value_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа. Возможные значение: 3400 - STRING_CULTURE',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия параметров атрибута',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус атрибута: ACTIVE, INACTIVE или ARCHIVE',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_heatmeter_type_attribute__heatmeter_type` FOREIGN KEY (`object_id`) REFERENCES `heatmeter_type`(`object_id`),
  CONSTRAINT `fk_heatmeter_type_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
  REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_heatmeter_type_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
  REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Атрибуты типа теплосчетчика';

DROP TABLE IF EXISTS `heatmeter_type_string_culture`;

CREATE TABLE `heatmeter_type_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор значения',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_heatmeter_type_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Локализированное значение атрибута типа теплосчетчика';

-- ------------------------------
-- heatmeter Period Type
-- ------------------------------
DROP TABLE IF EXISTS `heatmeter_period_type`;

CREATE TABLE `heatmeter_period_type` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `parent_id` BIGINT(20) COMMENT 'Не используется',
  `parent_entity_id` BIGINT(20) COMMENT 'Не используется',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров объекта',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата завершения периода действия параметров объекта',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус объекта: ACTIVE, INACTIVE или ARCHIVE',
  `permission_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT 'Ключ прав доступа к объекту',
  `external_id` BIGINT(20) COMMENT 'Внешний идентификатор импорта записи',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_object_id__start_date` (`object_id`,`start_date`),
  UNIQUE KEY `unique_external_id` (`external_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_parent_entity_id` (`parent_entity_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  KEY `key_permission_id` (`permission_id`),
  CONSTRAINT `fk_heatmeter_period_type__entity` FOREIGN KEY (`parent_entity_id`) REFERENCES `entity` (`id`),
  CONSTRAINT `fk_heatmeter_period_type__permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Тип периода теплосчетчика';

DROP TABLE IF EXISTS `heatmeter_period_type_attribute`;

CREATE TABLE `heatmeter_period_type_attribute` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `attribute_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор атрибута',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `attribute_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа атрибута. Возможные значения: 3500 - НАЗВАНИЕ',
  `value_id` BIGINT(20) COMMENT 'Идентификатор значения',
  `value_type_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор типа. Возможные значение: 3500 - STRING_CULTURE',
  `start_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата начала периода действия параметров атрибута',
  `end_date` TIMESTAMP NULL DEFAULT NULL COMMENT 'Дата окончания периода действия параметров атрибута',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'Статус атрибута: ACTIVE, INACTIVE или ARCHIVE',
  PRIMARY KEY  (`pk_id`),
  UNIQUE KEY `unique_id` (`attribute_id`,`object_id`,`attribute_type_id`, `start_date`),
  KEY `key_object_id` (`object_id`),
  KEY `key_attribute_type_id` (`attribute_type_id`),
  KEY `key_value_id` (`value_id`),
  KEY `key_value_type_id` (`value_type_id`),
  KEY `key_start_date` (`start_date`),
  KEY `key_end_date` (`end_date`),
  KEY `key_status` (`status`),
  CONSTRAINT `fk_heatmeter_period_type_attribute__heatmeter_period_type` FOREIGN KEY (`object_id`) REFERENCES `heatmeter_period_type`(`object_id`),
  CONSTRAINT `fk_heatmeter_period_type_attribute__entity_attribute_type` FOREIGN KEY (`attribute_type_id`)
  REFERENCES `entity_attribute_type` (`id`),
  CONSTRAINT `fk_heatmeter_period_type_attribute__entity_attribute_value_type` FOREIGN KEY (`value_type_id`)
  REFERENCES `entity_attribute_value_type` (`id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Атрибуты типа периода теплосчетчика';

DROP TABLE IF EXISTS `heatmeter_period_type_string_culture`;

CREATE TABLE `heatmeter_period_type_string_culture` (
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Суррогатный ключ',
  `id` BIGINT(20) NOT NULL COMMENT 'Идентификатор значения',
  `locale_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор локали',
  `value` VARCHAR(1000) COMMENT 'Текстовое значение',
  PRIMARY KEY (`pk_id`),
  UNIQUE KEY `unique_id__locale` (`id`,`locale_id`),
  KEY `key_locale` (`locale_id`),
  KEY `key_value` (`value`),
  CONSTRAINT `fk_heatmeter_period_type_string_culture__locales` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Локализированное значение атрибута типа периода теплосчетчика';

-- ------------------------------
-- heatmeter
-- ------------------------------

DROP TABLE IF EXISTS `heatmeter`;
CREATE TABLE `heatmeter`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `ls` INT(7) NOT NULL COMMENT 'Номер л/с теплосчетчика',
  `type_id` BIGINT(20) NOT NULL COMMENT 'Ссылка на тип счетчика',
  `building_code_id` BIGINT(20) NOT NULL COMMENT 'Ссылка на код дома',
  PRIMARY KEY (`id`),
  UNIQUE KEY `heatmeter_unique_id` (`ls`, `building_code_id`),
  KEY `key_heatmeter_ls` (`ls`),
  KEY `key_heatmeter_building_code_id` (`building_code_id`),
  CONSTRAINT `fk_heatmeter__heatmeter_type` FOREIGN KEY (`type_id`) REFERENCES `heatmeter_type` (`object_id`),
  CONSTRAINT `fk_heatmeter__building_code` FOREIGN KEY (`building_code_id`) REFERENCES `building_code` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Теплосчетчик';

-- ------------------------------
-- heatmeter Period
-- ------------------------------

DROP TABLE IF EXISTS `heatmeter_period`;
CREATE TABLE `heatmeter_period`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор объекта',
  `heatmeter_id` BIGINT(20) NOT NULL COMMENT 'Ссылка на теплосчетчик',
  `type_id` BIGINT(20) NOT NULL COMMENT 'Ссылка на тип теплосчетчика',
  `begin_date` DATE COMMENT 'Дата начала периода',
  `end_date` DATE COMMENT 'Дата окончания периода',
  `operating_month` DATE NOT NULL COMMENT  'Операционный месяц установки периода',
  PRIMARY KEY (`id`),
  KEY `key_object_id` (`parent_id`),
  KEY `key_heatmeter_id` (`heatmeter_id`),
  KEY `key_type_id` (`type_id`),
  KEY `key_operating_month` (`operating_month`),
  CONSTRAINT `fk_heatmeter_period__heatmeter_period` FOREIGN KEY (`parent_id`) REFERENCES `heatmeter_period` (`id`),
  CONSTRAINT `fk_heatmeter_period__heatmeter` FOREIGN KEY (`heatmeter_id`) REFERENCES `heatmeter` (`id`),
  CONSTRAINT `fk_heatmeter_period__heatmeter_period_type` FOREIGN KEY (`type_id`) REFERENCES `heatmeter_period_type` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Период теплосчетчика';


DROP TABLE IF EXISTS `payload`;
CREATE TABLE `payload`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `parent_id` BIGINT(20) COMMENT 'Идентификатор объекта',
  `heatmeter_id` BIGINT(20) NOT NULL COMMENT 'Ссылка на теплосчетчик',
  `begin_date` DATE COMMENT 'Дата начала периода',
  `end_date` DATE COMMENT 'Дата окончания периода',
  `operating_month` DATE NOT NULL COMMENT  'Операционный месяц установки периода',
  `payload_1` DECIMAL(5, 2) COMMENT 'Процент распределения расхода для тарифной группы 1',
  `payload_2` DECIMAL(5, 2) COMMENT 'Процент распределения расхода для тарифной группы 2',
  `payload_3` DECIMAL(5, 2) COMMENT 'Процент распределения расхода для тарифной группы 3',
  PRIMARY KEY (`id`),
  UNIQUE KEY `payload_unique_id` (`heatmeter_id`, `begin_date`, `end_date`, `operating_month`),
  KEY `key_parent_id` (`parent_id`),
  KEY `key_heatmeter_id` (`heatmeter_id`),
  KEY `key_operating_month` (`operating_month`),
  CONSTRAINT `fk_payload__heatmeter` FOREIGN KEY (`heatmeter_id`) REFERENCES `heatmeter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Проценты распределения расходов';

-- ------------------------------
-- Corrections
-- ------------------------------
DROP TABLE IF EXISTS `city_correction`;

CREATE TABLE `city_correction` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
    `parent_id` BIGINT(20) COMMENT 'Не используется',
    `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта населенного пункта',
    `correction` VARCHAR(100) NOT NULL COMMENT 'Название населенного пункта',
    `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
    `organization_code` VARCHAR(100) COMMENT 'Код',
    `internal_organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор внутренней организации',
    `user_organization_id` BIGINT(20),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_city_correction` (`object_id`, `correction`, `organization_id`, `user_organization_id`, `internal_organization_id`),
    KEY `key_object_id` (`object_id`),
    KEY `key_correction` (`correction`),
    KEY `key_organization_id` (`organization_id`),
    KEY `key_internal_organization_id` (`internal_organization_id`),
    KEY `key_user_organization_id` (`user_organization_id`),
    CONSTRAINT `fk_city_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
    CONSTRAINT `fk_city_correction__city` FOREIGN KEY (`object_id`) REFERENCES `city` (`object_id`),
    CONSTRAINT `fk_city_correction__internal_organization` FOREIGN KEY (`internal_organization_id`) REFERENCES `organization` (`object_id`),
    CONSTRAINT `fk_city_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Коррекция населенного пункта';

DROP TABLE IF EXISTS `city_type_correction`;

CREATE TABLE `city_type_correction` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
    `parent_id` BIGINT(20) COMMENT 'Не используется',
    `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта типа населенного пункта',
    `correction` VARCHAR(100) NOT NULL COMMENT 'Название типа населенного пункта',
    `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
    `organization_code` VARCHAR(100) COMMENT 'Код',
    `internal_organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор внутренней организации',
    `user_organization_id` BIGINT(20),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_city_type_correction` (`object_id`, `correction`, `organization_id`, `user_organization_id`, `internal_organization_id`),
    KEY `key_object_id` (`object_id`),
    KEY `key_correction` (`correction`),
    KEY `key_organization_id` (`organization_id`),
    KEY `key_internal_organization_id` (`internal_organization_id`),
    KEY `key_user_organization_id` (`user_organization_id`),
    CONSTRAINT `fk_city_type__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
    CONSTRAINT `fk_city_type_correction__city_type` FOREIGN KEY (`object_id`) REFERENCES `city_type` (`object_id`),
    CONSTRAINT `fk_city_type_correction__internal_organization` FOREIGN KEY (`internal_organization_id`) REFERENCES `organization` (`object_id`),
    CONSTRAINT `fk_city_type_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Коррекция типа населенного пункта';

DROP TABLE IF EXISTS `district_correction`;

CREATE TABLE `district_correction` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
    `parent_id` BIGINT(20) COMMENT 'Идентификатор населенного пункта',
    `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта района',
    `correction` VARCHAR(100) NOT NULL COMMENT 'Название района',
    `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
    `organization_code` VARCHAR(100) COMMENT 'Код',
    `internal_organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор внутренней организации',
    `user_organization_id` BIGINT(20),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_district_correction` (`parent_id`, `object_id`, `correction`, `organization_id`, `user_organization_id`, `internal_organization_id`),
    KEY `key_object_id` (`object_id`),
    KEY `key_parent_id` (`parent_id`),
    KEY `key_correction` (`correction`),
    KEY `key_organization_id` (`organization_id`),
    KEY `key_internal_organization_id` (`internal_organization_id`),
    KEY `key_user_organization_id` (`user_organization_id`),
    CONSTRAINT `fk_district_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
    CONSTRAINT `fk_district_correction__district` FOREIGN KEY (`object_id`) REFERENCES `district` (`object_id`),
    CONSTRAINT `fk_district_correction__city_correction` FOREIGN KEY (`parent_id`) REFERENCES `city_correction` (`id`),
    CONSTRAINT `fk_district_correction__internal_organization` FOREIGN KEY (`internal_organization_id`) REFERENCES `organization` (`object_id`),
    CONSTRAINT `fk_district_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Коррекция района';

DROP TABLE IF EXISTS `street_correction`;

CREATE TABLE `street_correction` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
    `parent_id` BIGINT(20) COMMENT 'Идентификатор населенного пункта',
    `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта улицы',
    `correction` VARCHAR(100) NOT NULL COMMENT 'Название улицы',
    `street_type_correction_id` BIGINT(20) NULL COMMENT 'Идентификатор коррекции типа улицы',
    `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
    `organization_code` VARCHAR(100) COMMENT 'Код',
    `internal_organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор внутренней организации',
    `user_organization_id` BIGINT(20),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_street_correction` (`parent_id`, `correction`, `organization_id`, `user_organization_id`, `internal_organization_id`,
            `street_type_correction_id`, `object_id`, `organization_code`),
    KEY `key_object_id` (`object_id`),
    KEY `key_parent_id` (`parent_id`),
    KEY `key_correction` (`correction`),
    KEY `key_organization_id` (`organization_id`),
    KEY `key_internal_organization_id` (`internal_organization_id`),
    KEY `key_street_type_correction_id` (`street_type_correction_id`),
    KEY `key_user_organization_id` (`user_organization_id`),
    CONSTRAINT `fk_street_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
    CONSTRAINT `fk_street_correction__street` FOREIGN KEY (`object_id`) REFERENCES `street` (`object_id`),
    CONSTRAINT `fk_street_correction__city_correction` FOREIGN KEY (`parent_id`) REFERENCES `city_correction` (`id`),
    CONSTRAINT `fk_street_correction__internal_organization` FOREIGN KEY (`internal_organization_id`) REFERENCES `organization` (`object_id`),
    CONSTRAINT `fk_street_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
    CONSTRAINT `fk_street_correction__street_type_correctionn` FOREIGN KEY (`street_type_correction_id`) REFERENCES `street_type_correction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Коррекция улицы';

DROP TABLE IF EXISTS `street_type_correction`;

CREATE TABLE `street_type_correction` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
    `parent_id` BIGINT(20) COMMENT 'Не используется',
    `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта типа улицы',
    `correction` VARCHAR(100) NOT NULL COMMENT 'Название типа улицы',
    `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
    `organization_code` VARCHAR(100) COMMENT 'Код',
    `internal_organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор внутренней организации',
    `user_organization_id` BIGINT(20),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_street_type_correction` (`object_id`, `correction`, `organization_id`, `user_organization_id`, `internal_organization_id`),
    KEY `key_object_id` (`object_id`),
    KEY `key_correction` (`correction`),
    KEY `key_organization_id` (`organization_id`),
    KEY `key_internal_organization_id` (`internal_organization_id`),
    KEY `key_user_organization_id` (`user_organization_id`),
    CONSTRAINT `fk_street_type__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
    CONSTRAINT `fk_street_type_correction__street_type` FOREIGN KEY (`object_id`) REFERENCES `street_type` (`object_id`),
    CONSTRAINT `fk_street_type_correction__internal_organization` FOREIGN KEY (`internal_organization_id`) REFERENCES `organization` (`object_id`),
    CONSTRAINT `fk_street_type_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT 'Коррекция типа улицы';

DROP TABLE IF EXISTS `building_correction`;

CREATE TABLE `building_correction` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор коррекции',
    `parent_id` BIGINT(20) COMMENT 'Идентификатор коррекции улицы',
    `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта дома',
    `correction` VARCHAR(20) NOT NULL COMMENT 'Номер дома',
    `correction_corp` VARCHAR(20) NOT NULL DEFAULT '' COMMENT 'Корпус дома',
    `organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
    `organization_code` VARCHAR(100) COMMENT 'Код',
    `internal_organization_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор внутренней организации',
    `user_organization_id` BIGINT(20),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_building_correction` (`parent_id`, `object_id`, `correction`, `correction_corp`, `organization_id`, 
                `user_organization_id`, `internal_organization_id`),
    KEY `key_object_id` (`object_id`),
    KEY `key_parent_id` (`parent_id`),
    KEY `key_correction` (`correction`),
    KEY `key_organization_id` (`organization_id`),
    KEY `key_internal_organization_id` (`internal_organization_id`),
    KEY `key_user_organization_id` (`user_organization_id`),
    CONSTRAINT `fk_building_correction__user_organization` FOREIGN KEY (`user_organization_id`) REFERENCES `organization` (`object_id`),
    CONSTRAINT `fk_building_correction__building` FOREIGN KEY (`object_id`) REFERENCES `building` (`object_id`),
    CONSTRAINT `fk_building_correction__street_correction` FOREIGN KEY (`parent_id`) REFERENCES `street_correction` (`id`),
    CONSTRAINT `fk_building_correction__internal_organization` FOREIGN KEY (`internal_organization_id`) REFERENCES `organization` (`object_id`),
    CONSTRAINT `fk_building_correction__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB DEFAULT  CHARSET=utf8 COMMENT 'Коррекция дома';

DROP TABLE IF EXISTS `operating_month`;
CREATE TABLE `operating_month`(
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
    `organization_id` BIGINT(20) NOT NULL COMMENT 'Ссылка на организацию',
    `operating_month` DATE NOT NULL COMMENT  'Операционный месяц',
    `operating_month_end` DATE NOT NULL COMMENT  'Конец операционного месяца',
    `updated` TIMESTAMP NULL COMMENT  'Время изменения опер. месяца',
    PRIMARY KEY (`id`),
    KEY `key_organization_id` (`organization_id`),
    CONSTRAINT `fk_operating_month__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Журнал операционных месяцев';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
