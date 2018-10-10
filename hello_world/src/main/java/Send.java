import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Send {

    private final static String QUEUE_NAME = "hello";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");


    public static void main(String[] args) throws IOException, TimeoutException {
        //Connect to a broker on the local machine - hence the localhost.
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        //create a channel, which is where most of the API for getting things done resides.
        Channel channel = connection.createChannel();

        //Declaring a queue is idempotent - it will only be created if it doesn't exist already
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "I have passed through broker";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        System.out.println(sdf.format(timestamp) + " Producer sent message '" + message + "' to queue");

        channel.close();
        connection.close();
    }
}
