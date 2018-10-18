import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;


public class RPCClient {

    private Connection connection;
    private Channel channel;
    private static final String QUEUE_NAME = "rpc_queue";

    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public String call(String message) throws IOException, InterruptedException {
        //Generate a unique correlationId
        final String corrId = UUID.randomUUID().toString();

        //Create a dedicated exclusive queue for the reply and subscribe to it.
        String replyQueueName = channel.queueDeclare().getQueue();
        String replyToQueue = "amq.rabbitmq.reply-to";
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyToQueue)
                .build();


        /*As we need to suspend main while server is working, we use BlockingQueue with capacity 1
        which means that we need to wait only for 1 response*/
        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

        //For reply-to, use replyToQueue and autoack true. Also consume should be before publish.
        String ctag = channel.basicConsume(replyToQueue, true, new DefaultConsumer(channel) {
            /*Handle delivery check whether arrived message has same as we put it in.
            if it is, it is put to BlockingQueue*/
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //if (properties.getCorrelationId().equals(corrId)) {
                    response.offer(new String(body, "UTF-8"));
                //}
            }
        });

        channel.basicPublish("", QUEUE_NAME, props, message.getBytes("UTF-8"));


        String result = response.take();
        channel.basicCancel(ctag);
        return result;
    }

    public void close() throws IOException {
        connection.close();
    }

    public static void main(String[] argv) {
        RPCClient fibonacciRpc = null;
        String response = null;
        try {
            fibonacciRpc = new RPCClient();

            for (int i = 0; i < 50; i++) {
                String i_str = Integer.toString(i);
                System.out.println(" [x] Requesting fib(" + i_str + ")");
                response = fibonacciRpc.call(i_str);
                System.out.println(" [.] Got '" + response + "'");
            }
        }
        catch  (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            if (fibonacciRpc!= null) {
                try {
                    fibonacciRpc.close();
                }
                catch (IOException ignored) {}
            }
        }
    }
}
