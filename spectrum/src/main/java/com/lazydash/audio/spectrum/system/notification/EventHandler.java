package com.lazydash.audio.spectrum.system.notification;

public class EventHandler {
    private EventEnum eventEnum;
    private EventCallback eventCallback;

    public EventHandler(EventEnum eventEnum, EventCallback eventCallback) {
        this.eventEnum = eventEnum;
        this.eventCallback = eventCallback;
    }

    public EventEnum getEventEnum() {
        return eventEnum;
    }

    public void setEventEnum(EventEnum eventEnum) {
        this.eventEnum = eventEnum;
    }

    public EventCallback getEventCallback() {
        return eventCallback;
    }

    public void setEventCallback(EventCallback eventCallback) {
        this.eventCallback = eventCallback;
    }
}
