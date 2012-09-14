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
-- Heatmeater Type
-- ------------------------------

DROP TABLE IF EXISTS `heatmeater_type`;
CREATE TABLE `heatmeater_type`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `name` VARCHAR(255) NOT NULL COMMENT 'Название типа теплосчетчика',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Тип теплосчетчика';

-- ------------------------------
-- Heatmeater
-- ------------------------------

DROP TABLE IF EXISTS `heatmeater`;
CREATE TABLE `heatmeater`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `ls` INT(7) NOT NULL COMMENT 'Номер л/с теплосчетчика',
  `type_id` BIGINT(20) NOT NULL COMMENT 'Ссылка на тип счетчика',
  `building_code_id` BIGINT(20) NOT NULL COMMENT 'Ссылка на код дома',
  PRIMARY KEY (`id`),
  UNIQUE KEY `heatmeater_unique_id` (`ls`, `building_code_id`),
  KEY `key_ls` (`ls`),
  KEY `key_building_code_id` (`building_code_id`),
  CONSTRAINT `fk_heatmeater__heatmeater_type` FOREIGN KEY (`type_id`) REFERENCES `heatmeater_type` (`id`)
  --  todo CONSTRAINT `fk_heatmeater__building_code` FOREIGN KEY (`building_code_id`) REFERENCES `building_code` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Теплосчетчик';

-- ------------------------------
-- Heatmeater Period Type
-- ------------------------------

DROP TABLE IF EXISTS `heatmeater_period_type`;
CREATE TABLE `heatmeater_period_type`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `name` VARCHAR(255) NOT NULL COMMENT 'Название типа периода теплосчетчика',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Тип периода теплосчетчика';

-- ------------------------------
-- Heatmeater Period
-- ------------------------------

DROP TABLE IF EXISTS `heatmeater_period`;
CREATE TABLE `heatmeater_period`(
  `pk_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор объекта',
  `heatmeater_id` BIGINT(20) NOT NULL COMMENT 'Ссылка на теплосчетчик',
  `type_id` BIGINT(20) NOT NULL COMMENT 'Ссылка на тип теплосчетчика',
  `begin_date` DATE COMMENT 'Дата начала периода',
  `end_date` DATE COMMENT 'Дата окончания периода',
  `operating_month` DATE NOT NULL COMMENT  'Операционный месяц установки периода',
  PRIMARY KEY (`pk_id`),
  KEY `key_object_id` (`object_id`),
  KEY `key_heatmeater_id` (`heatmeater_id`),
  KEY `key_type_id` (`type_id`),
  KEY `key_operating_month` (`operating_month`),
  CONSTRAINT `fk_heatmeater_period__heatmeater` FOREIGN KEY (`heatmeater_id`) REFERENCES `heatmeater` (`id`),
  CONSTRAINT `fk_heatmeater_period__heatmeater_period_type` FOREIGN KEY (`type_id`) REFERENCES `heatmeater_period_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Период теплосчетчика';


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;