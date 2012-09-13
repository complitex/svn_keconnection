/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- ------------------------------
-- Heatmeater
-- ------------------------------

DROP TABLE IF EXISTS `heatmeater`;
CREATE TABLE `heatmeater`(
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `gek` INT(11) NOT NULL COMMENT 'Код структурной единицы',
  `dom` INT(11) NOT NULL COMMENT 'Код дома',
  `ul` VARCHAR(255) NOT NULL COMMENT 'Название улицы вместе с типом',
  `ndom` VARCHAR(10) NOT NULL COMMENT 'Номер дома',
  `lotop0` INT(11) COMMENT 'Номер л/с первого счетчика в доме',
  `lotop1` INT(11) COMMENT 'Номер л/с второго счетчика в доме',
  `lotop2` INT(11) COMMENT 'Номер л/с третьего счетчика в доме',
  `lotop3` INT(11) COMMENT 'Номер л/с четвертого счетчика в доме',
  `lotop4` INT(11) COMMENT 'Номер л/с пятого счетчика в доме',
  `organization_id` BIGINT(20) COMMENT 'Идентификатор структурной единицы',
  `building_id` BIGINT(20) COMMENT 'Идентификатор дома',
  PRIMARY KEY (`id`),
  UNIQUE KEY `heatmeater_unique_id` (`gek`, `dom`),
  KEY `key_organization_id` (`organization_id`),
  KEY `ke_building_id` (`building_id`),
  CONSTRAINT `fk_heatmeater__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`object_id`),
  CONSTRAINT `fk_heatmeater__building` FOREIGN KEY (`building_id`) REFERENCES `building` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Теплосчетчик';

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

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;