ALTER TABLE `heatmeter_consumption`
MODIFY COLUMN `consumption` DECIMAL(15, 7) COMMENT 'Расход',
MODIFY COLUMN `consumption1` DECIMAL(15, 7) COMMENT 'Расхода для тарифной группы 1',
MODIFY COLUMN `consumption2` DECIMAL(15, 7) COMMENT 'Расхода для тарифной группы 2',
MODIFY COLUMN `consumption3` DECIMAL(15, 7) COMMENT 'Расхода для тарифной группы 3';

INSERT INTO `update` (`version`) VALUE ('20121130_94_0.0.1');