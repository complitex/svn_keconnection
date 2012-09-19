-- sequence update

update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `organization_string_culture`)+1 where sequence_name = 'organization_string_culture';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `organization`)+1 where sequence_name = 'organization';
update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `organization_type_string_culture`)+1 where sequence_name = 'organization_type_string_culture';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `organization_type`)+1 where sequence_name = 'organization_type';
update sequence set sequence_value = (select IFNULL(max(`id`), 0) from `tarif_group_string_culture`)+1 where sequence_name = 'tarif_group_string_culture';
update sequence set sequence_value = (select IFNULL(max(`object_id`), 0) from `tarif_group`)+1 where sequence_name = 'tarif_group';