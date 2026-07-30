package com.event;
import java.io.File;
import java.io.IOException;
import com.event.models.SubscriptionDefination;
import com.event.service.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {

    public static void main(String[] args) {
        try {
            // Default config file path (relative to project)
            String configPath = "D:\\Event_Generated\\Full_Proj\\ECM_Eventgen\\src\\JsonFiles\\DataCap.json";

            // Allow command-line argument to override config path
            if (args.length > 0) {
                configPath = args[0];
            }

            ObjectMapper mapper = new ObjectMapper();

            SubscriptionDefination subscriptionDefination = mapper.readValue(
                    new File(configPath),
                    SubscriptionDefination.class
            );

            System.out.println("Generating " + subscriptionDefination.getSubscriptionName() + " subscription ...");
            SubscriptionService.processSubscription(subscriptionDefination);

        } catch (IOException e) {
            System.err.println("Failed to read config.json: " + e.getMessage());
            e.printStackTrace();
        }
    }
}