package ru.study.delivery.jms_classes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.study.delivery.delivery_utils.Deliverer;
import ru.study.delivery.entities.Order;

import javax.jms.*;
import java.util.Objects;

public class RequestConsumer implements Runnable {
    private static final int RECEIVE_TIMEOUT = 10_000;

    private final MessageConsumer messageConsumer;
    private final Deliverer deliverer;
    private final ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(RequestConsumer.class);

    private boolean isStopped = false;

    public RequestConsumer(MessageConsumer messageConsumer, Deliverer deliverer, ObjectMapper objectMapper) {
        this.deliverer = deliverer;
        this.messageConsumer = messageConsumer;
        this.objectMapper = objectMapper;
    }

    public void run() {
        logger.info("REQUEST CONSUMER IS UP");

        while (!isStopped) {
            try {
                TextMessage message = (TextMessage) messageConsumer.receive(RECEIVE_TIMEOUT);
                if (Objects.isNull(message)) {
                    continue;
                }

                String deliveryRequestText = message.getText();

                logger.info("RECEIVED MESSAGE: {}", deliveryRequestText);

                Order orderToDeliver = objectMapper.readValue(deliveryRequestText, Order.class);

                deliverer.sendToDeliver(orderToDeliver);
            } catch (JMSException e) {
                logger.error("AN ERROR HAS OCCURRED DURING GETTING A MESSAGE");
                logger.error(e.toString());
            } catch (JsonProcessingException e) {
                logger.error("AN ERROR HAS OCCURRED DURING MESSAGE PARSING");
                logger.error(e.toString());
            }
        }
        try {
            messageConsumer.close();
            logger.info("REQUEST CONSUMER IS CLOSED");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void stopConsuming() {
        isStopped = true;
    }

}
