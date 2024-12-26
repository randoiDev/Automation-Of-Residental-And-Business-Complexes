package com.arbc.notificationsservice;

public class Constants {

    // Email templates and subjects
    public static final String USER_ACCOUNT_CREATED_SUBJECT = "User account creation";
    public static final String USER_ACCOUNT_DELETED_SUBJECT = "User account deletion";
    public static final String USER_ACCOUNT_PASSWORD_RENEWAL_SUBJECT = "User account password reset";
    public static final String RESIDENCE_ADDED_TO_ACCOUNT_SUBJECT = "Residence added to account";
    public static final String RESIDENCE_REMOVED_FROM_ACCOUNT_SUBJECT = "Residence removed from account";
    public static final String RESERVATION_CREATED_SUBJECT = "Reservation created";
    public static final String RESERVATION_DELETED_SUBJECT = "Reservation deleted";
    public static final String RESERVATION_REMINDER_SUBJECT = "Reservation reminder";
    public static final String ADDING_RESIDENCE_FILE = "adding-residence.html";
    public static final String REMOVING_RESIDENCE_FILE = "removing-residence.html";
    public static final String PASSWORD_RENEWAL_FILE = "password-renewal.html";
    public static final String RESERVATION_CREATION_FILE = "reservation-creation.html";
    public static final String RESERVATION_REMOVAL_FILE = "reservation-removal.html";
    public static final String RESERVATION_REMINDER_FILE = "reservation-reminder.html";
    public static final String ADDING_WORKER_FILE = "worker-creation.html";
    public static final String ADDING_RESIDENT_FILE = "resident-creation.html";
    public static final String REMOVING_RESIDENT_FILE = "resident-removal.html";

    // Exchange Names
    public static final String SPORTS_CENTER_EXCHANGE = "sports_center_exchange";
    public static final String USER_MANAGEMENT_EXCHANGE = "user_management_exchange";
    public static final String RESIDENCE_MANAGEMENT_EXCHANGE = "residence_management_exchange";

    // Queue Names
    public static final String QUEUE_RESERVATION_CREATION = "reservation_creation";
    public static final String QUEUE_RESERVATION_DELETION = "reservation_deletion";
    public static final String QUEUE_RESERVATION_REMINDER = "reservation_reminder";
    public static final String QUEUE_CREATE_USER_ACCOUNT = "create_user_account";
    public static final String QUEUE_DELETE_USER_ACCOUNT = "delete_user_account";
    public static final String QUEUE_RENEW_USER_ACCOUNT_PASSWORD = "renew_user_account_password";
    public static final String QUEUE_ADD_RESIDENCE_TO_ACCOUNT = "add_residence_to_account";
    public static final String QUEUE_REMOVE_RESIDENCE_FROM_ACCOUNT = "remove_residence_from_account";

    // Routing Keys
    public static final String ROUTING_KEY_RESERVATION_CREATION = "reservation.creation";
    public static final String ROUTING_KEY_RESERVATION_DELETION = "reservation.deletion";
    public static final String ROUTING_KEY_RESERVATION_REMINDER = "reservation.reminder";
    public static final String ROUTING_KEY_CREATE_USER_ACCOUNT = "account.create";
    public static final String ROUTING_KEY_DELETE_USER_ACCOUNT = "account.delete";
    public static final String ROUTING_KEY_RENEW_USER_ACCOUNT_PASSWORD = "account.renew";
    public static final String ROUTING_KEY_ADD_RESIDENCE = "account.add_residence";
    public static final String ROUTING_KEY_REMOVE_RESIDENCE = "account.remove_residence";

    // RabbitMQ properties
    public static final String RABBIT_HOST = "localhost";
    public static final int RABBIT_PORT = 5672;
    public static final String RABBIT_USERNAME = "guest";
    public static final String RABBIT_PASSWORD = "guest";

    // SMTP properties
    public static final String SMTP_HOST = "smtp.gmail.com";
    public static final String SMTP_PORT = "587";
    public static final String EMAIL = "arbc.management@gmail.com";
    public static final String PASSWORD = "eekssnynxidqqnqf";
    public static final boolean SMTP_START_TLS_ENABLED = true;
    public static final boolean SMTP_AUTH = true;
}
