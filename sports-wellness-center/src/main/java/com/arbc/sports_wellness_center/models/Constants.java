package com.arbc.sports_wellness_center.models;

public class Constants {

    // Regexes and constraint violation messages for responses on CRUD operations on residents
    public static final String RESERVATION_APPOINTMENT_RESOURCE_FIELD_REQUIRED_VIOLATION_MESSAGE = "Reservation appointment resource is required.";
    public static final String RESERVATION_APPOINTMENT_START_TIME_FIELD_REQUIRED_VIOLATION_MESSAGE = "Reservation appointment start time is required.";
    public static final String RESERVATION_APPOINTMENT_END_TIME_REQUIRED_VIOLATION_MESSAGE = "Reservation appointment start time is required.";
    public static final String RESERVATION_APPOINTMENT_MAX_USERS_REQUIRED_VIOLATION_MESSAGE = "Reservation appointment users limit is required.";
    public static final String RESERVATION_APPOINTMENT_MAX_USERS_MIN_VIOLATION_MESSAGE = "Reservation appointment users limit must be at least 3.";
    public static final String LIGHTS_COLOR_FIELD_REQUIRED_VIOLATION_MESSAGE = "Color for the lights is required.";
    public static final String RESOURCE_FIELD_REQUIRED_VIOLATION_MESSAGE = "Resource where the device is held is required.";
    public static final String DEVICE_ACTION_FIELD_REQUIRED_VIOLATION_MESSAGE = "Device action is required.";

    // Exception messages
    public static final String RESERVATION_APPOINTMENT_CREATION_ERROR_EXCEPTION_MESSAGE = "Reservation appointment could not be created, please try again...";
    public static final String RESERVATION_APPOINTMENT_DELETION_ERROR_EXCEPTION_MESSAGE = "Reservation appointment could not be deleted, please try again...";
    public static final String RESERVATION_APPOINTMENT_DELETION_TIME_CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE = "You cannot delete a reservation appointment if time before it begins is between 0 and 4 hours.";
    public static final String RESERVATION_CREATION_ERROR_EXCEPTION_MESSAGE = "Reservation could not be created, please try again...";
    public static final String RESERVATION_DELETION_ERROR_EXCEPTION_MESSAGE = "Reservation could not be deleted, please try again...";
    public static final String RESERVATION_ARRIVED_FIELD_UPDATE_ERROR_EXCEPTION_MESSAGE = "Reservation arrived field could not be updated, please try again...";
    public static final String RESERVATION_NUMBER_CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE = "Reservation limit has been reached for this appointment, please try with another one.";
    public static final String RESERVATION_CREATION_TIME_CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE = "You cannot make or delete a reservation 4 hours before reservation appointment.";
    public static final String RESERVATION_APPOINTMENT_START_END_TIME_CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE = "Reservation appointment must last at least 1 hour.";
    public static final String RESERVATION_APPOINTMENT_START_TIME_CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE = "Reservation appointment must begin at least 24 hours upfront.";
    public static final String DATE_FORMAT_CONSTRAINT_VIOLATION_EXCEPTION_MESSAGE = "Date must be in format yyyy-MM-ddThh:mm:ss and valid.";
    public static final String RESERVATION_APPOINTMENT_NOT_FOUND_EXCEPTION_MESSAGE = "Reservation appointment with ID %s does not exist.";
    public static final String RESERVATION_NOT_FOUND_EXCEPTION_MESSAGE = "Reservation with ID %s not found.";
    public static final String RESERVATION_OWNERSHIP_EXCEPTION_MESSAGE = "Reservation with ID %s is not owned by you.";
    public static final String RESERVATION_CREATION_FORBIDDEN_EXCEPTION_MESSAGE = "You need to own at least one property in ARBC system to make a reservation.";
    public static final String NOT_AUTHORIZED_MESSAGE = "You are not authorized to access %s with method %s.";
    public static final String NOT_AUTHENTICATED_MESSAGE = "You are not authenticated to access %s with method %s.";
    public static final String SERVER_ERROR_EXCEPTION_MESSAGE = "Server error occurred, please try again...";

    // Residence Management API paths and properties
    public static final String RESIDENCE_BASE_URI = "http://localhost";
    public static final String RESIDENCE_PORT = "8080";
    public static final String RESIDENCE_CONTEXT_PATH = "/residence-management-1.0-SNAPSHOT";
    public static final String RETRIEVE_RESIDENCES_BY_RESIDENTS_EMAIL = "/api/residences/search/residence-numbers";

    // Exchange Names
    public static final String SPORTS_CENTER_EXCHANGE = "sports_center_exchange";

    // Queue Names
    public static final String QUEUE_RESERVATION_CREATION = "reservation_creation";
    public static final String QUEUE_RESERVATION_DELETION = "reservation_deletion";
    public static final String QUEUE_RESERVATION_REMINDER = "reservation_reminder";

    // Routing Keys
    public static final String ROUTING_KEY_RESERVATION_CREATION = "reservation.creation";
    public static final String ROUTING_KEY_RESERVATION_DELETION = "reservation.deletion";
    public static final String ROUTING_KEY_RESERVATION_REMINDER = "reservation.reminder";

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

    // Mosquitto properties and json request template
    public static final String MQTT_BROKER = "tcp://localhost";
    public static final String MQTT_PORT = "1883";
    public static final String MQTT_USERNAME = "admin";
    public static final String MQTT_PASSWORD = "admin";
    public static final String MQTT_CLIENT_ID = "swc-management";
    public static final String PUBLISHER_TOPIC_INTERIOR_HEATER = "swc_iot_device/interior_heater";
    public static final String PUBLISHER_TOPIC_ORDINARY_LIGHTS = "swc_iot_device/ordinary_lights";
    public static final String PUBLISHER_TOPIC_AIR_CONDITIONER = "swc_iot_device/air_conditioner";
    public static final String PUBLISHER_TOPIC_ROOF = "swc_iot_device/roof";
    public static final String PUBLISHER_TOPIC_CHANGING_COLOR_LIGHTS = "swc_iot_device/changing_color_lights";
    public static final String PUBLISHER_TOPIC_WATER_PUMP = "swc_iot_device/water_pump";
    public static final String SUBSCRIBER_TOPIC_INTERIOR_HEATER = "swc-management/interior_heater";
    public static final String SUBSCRIBER_TOPIC_ORDINARY_LIGHTS = "swc-management/ordinary_lights";
    public static final String SUBSCRIBER_TOPIC_AIR_CONDITIONER = "swc-management/air_conditioner";
    public static final String SUBSCRIBER_TOPIC_ROOF = "swc-management/roof";
    public static final String SUBSCRIBER_TOPIC_CHANGING_COLOR_LIGHTS = "swc-management/changing_color_lights";
    public static final String SUBSCRIBER_TOPIC_WATER_PUMP = "swc-management/water_pump";
}
