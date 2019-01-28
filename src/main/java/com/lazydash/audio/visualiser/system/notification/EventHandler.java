package com.lazydash.audio.visualiser.system.notification;

public class EventHandler {
    private String handlerId;
    private String eventId;
    private EventCallback eventCallback;

    public EventHandler(String handlerId, String eventId, EventCallback eventCallback) {
        this.handlerId = handlerId;
        this.eventId = eventId;
        this.eventCallback = eventCallback;
    }

    public String getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(String handlerId) {
        this.handlerId = handlerId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public EventCallback getEventCallback() {
        return eventCallback;
    }

    public void setEventCallback(EventCallback eventCallback) {
        this.eventCallback = eventCallback;
    }
}
