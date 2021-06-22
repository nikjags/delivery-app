package ru.study.delivery.delivery_utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.study.delivery.entities.Order;
import ru.study.delivery.jms_classes.ResponseProducer;

public class Deliverer {
    private static final int FAKE_DELIVERY_SLEEP_TIME = 10_000;

    private final ResponseProducer responseProducer;

    private final Logger logger = LoggerFactory.getLogger(Deliverer.class);

    public Deliverer(ResponseProducer responseProducer) {
        this.responseProducer = responseProducer;
    }

    public void sendToDeliver(Order order) {
        fakeDeliverSleep();

        order.setDelivered(true);
        logger.info("ORDER [id: {}] HAS BEEN DELIVERED", order.getId());

        responseProducer.sendOrderIsDeliveredMessage(order);
    }

    // ===================================================================================================================
    // = Implementation
    // ===================================================================================================================

    private void fakeDeliverSleep() {
        try {
            Thread.sleep(FAKE_DELIVERY_SLEEP_TIME);
        } catch (InterruptedException interruptedException) {
            logger.error("ERROR DURING DELIVERY SLEEP");
            logger.error(interruptedException.toString());
        }
    }
}
