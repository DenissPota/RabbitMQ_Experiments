package ee.projects.rabbitmq.orderticket.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.projects.rabbitmq.orderticket.message.OrderMessage;
import ee.projects.rabbitmq.orderticket.message.Status;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.io.IOException;

public class OrderServer {


    @RabbitListener(queues = "order-queue")
    public String processOrder(String orderMessageAsJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(" [x] Received request for " + orderMessageAsJson);

        OrderMessage orderMessage = mapper.readValue(orderMessageAsJson, OrderMessage.class);

        if (orderMessage.getStatus().equals(Status.SENT.toString())) {
            orderMessage.setStatus(Status.SUCCESSFUL.toString());
        }
        System.out.println(" [.] Returned " + orderMessage.getUuid() + " " + orderMessage.getStatus());
        return mapper.writeValueAsString(orderMessage);
    }


}
