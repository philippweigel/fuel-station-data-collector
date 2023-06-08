package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import model.CustomerCharge;
import model.StationCustomer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RabbitMQService {

    private final DatabaseService databaseService;
    private final static String CUSTOMER_CHARGE_QUEUE_NAME = "customer-charge";

    private ObjectMapper objectMapper = new ObjectMapper();

    public RabbitMQService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public void consumeMessage(Channel channel, String queueName, DeliverCallback deliverCallback) {
        try {
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
            });
        } catch (IOException e) {
            System.err.println("Failed to consume message from queue " + queueName);
            e.printStackTrace();
        }
    }

    public void sendCustomerChargeMessage(Channel channel, CustomerCharge customerCharge) throws IOException {
        String jsonCustomerCharge = objectMapper.writeValueAsString(customerCharge);
        channel.queueDeclare(CUSTOMER_CHARGE_QUEUE_NAME, false, false, false, null);
        channel.basicPublish("", CUSTOMER_CHARGE_QUEUE_NAME, null, jsonCustomerCharge.getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent '" + jsonCustomerCharge + "'");
    }

}
