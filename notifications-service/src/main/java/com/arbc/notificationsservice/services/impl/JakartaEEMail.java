package com.arbc.notificationsservice.services.impl;

import com.arbc.notificationsservice.services.spec.EmailService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import static com.arbc.notificationsservice.Constants.*;

@Stateless
public class JakartaEEMail implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JakartaEEMail.class);
    private Session session;

    @PostConstruct
    public void setBasicEmailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", SMTP_AUTH);
        props.put("mail.smtp.starttls.enable", SMTP_START_TLS_ENABLED);
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });
    }

    @Override
    public void sendUserCreationEmail(String name, String username, String password, String recipientEmail) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(USER_ACCOUNT_CREATED_SUBJECT);
            String htmlTemplate = readHtmlTemplate(username == null ? ADDING_RESIDENT_FILE : ADDING_WORKER_FILE);
            String body = String.format(htmlTemplate, name, username == null ? recipientEmail : username, password);
            message.setContent(body, "text/html");
            Transport.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Email for user creation could not be sent on {}", recipientEmail, e);
        }
    }

    @Override
    public void sendUserRemovalEmail(String name, String recipientEmail) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(USER_ACCOUNT_DELETED_SUBJECT);
            String htmlTemplate = readHtmlTemplate(REMOVING_RESIDENT_FILE);
            String body = String.format(htmlTemplate, name);
            message.setContent(body, "text/html");
            Transport.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Email for user deletion could not be sent on {}", recipientEmail, e);
        }
    }

    @Override
    public void sendUserRenewedPassword(String name, String password, String recipientEmail) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(USER_ACCOUNT_PASSWORD_RENEWAL_SUBJECT);
            String htmlTemplate = readHtmlTemplate(PASSWORD_RENEWAL_FILE);
            String body = String.format(htmlTemplate, name, password);
            message.setContent(body, "text/html");
            Transport.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Email for resident account password renewal could not be sent on {}", recipientEmail, e);
        }
    }

    @Override
    public void sendResidenceAdditionEmail(String name, String residenceNumbers, String recipientEmail) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(RESIDENCE_ADDED_TO_ACCOUNT_SUBJECT);
            String htmlTemplate = readHtmlTemplate(ADDING_RESIDENCE_FILE);
            String body = String.format(htmlTemplate, name, residenceNumbers);
            message.setContent(body, "text/html");
            Transport.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Email for residence addition to account could not be sent on {}", recipientEmail, e);
        }
    }

    @Override
    public void sendResidenceRemovalEmail(String name, String residenceNumbers, String recipientEmail) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(RESIDENCE_REMOVED_FROM_ACCOUNT_SUBJECT);
            String htmlTemplate = readHtmlTemplate(REMOVING_RESIDENCE_FILE);
            String body = String.format(htmlTemplate, name, residenceNumbers);
            message.setContent(body, "text/html");
            Transport.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Email for residence removal from account could not be sent on {}", recipientEmail, e);
        }
    }

    @Override
    public void sendReservationCreationEmail(String name, String resource, String startTime, String endTime, String recipientEmail, String code) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(RESERVATION_CREATED_SUBJECT);
            String htmlTemplate = readHtmlTemplate(RESERVATION_CREATION_FILE);
            String body = String.format(htmlTemplate, name, resource, startTime, endTime, code);
            message.setContent(body, "text/html");
            Transport.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Email for resource reservation creation could not be sent on {}", recipientEmail, e);
        }
    }

    @Override
    public void sendReservationRemovalEmail(String name, String resource, String startTime, String endTime, String recipientEmail) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(RESERVATION_DELETED_SUBJECT);
            String htmlTemplate = readHtmlTemplate(RESERVATION_REMOVAL_FILE);
            String body = String.format(htmlTemplate, name, resource, startTime, endTime);
            message.setContent(body, "text/html");
            Transport.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Email for resource reservation removal could not be sent on {}", recipientEmail, e);
        }
    }

    @Override
    public void sendReservationReminderEmail(String name, String resource, String startTime, String endTime, String recipientEmail) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(RESERVATION_REMINDER_SUBJECT);
            String htmlTemplate = readHtmlTemplate(RESERVATION_REMINDER_FILE);
            String body = String.format(htmlTemplate, name, resource, startTime, endTime);
            message.setContent(body, "text/html");
            Transport.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Email for resource reservation reminder could not be sent on {}", recipientEmail, e);
        }
    }

    private String readHtmlTemplate(String templateName) {
        StringBuilder content = new StringBuilder();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("html-templates/" + templateName)) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Could not load content from {} file", templateName, e);
        }
        return content.toString();
    }

}
