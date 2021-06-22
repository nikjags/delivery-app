package ru.study.delivery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.study.delivery.delivery_utils.Deliverer;
import ru.study.delivery.jms_classes.RequestConsumer;
import ru.study.delivery.jms_classes.ResponseProducer;

import javax.jms.*;
import java.util.ArrayList;

public class Main {
    private static String activeMqHostName = "localhost"; //= localhost if host name is not passed to the args

    private static final String USER_NAME = "admin";
    private static final String PASSWORD = "admin";
    private static final String REQUEST_QUEUE_NAME = "delivery-request-queue";
    private static final String RESPONSE_QUEUE_NAME = "delivery-response-queue";

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        getActiveMqHostName(args);

        ActiveMQConnectionFactory connectionFactory = configAndGetConnection();

        try {
            Connection connection = connectionFactory.createConnection();
            logger.info("CONNECTION CREATED");

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            logger.info("SESSION CREATED");

            Queue responseQueue = session.createQueue(RESPONSE_QUEUE_NAME);
            ResponseProducer responseProducer = new ResponseProducer(session, responseQueue);

            Deliverer deliverer = new Deliverer(responseProducer);

            Queue requestQueue = session.createQueue(REQUEST_QUEUE_NAME);
            MessageConsumer consumer = session.createConsumer(requestQueue);
            RequestConsumer requestConsumer = new RequestConsumer(consumer, deliverer, objectMapper);
            Thread requestConsumerThread = new Thread(requestConsumer);

            requestConsumerThread.start();

            connection.start();
        } catch (JMSException e) {
            logger.error("ERROR HAS OCCURRED DURING GETTING A CONNECTION");
            logger.error(e.toString());
        }
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private static void getActiveMqHostName(String[] args) {
        System.out.println("args[0]: " + args[0]);
        if (args.length == 1 && !args[0].equals("-classpath")) {
            activeMqHostName = args[0];
        }
    }

    private static ActiveMQConnectionFactory configAndGetConnection() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL("tcp://" + activeMqHostName + ":61616");
        connectionFactory.setUserName(USER_NAME);
        connectionFactory.setPassword(PASSWORD);

        connectionFactory.setTrustAllPackages(false);
        connectionFactory.setTrustedPackages(new ArrayList<>());

        logger.info("ALL PROPERTIES ARE SET");
        return connectionFactory;
    }
}
