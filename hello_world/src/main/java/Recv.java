import com.rabbitmq.client.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeoutException;

public class Recv {

    private final static String QUEUE_NAME = "hello";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");


    public static void main(String[] argv) throws java.io.IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        /*We declare the queue here because we might start the consumer before the publisher,
        we want to make sure the queue exists before we try to consume messages from it. */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");


        /*Provide a callback in the form of an object that will buffer the messages until we're ready to use them.
        That is what a DefaultConsumer subclass does.*/
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                System.out.println(sdf.format(timestamp) + " [x] Received '" + message + "'");
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);
    }

}
