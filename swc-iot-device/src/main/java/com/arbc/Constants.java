package com.arbc;

public class Constants {

    // Publisher and subscriber topics
    public static final String SUBSCRIBER_TOPIC_INTERIOR_HEATER = "swc_iot_device/interior_heater";
    public static final String SUBSCRIBER_TOPIC_ORDINARY_LIGHTS = "swc_iot_device/ordinary_lights";
    public static final String SUBSCRIBER_TOPIC_AIR_CONDITIONER = "swc_iot_device/air_conditioner";
    public static final String SUBSCRIBER_TOPIC_ROOF = "swc_iot_device/roof";
    public static final String SUBSCRIBER_TOPIC_CHANGING_COLOR_LIGHTS = "swc_iot_device/changing_color_lights";
    public static final String SUBSCRIBER_TOPIC_WATER_PUMP = "swc_iot_device/water_pump";
    public static final String PUBLISHER_TOPIC_INTERIOR_HEATER = "swc-management/interior_heater";
    public static final String PUBLISHER_TOPIC_ORDINARY_LIGHTS = "swc-management/ordinary_lights";
    public static final String PUBLISHER_TOPIC_AIR_CONDITIONER = "swc-management/air_conditioner";
    public static final String PUBLISHER_TOPIC_ROOF = "swc-management/roof";
    public static final String PUBLISHER_TOPIC_CHANGING_COLOR_LIGHTS = "swc-management/changing_color_lights";
    public static final String PUBLISHER_TOPIC_WATER_PUMP = "swc-management/water_pump";

    // JSON device responses
    public static final String INTERIOR_HEATER_JSON = "{ \"device\" : \"interior-heater\" , \"resource\": \"%s\" , \"temperature\": \"%s\" , \"power\" : \"%s\" , \"message\": \"%s\"}";
    public static final String AIR_CONDITIONER_JSON = "{ \"device\" : \"air-conditioner\" , \"resource\": \"%s\" , \"temperature\": \"%s\" , \"power\": \"%s\" , \"message\": \"%s\"}";
    public static final String ORDINARY_LIGHTS_JSON = "{ \"device\" : \"ordinary-lights\" , \"resource\": \"%s\" , \"volume\": \"%s\" , \"message\": \"%s\"}";
    public static final String ROOF_JSON = "{ \"device\" : \"roof\" , \"resource\": \"%s\" , \"state\": \"%s\" , \"message\": \"%s\"}";
    public static final String CHANGING_COLOR_LIGHTS_JSON = "{ \"device\" : \"changing-color-lights\" , \"resource\": \"%s\" , \"color\": \"%s\" , \"message\": \"%s\"}";
    public static final String WATER_PUMP_JSON = "{ \"device\" : \"water-pump\" , \"resource\": \"%s\" , \"state\": \"%s\" , \"message\": \"%s\"}";
}
