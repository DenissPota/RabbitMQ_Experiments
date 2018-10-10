import com.rabbitmq.client.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeoutException;

public class Worker {

    private final static String QUEUE_NAME = "hello";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        /*We declare the queue here because we might start the consumer before the publisher,
        we want to make sure the queue exists before we try to consume messages from it. */
        boolean durable = true; //queue will be not lost, if rabbitmq server stops
        channel.queueDeclare("task_queue", durable, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        int prefetchCount = 1;
        channel.basicQos(prefetchCount); // Don't dispatch a new message to a worker until it has processed and acknowledged the previous one


    /*Provide a callback in the form of an object that will buffer the messages until we're ready to use them.
    That is what a DefaultConsumer subclass does.*/
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                System.out.println(sdf.format(timestamp) + " [x] Received '" + message + "'");

                try {
                    doWork(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println( " [x] Done");
                    /* If next line not declared. Messages will be redelivered when your client quits (which may look like random redelivery),
                    but RabbitMQ will eat more and more memory as it won't be able to release any unacked messages.
                    * */
                    channel.basicAck(envelope.getDeliveryTag(), false);

                }
            }
        };

        boolean autoAck = false; //In automatic acknowledgement mode, a message is considered to be successfully delivered immediately after it is sent
        channel.basicConsume("task_queue", autoAck, consumer);
    }

    private static void doWork(String task) throws InterruptedException {
        for (char ch : task.toCharArray()) {
            if (ch == '.') Thread.sleep(1000);
        }
    }

}

