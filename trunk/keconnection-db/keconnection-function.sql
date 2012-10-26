DROP FUNCTION IF EXISTS `keconnection`.`heatmeter_status` $$
CREATE FUNCTION `keconnection`.`heatmeter_status` (pHeatmeterId BIGINT) RETURNS INT
  BEGIN
    declare connections_count int;
    declare period_operation_count int;
    declare period_adjustment_count int;

    select count(*) into connections_count from `heatmeter_connection` where `heatmeter_id` = pHeatmeterId;
    select count(*) into period_operation_count from `heatmeter_period` where `end_date` is null and `heatmeter_id` = pHeatmeterId and `type` = 1;
    select count(*) into period_adjustment_count from `heatmeter_period` where `end_date` is null and `heatmeter_id` = pHeatmeterId and `type` = 2;

    if (connections_count = 0) then return 3;
    elseif (period_operation_count = 0) then return 0;
    elseif (period_operation_count > 0 and period_adjustment_count = 0) then return 1;
    elseif (period_operation_count > 0 and period_adjustment_count > 0) then return 2;
    end if;

    return -1;

  END $$