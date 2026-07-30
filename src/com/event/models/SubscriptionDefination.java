package com.event.models;

import java.util.List;

public class SubscriptionDefination {

    private String subscriptionName;
    private List<SubscriptionEvent> subscriptionEvent;

    public String getSubscriptionName() {
        return subscriptionName;
    }
    public void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    public List<SubscriptionEvent> getSubscriptionEvent() {
        return subscriptionEvent;
    }

    public void setSubscriptionEvent(List<SubscriptionEvent> subscriptionEvent) {
        this.subscriptionEvent = subscriptionEvent;
    }

}




