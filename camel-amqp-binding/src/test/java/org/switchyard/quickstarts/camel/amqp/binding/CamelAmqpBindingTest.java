package org.switchyard.quickstarts.camel.amqp.binding;

import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.test.MockHandler;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.AMQPMixIn;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = {AMQPMixIn.class},
        scanners = BeanSwitchYardScanner.class
)
@RunWith(SwitchYardRunner.class)
public class CamelAmqpBindingTest {

    private ConnectionFactory _connectionFactory;
    private Context _context;

    private SwitchYardTestKit _testKit;

    @Test
    public void sendTextMessageToAmqpQueue() throws Exception {
        final String payload = "Hola Mundo!";
        //replace existing implementation for testing purposes
        _testKit.removeService("GreetingService");
        final MockHandler greetingService = _testKit.registerInOnlyService("GreetingService");

        sentTextToQueue(payload);
        //Allow for the JMS Message to be processed.
        Thread.sleep(1000);

        final LinkedBlockingQueue<Exchange> receivedMessages = greetingService.getMessages();
        assertThat(receivedMessages, is(notNullValue()));
        final Exchange receivedExchange = receivedMessages.iterator().next();
        assertThat(receivedExchange.getMessage().getContent(String.class), is(equalTo(payload)));
    }

    private void sentTextToQueue(String payload) throws NamingException, JMSException {
        Connection connection = null;
        Session session = null;
        try {
            _context = new InitialContext();
            _connectionFactory = (ConnectionFactory) _context.lookup(AMQPMixIn.JNDI_CONNECTION_FACTORY);
            connection = _connectionFactory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = (Destination) _context.lookup(AMQPMixIn.JNDI_QUEUE_EXCHANGE);

            MessageProducer messageProducer = session.createProducer(destination);
            //MessageConsumer messageConsumer = session.createConsumer(destination);

            TextMessage message = session.createTextMessage(payload);
            messageProducer.send(message);

            //message = (TextMessage)messageConsumer.receive();
            //System.out.println(message);

        } finally {
            if (connection != null) {
                try { connection.close(); } catch (Exception e) {}
            }
            if (session != null) {
                try {session.close(); } catch (Exception e) {}
            }
        }

    }




}
