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

-- --------------------------------
-- Building
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (503, 1, UPPER('Список кодов дома')), (503, 2, UPPER('Список кодов дома'));
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (502, 500, 0, 503, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (502, 502, 'building_organization_association');

-- ------------------------------
-- Heatmeater Type
-- ------------------------------

INSERT INTO `heatmeater_type`(`id`, `name`) VALUES (100, 'Отопление'), (200, 'Отопление и подогрев воды');

-- ------------------------------
-- Heatmeater Period Type
-- ------------------------------

INSERT INTO `heatmeater_period_type`(`id`, `name`) VALUES (100, 'Функционирование'), (200, 'Юстировка');