package gew.kafka.logger;

import gew.kafka.logger.entity.LogLevel;
import gew.kafka.logger.kafka.Producer;
import gew.kafka.logger.kafka.ProducerBuilder;
import gew.kafka.logger.service.KafkaLogger;
import gew.kafka.logger.service.KafkaLoggerManager;


public class Application
{

    private static String kafkaBroker = "10.0.1.100:9092";
    private static String kafkaClientID = "C2M-Logger";
    private static String defaultTopic = "Jason-Test";

    private static KafkaLogger c2mLogger;

    public static void main(String[] args)
    {
        Application app = new Application();
        app.c2mLoggerInit();
        app.test();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void test()
    {
        c2mLogger.debug(this.getClass().getName(), "test", "Hello World 2018-03-02");
    }

    private void c2mLoggerInit()
    {
        Producer producer = null;
        try{
            producer = new ProducerBuilder()
                    .setServer(kafkaBroker)
                    .setClientId(kafkaClientID)
                    .setAcknowledge("0")
                    .setTopic(defaultTopic)
                    .setRetries(0)
                    .setSerializeClass("org.apache.kafka.common.serialization.StringSerializer")
                    .build();
            producer.init();
        } catch (Exception err) {
            System.err.println("Kafka Producer Setup Error: " + err.getMessage());
        }

        KafkaLoggerManager.setProducer(producer);
        KafkaLoggerManager.setPORT(1883);
        KafkaLoggerManager.setRootLevel(LogLevel.INFO);
        c2mLogger = KafkaLoggerManager.getInstance();
        c2mLogger.getSystemInfo();
        c2mLogger.info(this.getClass().getName(), "main", "System Started!");
    }


}
