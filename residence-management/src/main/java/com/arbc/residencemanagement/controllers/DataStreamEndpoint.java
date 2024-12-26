package com.arbc.residencemanagement.controllers;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/notifications")
public class DataStreamEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataStreamEndpoint.class);
    private static final Map<String, Session> activeSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        String residenceNumber = getResidenceNumber(session); // You need to define how to extract the client ID
        activeSessions.put(residenceNumber, session);
        LOGGER.info("WebSocket opened with session id {} and residence number {}", session.getId(), residenceNumber);
    }

    @OnClose
    public void onClose(Session session) {
        String residenceNumber = getResidenceNumber(session); // You need to define how to extract the client ID
        activeSessions.remove(residenceNumber);
        LOGGER.info("WebSocket closed with session id {} and residence number {}", session.getId(), residenceNumber);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
    }

    // Send data to a specific client based on the client ID
    public static void sendDataToClient(String residenceNumber, String data) {
        Session session = activeSessions.get(residenceNumber);
        if (session != null) {
            try {
                session.getBasicRemote().sendText(data);
            } catch (IOException e) {
                LOGGER.info("WebSocket failed to send message to client with session id {}", session.getId());
            }
        }
    }

    // Helper method to extract a unique client ID (you can customize this based on your needs)
    private String getResidenceNumber(Session session) {

        // For example, extract the client ID from session's query parameters or headers
        return session.getRequestParameterMap().get("residence-number").get(0);
    }
}
