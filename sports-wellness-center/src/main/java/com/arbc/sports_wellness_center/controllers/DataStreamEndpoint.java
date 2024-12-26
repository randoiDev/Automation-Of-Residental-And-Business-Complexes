package com.arbc.sports_wellness_center.controllers;

import com.arbc.sports_wellness_center.models.enums.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/notifications")
public class DataStreamEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataStreamEndpoint.class);
    private static final Map<Resource, List<Session>> activeSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        Resource resource = getResource(session);
        activeSessions.computeIfAbsent(resource, k -> new ArrayList<>()).add(session);
        LOGGER.info("WebSocket opened with session id {}", session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        Resource resource = getResource(session);
        activeSessions.get(resource).remove(session);
        LOGGER.info("WebSocket closed with session id {}", session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
    }

    // Send data to a specific client based on the client ID
    public static void sendDataToClients(Resource resource, String data) {
        System.out.println(data);
        List<Session> sessions = activeSessions.get(resource);

        if (sessions != null) {
            for (Session session : sessions) {
                try {
                    session.getBasicRemote().sendText(data);
                } catch (IOException e) {
                    LOGGER.error("WebSocket failed to send message to client with session id {}", session.getId(), e);
                }
            }
        } else {
            LOGGER.info("No active sessions found for resource {}", resource);
        }
    }

    // Helper method to extract a unique client ID (you can customize this based on your needs)
    private Resource getResource(Session session) {

        // For example, extract the client ID from session's query parameters or headers
        return Resource.valueOf(session.getRequestParameterMap().get("resource").get(0));
    }
}
