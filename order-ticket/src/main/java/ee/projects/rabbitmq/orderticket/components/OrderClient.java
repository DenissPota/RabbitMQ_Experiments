package ee.projects.rabbitmq.orderticket.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.projects.rabbitmq.orderticket.message.OrderMessage;
import ee.projects.rabbitmq.orderticket.message.Status;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class OrderClient {

    @Autowired
    private RabbitTemplate template;

    @Autowired
    private TopicExchange topicExchange;

    private List<String> routingKeys =
            Arrays.asList("order.ticket.concert.tallinn",
                    "order.ticket.concert.tartu",
                    "booking.ticket.concert.tallinn,",
                    "announce.ticket.concert.valga");


    public OrderMessage setupMessage() {
        OrderMessage message = new OrderMessage();
        message.setUuid(UUID.randomUUID().toString());
        message.setStatus(Status.SENT.toString());
        message.setName("Dancing Penguins");
        message.setDesc("Concert of famous dancing penguins performers");

        return message;

    }

    @Scheduled(fixedDelay = 2000, initialDelay = 1000)
    public void sendMessage() {
        try {
            String orderMessage = objectToJSON(setupMessage());
            String routingKey = getRandom(routingKeys);
            System.out.println("Sent " + orderMessage + " | with routing key: " + routingKey);
            String response = (String) template.convertSendAndReceive(topicExchange.getName(), routingKey, orderMessage);
            System.out.println("Got back from server: '" + response + "'");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private String objectToJSON(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);

    }


    private static String getRandom(List<String> array) {
        Random r = new Random();
        return array.get(r.nextInt(array.size()));
    }
}
