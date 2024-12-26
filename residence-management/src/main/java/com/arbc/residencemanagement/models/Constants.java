package com.arbc.residencemanagement.models;

public class Constants {

    // Regexes and constraint violation messages for responses on CRUD operations on residents
    public static final String EMAIL_FIELD_REQUIRED_VIOLATION_MESSAGE = "Email is required.";
    public static final String RESIDENCE_NUMBER_FIELD_REQUIRED_VIOLATION_MESSAGE = "Residence number is required.";
    public static final String RESIDENCE_NUMBERS_FIELD_REQUIRED_VIOLATION_MESSAGE = "List of residence numbers is required.";
    public static final String HEATER_OPERATION_FIELD_REQUIRED_VIOLATION_MESSAGE = "Operation type on heater is required.";
    public static final String AIR_CONDITIONER_OPERATION_FIELD_REQUIRED_VIOLATION_MESSAGE = "Operation type on air conditioner is required.";
    public static final String WINDOWS_OPERATION_FIELD_REQUIRED_VIOLATION_MESSAGE = "Operation type on windows is required.";
    public static final String WINDOWS_LOCATION_FIELD_REQUIRED_VIOLATION_MESSAGE = "Windows location on which the operation will be performed is required.";

    // Exception messages
    public static final String SERVER_ERROR_EXCEPTION_MESSAGE = "Server error occurred, please try again...";
    public static final String RESIDENCES_ATTACHMENT_ERROR_EXCEPTION_MESSAGE = "Residences could not be attached, please try again...";
    public static final String RESIDENCES_DETACHMENT_ERROR_EXCEPTION_MESSAGE = "Residences could not be detached, please try again...";
    public static final String RESIDENCE_ALREADY_OWNED_EXCEPTION_MESSAGE = "Residence with number %s is already tied to a resident.";
    public static final String RESIDENCES_MUST_BE_SPECIFIED_EXCEPTION_MESSAGE = "List of residences can't be empty when attaching residents to residences.";
    public static final String RESIDENCE_NOT_FOUND_EXCEPTION_MESSAGE = "Residence with number %s not found.";
    public static final String RESIDENCE_OWNERSHIP_EXCEPTION_MESSAGE = "Residence with number %s does not belong to resident with id %s.";
    public static final String RESIDENCES_OWNERSHIP_EXCEPTION_MESSAGE = "Not all of residences %s belong to resident with id %s.";
    public static final String NOT_AUTHORIZED_MESSAGE = "You are not authorized to access %s with method %s.";
    public static final String NOT_AUTHENTICATED_MESSAGE = "You are not authenticated to access %s with method %s.";

    // Users Management API paths and properties
    public static final String USER_BASE_URI = "http://localhost";
    public static final String USER_PORT = "8080";
    public static final String USER_CONTEXT_PATH = "/user-management-1.0-SNAPSHOT";
    public static final String RETRIEVE_RESIDENT_BY_EMAIL = "/api/residents/search/email-exact-match";

    // Exchange Names
    public static final String RESIDENCE_MANAGEMENT_EXCHANGE = "residence_management_exchange";

    // Queue Names
    public static final String QUEUE_ADD_RESIDENCE_TO_ACCOUNT = "add_residence_to_account";
    public static final String QUEUE_REMOVE_RESIDENCE_FROM_ACCOUNT = "remove_residence_from_account";

    // Routing Keys
    public static final String ROUTING_KEY_ADD_RESIDENT = "account.add_residence";
    public static final String ROUTING_KEY_REMOVE_RESIDENT = "account.remove_residence";

    // Mongo database properties
    public static final String MONGO_USERNAME = "appUser";
    public static final String MONGO_PASSWORD = "appUser";
    public static final String MONGO_HOST = "localhost";
    public static final String MONGO_PORT = "27017";
    public static final String DATABASE = "arbc";

    // RabbitMQ properties
    public static final String RABBIT_HOST = "localhost";
    public static final int RABBIT_PORT = 5672;
    public static final String RABBIT_USERNAME = "guest";
    public static final String RABBIT_PASSWORD = "guest";

    // Mosquitto properties
    public static final String MQTT_BROKER = "tcp://localhost";
    public static final String MQTT_PORT = "1883";
    public static final String MQTT_USERNAME = "admin";
    public static final String MQTT_PASSWORD = "admin";
    public static final String MQTT_CLIENT_ID = "residence-management";
    public static final String SUBSCRIBER_TOPIC_HEATER_TEMPLATE = "residence_management/%s/interior_heater";
    public static final String SUBSCRIBER_TOPIC_AIR_CONDITION_TEMPLATE = "residence_management/%s/air_conditioner";
    public static final String SUBSCRIBER_TOPIC_WINDOWS_TEMPLATE = "residence_management/%s/windows";
    public static final String PUBLISHER_TOPIC_HEATER_TEMPLATE = "building_apartment_iot_device/%s/interior_heater";
    public static final String PUBLISHER_TOPIC_AIR_CONDITION_TEMPLATE = "building_apartment_iot_device/%s/air_conditioner";
    public static final String PUBLISHER_TOPIC_WINDOWS_TEMPLATE = "building_apartment_iot_device/%s/windows";

}
