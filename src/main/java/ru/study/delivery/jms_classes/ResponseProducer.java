package ru.study.delivery.jms_classes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.study.delivery.entities.Order;

import javax.jms.*;
import java.util.Objects;

public class ResponseProducer {
    private static final String ORDER_ID_PROPERTY_NAME = "orderId";
    private static final String IS_DELIVERED_PROPERTY_NAME = "isDelivered";

    private final Session session;

    private final Logger logger = LoggerFactory.getLogger(ResponseProducer.class);

    private MessageProducer messageProducer;

    public ResponseProducer(Session session, Destination destination) {
        this.session = session;

        try {
            messageProducer = session.createProducer(destination);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void sendOrderIsDeliveredMessage(Order order) {
        if (Objects.isNull(messageProducer)) {
            return;
        }

        try {
            Message message = session.createMessage();

            message.setStringProperty(ORDER_ID_PROPERTY_NAME, order.getId().toString());
            message.setStringProperty(IS_DELIVERED_PROPERTY_NAME, Boolean.toString(order.isDelivered()));

            messageProducer.send(message);

            logger.info("MESSAGE [{}: {}; {}: {}] HAS BEEN SENT",
                ORDER_ID_PROPERTY_NAME,
                order.getId(),
                IS_DELIVERED_PROPERTY_NAME,
                order.isDelivered());

        } catch (JMSException e) {
            logger.error("AN ERROR HAS OCCURRED DURING MESSAGE SENDING");
            logger.error(e.toString());
        }
    }
}
