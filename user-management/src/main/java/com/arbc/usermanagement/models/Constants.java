package com.arbc.usermanagement.models;

public class Constants {

    // Regexes and constraint violation messages for responses on CRUD operations on residents
    public static final String NAME_FIELD_REQUIRED_VIOLATION_MESSAGE = "Name is required.";
    public static final String NAME_FIELD_REGEX_VIOLATION_MESSAGE = "Name must contain only alphabet letters and white spaces.";
    public static final String SURNAME_FIELD_REQUIRED_VIOLATION_MESSAGE = "Surname is required.";
    public static final String SURNAME_FIELD_REGEX_VIOLATION_MESSAGE = "Surname must contain only alphabet letters and white spaces.";
    public static final String NAME_SURNAME_REGEX = "^[A-Za-z\\\\s]+$";
    public static final String EMAIL_FIELD_REQUIRED_VIOLATION_MESSAGE = "Email is required.";
    public static final String EMAIL_FIELD_REGEX_VIOLATION_MESSAGE = "Invalid email address format.";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$";
    public static final String USERNAME_FIELD_REQUIRED_VIOLATION_MESSAGE = "Username is required.";
    public static final String MOBILE_NUMBER_FIELD_REQUIRED_VIOLATION_MESSAGE = "Mobile number is required.";
    public static final String MOBILE_NUMBER_FIELD_REGEX_VIOLATION_MESSAGE = "Mobile number must in serbian number format.";
    public static final String MOBILE_NUMBER_REGEX = "^((\\+3816\\d{7,8})|(06\\d{7,8}))$";
    public static final String PASSWORD_FIELD_CONSTRAINT_VIOLATION_MESSAGE = "Password must be between 8 and 16 characters.";
    public static final String PASSWORD_FIELD_REQUIRED_VIOLATION_MESSAGE = "Password is required.";
    public static final String OLD_PASSWORD_FIELD_REQUIRED_VIOLATION_MESSAGE = "Old password is required.";
    public static final String NEW_PASSWORD_FIELD_REQUIRED_VIOLATION_MESSAGE = "New password is required.";
    public static final String PASSWORD_REGEX = "^.{8,16}$";
    public static final String FIELD_VALUE_ALREADY_IN_USER_VIOLATION_MESSAGE = "Value %s is already in use.";
    public static final String NOT_AUTHORIZED_MESSAGE = "You are not authorized to access %s with method %s.";
    public static final String NOT_AUTHENTICATED_MESSAGE = "You are not authenticated to access %s with method %s.";

    //Tag constants
    public static final String USERNAME_TAG = "username";
    public static final String EMAIL_TAG = "email";
    public static final String ID_TAG = "id";

    // Exception messages
    public static final String USER_CREATION_EXCEPTION_MESSAGE = "User account could not be created, please try again...";
    public static final String USER_DELETION_EXCEPTION_MESSAGE = "User account could not be deleted, please try again...";
    public static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "User account with %s '%s' does not exist.";
    public static final String USER_HAS_ATTACHED_RESIDENCES_EXCEPTION_MESSAGE = "User account with %s '%s' still has some attached residences.";
    public static final String USER_PASSWORD_RENEWAL_EXCEPTION_MESSAGE = "Password could not be renewed, please try again...";
    public static final String USER_PASSWORD_UPDATE_EXCEPTION_MESSAGE = "Password could not be updated, please try again...";
    public static final String USER_BANNED_FOR_RESERVATIONS_UPDATE_EXCEPTION_MESSAGE = "Banned for reservations field could not be updated, please try again...";
    public static final String USER_MOBILE_NUMBER_ALREADY_PRESENT_UPDATE_EXCEPTION_MESSAGE = "Specified mobile number is already present.";
    public static final String USER_MOBILE_NUMBER_UPDATE_EXCEPTION_MESSAGE = "Mobile number could not be updated, please try again....";

    public static final String USER_PASSWORD_UPDATE_UNAUTHORIZED_EXCEPTION_MESSAGE = "Wrong current password.";
    public static final String USER_LOGIN_EXCEPTION_MESSAGE = "Wrong credentials.";
    public static final String SERVER_ERROR_EXCEPTION_MESSAGE = "Server error occurred, please try again...";

    // Residences Management API paths and properties
    public static final String RESIDENCE_BASE_URI = "http://localhost";
    public static final String RESIDENCE_PORT = "8080";
    public static final String RESIDENCE_CONTEXT_PATH = "/residence-management-1.0-SNAPSHOT";
    public static final String RETRIEVE_RESIDENCES_BY_RESIDENTS_EMAIL = "/api/residences/search/residence-numbers-exact-match";

    // Exchange Names
    public static final String USER_MANAGEMENT_EXCHANGE = "user_management_exchange";

    // Queue Names
    public static final String QUEUE_CREATE_USER_ACCOUNT = "create_user_account";
    public static final String QUEUE_DELETE_USER_ACCOUNT = "delete_user_account";
    public static final String QUEUE_RENEW_USER_ACCOUNT_PASSWORD = "renew_user_account_password";

    // Routing Keys
    public static final String ROUTING_KEY_CREATE_USER_ACCOUNT = "account.create";
    public static final String ROUTING_KEY_DELETE_USER_ACCOUNT = "account.delete";
    public static final String ROUTING_KEY_RENEW_USER_ACCOUNT_PASSWORD = "account.renew";

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

}
