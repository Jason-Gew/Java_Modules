********** ActiveMQ Client **********

Basic JMS -- ActiveMQ client without integrating with Spring Boot framework. 
Support multi-threading and internal blocking queue. 
Provide cases for ActiveMQ Queue mode and Pub-Sub on Topic mode... 
Provide both String message and Byte message processing, can get messages from ActiveMQ-MQTT. If passing customize object, please write specific serialize and deserialize classes. 

Utilize Log4j2-Slf4j implemetation instead of direct using Log4j or Slf4j. 
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j-impl</artifactId>
    <version>2.10.0</version>
</dependency>

For advanced message processing with JMS-ActiveMQ, use ActiveMQ Client -- Spring Boot.
