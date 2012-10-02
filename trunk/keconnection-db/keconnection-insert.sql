-- --------------------------------
-- Sequence
-- --------------------------------
INSERT INTO `sequence` (`sequence_name`, `sequence_value`) VALUES
('tarif_group',1), ('tarif_group_string_culture',1),
('tarif',1), ('tarif_string_culture',1),
('heatmeter_type',1), ('heatmeter_type_string_culture',1),
('heatmeter_period_type',1), ('heatmeter_period_type_string_culture',1);

-- --------------------------------
-- Organization type
-- --------------------------------

INSERT INTO `organization_type`(`object_id`) VALUES (2);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (2, 1, UPPER('МОДУЛЬ НАЧИСЛЕНИЙ')), (2, 2, UPPER('МОДУЛЬ НАЧИСЛЕНИЙ'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (1, 2, 2300, 2, 2300);

INSERT INTO `organization_type`(`object_id`) VALUES (3);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (3, 1, UPPER('БАЛАНСОДЕРЖАТЕЛЬ')), (3, 2, UPPER('БАЛАНСОДЕРЖАТЕЛЬ'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (1, 3, 2300, 3, 2300);

INSERT INTO `organization_type`(`object_id`) VALUES (4);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (4, 1, UPPER('ОБСЛУЖИВАЮЩАЯ ОРГАНИЗАЦИЯ')), (4, 2, UPPER('ОБСЛУЖИВАЮЩАЯ ОРГАНИЗАЦИЯ'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (1, 4, 2300, 4, 2300);

INSERT INTO `organization_type`(`object_id`) VALUES (5);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (5, 1, UPPER('ПОСТАВЩИК УСЛУГ')), (5, 2, UPPER('ПОСТАВЩИК УСЛУГ'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (1, 5, 2300, 5, 2300);

INSERT INTO `organization_type`(`object_id`) VALUES (6);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (6, 1, UPPER('ПОДРЯДЧИК')), (6, 2, UPPER('ПОДРЯДЧИК'));
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (1, 6, 2300, 6, 2300);

-- --------------------------------
-- Organization
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (920, 1, UPPER('Короткое наименование')), (920, 2, UPPER('Короткое наименование'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (920, 900, 0, 920, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (920, 920, 'string_culture');

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (921, 1, UPPER('ИСПОЛНИТЕЛЬ')), (921, 2, UPPER('ИСПОЛНИТЕЛЬ'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (921, 900, 1, 921, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (921, 921, 'boolean');

-- ------------------------------
-- Predefined organizations
-- ------------------------------

-- Itself organization
INSERT INTO `organization`(`object_id`) VALUES (0);
INSERT INTO `organization_string_culture`(`id`, `locale_id`, `value`) VALUES
(1, 1, UPPER('Модуль №1')), (1,2,UPPER('Модуль №1')), (2, (SELECT `id` FROM `locales` WHERE `system` = 1), UPPER('0'));
INSERT INTO `organization_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES
(1,0,900,1,900), (1,0,901,2,901);

-- --------------------------------
-- Building
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (503, 1, UPPER('Список кодов дома')), (503, 2, UPPER('Список кодов дома'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (502, 500, 0, 503, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (502, 502, 'building_organization_association');

-- ------------------------------
-- Tarif group
-- ------------------------------
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3200, 1, 'Группа тарифов'), (3200, 2, 'Группа тарифов');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (3200, 'tarif_group', 3200, '');

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3201, 1, UPPER('Название')), (3201, 2, UPPER('Назва'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (3200, 3200, 1, 3201, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (3200, 3200, UPPER('string_culture'));

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3202, 1, UPPER('Код тарифной группы')), (3202, 2, UPPER('Код тарифной группы'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (3201, 3200, 1, 3202, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (3201, 3201, UPPER('integer'));

-- ------------------------------
-- Predefined tarif groups
-- ------------------------------
INSERT INTO `tarif_group`(`object_id`) VALUES (1),(2),(3);
INSERT INTO `tarif_group_string_culture`(`id`, `locale_id`, `value`) VALUES 
(1, 1, UPPER('население')), (1, 2,UPPER('население')), (2,(SELECT `id` FROM `locales` WHERE `system` = 1), UPPER('1')),
(3, 1, UPPER('бюджетные организации')), (3, 2, UPPER('бюджетные организации')), (4,(SELECT `id` FROM `locales` WHERE `system` = 1), UPPER('2')),
(5, 1, UPPER('прочие потребители')), (5, 2, UPPER('прочие потребители')), (6,(SELECT `id` FROM `locales` WHERE `system` = 1), UPPER('3'));
INSERT INTO `tarif_group_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES
(1,1,3200,1,3200),(1,1,3201,2,3201),(1,2,3200,3,3200),(1,2,3201,4,3201),(1,3,3200,5,3200),(1,3,3201,6,3201);

-- ------------------------------
-- Tarif
-- ------------------------------
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3300, 1, 'Тариф'), (3300, 2, 'Тариф');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (3300, 'tarif', 3300, '');

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3301, 1, UPPER('Название')), (3301, 2, UPPER('Назва'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (3300, 3300, 1, 3301, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (3300, 3300, UPPER('string_culture'));

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3302, 1, UPPER('Код тарифа')), (3302, 2, UPPER('Код тарифа'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (3301, 3300, 1, 3302, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (3301, 3301, UPPER('integer'));

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3303, 1, UPPER('Тарифная группа')), (3303, 2, UPPER('Тарифная группа'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (3302, 3300, 1, 3303, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (3302, 3302, 'tarif_group');

-- ------------------------------
-- Heatmeater Type
-- ------------------------------
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3400, 1, 'Тип теплосчетчика'), (3400, 2, 'Тип теплосчетчика');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (3400, 'heatmeter_type', 3400, '');

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3401, 1, UPPER('Название')), (3401, 2, UPPER('Назва'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (3400, 3400, 1, 3401, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (3400, 3400, UPPER('string_culture'));

-- ------------------------------
-- Predefined heatmeter types
-- ------------------------------
INSERT INTO `heatmeter_type`(`object_id`) VALUES (1),(2);
INSERT INTO `heatmeter_type_string_culture`(`id`, `locale_id`, `value`) VALUES 
(1, 1, UPPER('отопление')), (1, 2,UPPER('отопление')),
(2, 1, UPPER('отопление и подогрев воды')), (2, 2, UPPER('отопление и подогрев воды'));
INSERT INTO `heatmeter_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES
(1,1,3400,1,3400),(1,2,3400,2,3400);

-- ------------------------------
-- Heatmeater Period Type
-- ------------------------------
INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3500, 1, 'Тип периода теплосчетчика'), (3500, 2, 'Тип периода теплосчетчика');
INSERT INTO `entity`(`id`, `entity_table`, `entity_name_id`, `strategy_factory`) VALUES (3500, 'heatmeter_period_type', 3500, '');

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (3501, 1, UPPER('Название')), (3501, 2, UPPER('Назва'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (3500, 3500, 1, 3501, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (3500, 3500, UPPER('string_culture'));

-- ------------------------------
-- Predefined heatmeter period types
-- ------------------------------
INSERT INTO `heatmeter_period_type`(`object_id`) VALUES (1),(2);
INSERT INTO `heatmeter_period_type_string_culture`(`id`, `locale_id`, `value`) VALUES 
(1, 1, UPPER('функционирование')), (1, 2,UPPER('функционирование')),
(2, 1, UPPER('юстировка')), (2, 2, UPPER('юстировка'));
INSERT INTO `heatmeter_period_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`) VALUES
(1,1,3500,1,3500),(1,2,3500,2,3500);