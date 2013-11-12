-- SET GLOBAL log_bin_trust_function_creators = 1 $$

-- -------------------
-- Heatmeter Status
-- -------------------

DROP FUNCTION IF EXISTS `keconnection`.`heatmeter_status` $$
CREATE FUNCTION `keconnection`.`heatmeter_status` (pHeatmeterId BIGINT) RETURNS INT DETERMINISTIC
  BEGIN
    declare connections_count int;
    declare period_operation_count int;
    declare period_adjustment_count int;

    select count(*) into connections_count from `heatmeter_period` where `heatmeter_id` = pHeatmeterId and `type` = 2;
    select count(*) into period_operation_count from `heatmeter_period` where `end_date` >= '2054-12-31' and `heatmeter_id` = pHeatmeterId and `type` = 1 and `sub_type` = 1;
    select count(*) into period_adjustment_count from `heatmeter_period` where `end_date` >= '2054-12-31' and `heatmeter_id` = pHeatmeterId and `type` = 1 and `sub_type` = 2;

    if (connections_count = 0) then return 3;
    elseif (period_operation_count = 0) then return 0;
    elseif (period_operation_count > 0 and period_adjustment_count = 0) then return 1;
    elseif (period_operation_count > 0 and period_adjustment_count > 0) then return 2;
    end if;

    return -1;

  END $$

-- --------------------------
-- Heatmeter Operating Month
-- --------------------------

DROP FUNCTION IF EXISTS `keconnection`.`heatmeter_active_om` $$
CREATE FUNCTION `keconnection`.`heatmeter_active_om` (pHeatmeterId BIGINT) RETURNS DATE DETERMINISTIC
  BEGIN

    DECLARE om DATE;

    SELECT MAX(om.`begin_om`) INTO om
        FROM `heatmeter_period` p
        LEFT JOIN `building_code` bc ON bc.`id` = p.`object_id`
        LEFT JOIN `operating_month` om ON om.`organization_id` = bc.`organization_id`
      WHERE p.`type` = 2 AND p.`heatmeter_id` = pHeatmeterId;

    RETURN om;

  END $$