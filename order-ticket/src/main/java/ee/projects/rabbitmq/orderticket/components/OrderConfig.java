package ee.projects.rabbitmq.orderticket.components;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile({"order-ticket"})
@Configuration
public class OrderConfig {


    @Profile("order-client")
    private static class OrderClientConfig {

        /*Declaring exchange in both places because
        this operation is idempotent and makes sure that exchange is present.*/
        @Bean
        public TopicExchange exchange() {
            return new TopicExchange("order-exchange");
        }

        @Bean
        OrderClient client() {
            return new OrderClient();
        }
    }

    @Profile("order-server")
    private static class OrderServerConfig {

        @Bean
        public TopicExchange exchange() {
            return new TopicExchange("order-exchange");
        }

        @Bean
        public Queue queue() {
            return new Queue("order-queue");
        }

        @Bean
        public Binding binding(TopicExchange exchange, Queue queue) {
            return BindingBuilder.bind(queue).to(exchange).with("order.#");
        }

        @Bean
        OrderServer server() {
            return new OrderServer();
        }
    }
}
