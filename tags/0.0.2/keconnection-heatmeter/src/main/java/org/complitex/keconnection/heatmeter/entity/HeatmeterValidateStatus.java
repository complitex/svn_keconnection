package org.complitex.keconnection.heatmeter.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.10.12 12:59
 */
public enum HeatmeterValidateStatus {
    VALID,

    HEATMETER_LS_REQUIRED,
    HEATMETER_TYPE_REQUIRED,

    ERROR_PERIOD_BEGIN_DATE_REQUIRED,
    ERROR_PERIOD_BEGIN_DATE_AFTER_END_DATE,
    ERROR_PERIOD_TYPE_REQUIRED,
    ERROR_PERIOD_MORE_THAN_TWO_OPEN_OPERATION,
    ERROR_PERIOD_MORE_THAN_TWO_OPEN_ADJUSTMENT,
    ERROR_PERIOD_INTERSECTION,
    ERROR_PERIOD_OPERATION_MUST_ENCLOSES_ADJUSTMENT,

    ERROR_CONNECTION_AT_LEAST_ONE_CONNECTION,
    ERROR_CONNECTION_BEGIN_DATE_REQUIRED,
    ERROR_CONNECTION_BEGIN_DATE_AFTER_END_DATE,
    ERROR_CONNECTION_NOT_FOUND,
    ERROR_CONNECTION_INTERSECTION,
    ERROR_CONNECTION_NOT_USER_ORGANIZATION,

    ERROR_PAYLOAD_BEGIN_DATE_REQUIRED,
    ERROR_PAYLOAD_BEGIN_DATE_AFTER_END_DATE,
    ERROR_PAYLOAD_VALUES_REQUIRED,
    ERROR_PAYLOAD_SUM_100,
    ERROR_PAYLOAD_INTERSECTION,
    ERROR_PAYLOAD_MUST_ENCLOSES_OPERATION,

    ERROR_INPUT_END_DATE_REQUIRED,
    ERROR_INPUT_VALUE_REQUIRED,
    ERROR_INPUT_INTERSECTION
}
