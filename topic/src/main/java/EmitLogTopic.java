import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class EmitLogTopic {

    private static final String EXCHANGE_NAME = "logs_topic";

    public static void main(String[] args)
            throws java.io.IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //Declaring named exchange with type DIRECT that will look for
        // binding key and routing key and match them */
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String routingKeyThatWontWork = "quick.white.rabbit";
        String routingKeyThatWorks = "quick.orange.rabbit";

        String message = getMessage(args);

        //Publishing message to exchange to unnamed queue
        channel.basicPublish(EXCHANGE_NAME, routingKeyThatWorks, null, message.getBytes());
        System.out.println(" [x] Sent " + routingKeyThatWorks + " '"+  message + "'");

        channel.close();
        connection.close();
    }

    private static String getMessage(String[] strings){
        if (strings.length < 2)
            return "Hello World!";
        return joinStrings(strings, " ", 1);
    }

    private static String joinStrings(String[] strings, String delimiter, int startIndex) {
        int length = strings.length;
        if (length == 0 ) return "";
        if (length < startIndex ) return "";
        StringBuilder words = new StringBuilder(strings[startIndex]);
        for (int i = startIndex + 1; i < length; i++) {
            words.append(delimiter).append(strings[i]);
        }
        return words.toString();
    }
}
