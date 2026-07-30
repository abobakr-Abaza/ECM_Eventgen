package com.event.models;

public class SubscriptionEvent {

	private String caseType;
    private EventConfig eventConfig;

    public String getCaseType() {
        return caseType;
    }
    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }
    public EventConfig getEventConfig() {
        return eventConfig;
    }
    public void setEventConfig(EventConfig eventConfig) {
        this.eventConfig = eventConfig;
    }
    
}
