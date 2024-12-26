package com.arbc;

public class Constants {

    // Publisher and subscriber topics
    public static final String SUBSCRIBER_TOPIC_INTERIOR_HEATER = "building_apartment_iot_device/%s/interior_heater";
    public static final String SUBSCRIBER_TOPIC_AIR_CONDITIONER = "building_apartment_iot_device/%s/air_conditioner";
    public static final String SUBSCRIBER_TOPIC_WINDOWS = "building_apartment_iot_device/%s/windows";
    public static final String PUBLISHER_TOPIC_INTERIOR_HEATER = "residence_management/%s/interior_heater";
    public static final String PUBLISHER_TOPIC_AIR_CONDITIONER = "residence_management/%s/air_conditioner";
    public static final String PUBLISHER_TOPIC_WINDOWS = "residence_management/%s/windows";

    // JSON device responses
    public static final String INTERIOR_HEATER_JSON = "{ \"device\" : \"interior-heater\" , \"temperature\": \"%s\" , \"power\" : \"%s\" , \"message\": \"%s\"}";
    public static final String AIR_CONDITIONER_JSON = "{ \"device\" : \"air-conditioner\" , \"temperature\": \"%s\" , \"power\": \"%s\" , \"message\": \"%s\"}";
    public static final String WINDOWS_JSON = "{ \"device\" : \"windows\" , \"location\": \"%s\", \"windowsState\": \"%s\" , \"message\": \"%s\"}";
}
