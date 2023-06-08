import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import config.RabbitMQConfig;
import model.CustomerCharge;
import model.StationCustomer;
import service.DatabaseService;
import service.RabbitMQService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class Main {

    private final static String DATA_GATHERING_QUEUE_NAME = "create-data-gathering-job";


    public static void main(String[] args) {
        DatabaseService databaseService = new DatabaseService();
        RabbitMQService rabbitMQService = new RabbitMQService(databaseService);

        try {
            Channel channel = RabbitMQConfig.setupRabbitMQChannel();
            rabbitMQService.consumeMessage(channel, DATA_GATHERING_QUEUE_NAME, (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + message + "'");

                StationCustomer stationCustomer;
                try{
                    stationCustomer = new ObjectMapper().readValue(message, StationCustomer.class);
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Unable to parse message to StationCustomer object: " + message);
                    return;  // Skip processing this message
                }
                String dbUrl = stationCustomer.getStation().getDbUrl();
                Integer customerId = stationCustomer.getCustomerId();
                Integer stationId = stationCustomer.getStation().getId();
                String uid = stationCustomer.getUid();

                Float sumOfKwhFromCustomer = databaseService.getSumOfKwhFromCustomer(dbUrl, customerId);

                CustomerCharge customerCharge = new CustomerCharge(customerId, stationId, sumOfKwhFromCustomer, uid);

                rabbitMQService.sendCustomerChargeMessage(channel, customerCharge);

            });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

}
