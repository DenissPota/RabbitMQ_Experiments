import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeoutException;

public class NewTask {


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
        boolean durable = true; //queue will be not lost, if rabbitmq server stops, saved on file disk
        channel.queueDeclare("task_queue", durable, false, false, null);
        String message = getMessage(args);
        channel.basicPublish("", "task_queue", MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        System.out.println(sdf.format(timestamp) + " Producer sent message '" + message + "' to queue");

        channel.close();
        connection.close();
    }

    private static String getMessage(String[] strings){
        if (strings.length < 1)
            return "Hello World!";
        return joinStrings(strings, " ");
    }

    private static String joinStrings(String[] strings, String delimiter) {
        int length = strings.length;
        if (length == 0) return "";
        StringBuilder words = new StringBuilder(strings[0]);
        for (int i = 1; i < length; i++) {
            words.append(delimiter).append(strings[i]);
        }
        return words.toString();
    }
}
