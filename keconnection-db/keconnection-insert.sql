-- --------------------------------
-- Organization type
-- --------------------------------

INSERT INTO `organization_type`(`object_id`) VALUES (3);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (3, 1, 'МОДУЛЬ НАЧИСЛЕНИЙ'), (3, 2, 'МОДУЛЬ НАЧИСЛЕНИЙ');
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (3, 3, 2300, 3, 2300);

INSERT INTO `organization_type`(`object_id`) VALUES (10);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (10, 1, 'БАЛАНСОДЕРЖАТЕЛЬ'), (10, 2, 'БАЛАНСОДЕРЖАТЕЛЬ');
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (10, 10, 2300, 10, 2300);

INSERT INTO `organization_type`(`object_id`) VALUES (11);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (11, 1, 'ОБСЛУЖИВАЮЩАЯ ОРГАНИЗАЦИЯ'), (11, 2, 'ОБСЛУЖИВАЮЩАЯ ОРГАНИЗАЦИЯ');
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (11, 11, 2300, 11, 2300);

INSERT INTO `organization_type`(`object_id`) VALUES (12);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (12, 1, 'ПОСТАВЩИК УСЛУГ'), (12, 2, 'ПОСТАВЩИК УСЛУГ');
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (12, 12, 2300, 12, 2300);

INSERT INTO `organization_type`(`object_id`) VALUES (13);
INSERT INTO `organization_type_string_culture`(`id`, `locale_id`, `value`)
  VALUES (13, 1, 'ПОДРЯДЧИК'), (13, 2, 'ПОДРЯДЧИК');
INSERT INTO `organization_type_attribute`(`attribute_id`, `object_id`, `attribute_type_id`, `value_id`, `value_type_id`)
  VALUES (13, 13, 2300, 13, 2300);

-- --------------------------------
-- Organization
-- --------------------------------

INSERT INTO `string_culture`(`id`, `locale_id`, `value`) VALUES (920, 1, UPPER('ИСПОЛНИТЕЛЬ')), (920, 2, 'ИСПОЛНИТЕЛЬ');
INSERT INTO `entity_attribute_type`(`id`, `entity_id`, `mandatory`, `attribute_type_name_id`, `system`) VALUES (920, 900, 1, 920, 1);
INSERT INTO `entity_attribute_value_type`(`id`, `attribute_type_id`, `attribute_value_type`) VALUES (920, 920, 'boolean');

