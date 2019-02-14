package com.lazydash.audio.spectrum.system.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    private List<EventHandler> eventHandlers = new LinkedList<>();
    private static NotificationService notificationService = new NotificationService();

    private NotificationService() {
    }

    public static NotificationService getInstance() {
        return notificationService;
    }

    public synchronized void emit(EventEnum eventEnum, String message) {
        for (EventHandler eventHandler : eventHandlers) {
            if (eventHandler.getEventEnum().equals(eventEnum)) {
                eventHandler.getEventCallback().run(message);
            }
        }
    }

    public synchronized void register(EventEnum eventEnum, EventCallback eventCallback) {
        eventHandlers.add(new EventHandler(eventEnum, eventCallback));

        LOGGER.debug("eventHandlers size: " + eventHandlers.size());

    }

}
