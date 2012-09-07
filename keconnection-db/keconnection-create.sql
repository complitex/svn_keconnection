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