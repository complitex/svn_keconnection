alter table `heatmeter_consumption` drop key `payload_unique_id`, add unique key `consumption_unique_id` (`heatmeter_input_id`, `begin_date`, `end_date`, `om`);

INSERT INTO `update` (`version`) VALUE ('20130321_175_0.0.2');