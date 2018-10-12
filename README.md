# RabbitMQ_Experiments
Playground for RabbitMQ related stuff.

* hello_world - [Basic point-to-point](https://www.rabbitmq.com/tutorials/tutorial-one-java.html)
* work_queues - [Distributing tasks among workers (the competing consumers pattern)](https://www.rabbitmq.com/tutorials/tutorial-two-java.html)
* publish_subscribe - [Basic fanout exchange](https://www.rabbitmq.com/tutorials/tutorial-three-java.html)
* rules - [Basic direct exchange](https://www.rabbitmq.com/tutorials/tutorial-four-java.html)
* topic - [Basic topic exchange](https://www.rabbitmq.com/tutorials/tutorial-five-java.html)
* rpc - [RPC task with reply to queue](https://www.rabbitmq.com/tutorials/tutorial-six-java.html) 

###Proof of concept


**order-ticket** - Spring boot + RabbitMQ RPC 

*How to run:*

1) Maven package order-ticket project
2) Open 2 terminal windows that will simulate Client and Server
3) Change directory to order-ticket
4) In Server window terminal type: java -jar target/order-ticket-0.0.1-SNAPSHOT.jar --spring.profiles.active=order-ticket,order-server
5) In Client window terminal type: java -jar target/order-ticket-0.0.1-SNAPSHOT.jar --spring.profiles.active=order-ticket,order-client
6) Enjoy!

