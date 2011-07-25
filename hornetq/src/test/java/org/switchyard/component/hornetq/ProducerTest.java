package org.switchyard.component.hornetq;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.switchyard.component.hornetq.internal.HornetQUtil;
import org.switchyard.test.mixins.HornetQMixIn;

/**
 * Functional tests for {@link HornetQProducer}.
 * 
 * @author Daniel Bevenius
 */
public class ProducerTest extends CamelTestSupport {
    
    private static final String DESTINATION = "jms.queue.producer";
    private static final String FROM_ENDPOINT = "direct://input";
	private static HornetQMixIn _hornetQMixIn;

	@BeforeClass
	public static void startHornetQ() {
		_hornetQMixIn = new HornetQMixIn();
		_hornetQMixIn.initialize();
	}
 
	@AfterClass
	public static void shutdownHornetQ() {
		_hornetQMixIn.uninitialize();
	}
    
    @Test
    public void producerTest() throws Exception {
        final String payload = "dummy producer payload";
        sendBody(FROM_ENDPOINT, payload);
        
        final ClientConsumer consumer = _hornetQMixIn.getClientSession().createConsumer(DESTINATION);
        final ClientMessage receivedMsg = consumer.receive(2000);
        assertThat(receivedMsg, is(notNullValue()));
        final String receivedBody = (String) _hornetQMixIn.readObjectFromMessage(receivedMsg);
        
        assertThat(receivedBody, is(equalTo(payload)));
        
        HornetQUtil.closeClientConsumer(consumer);
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                final TransportConfiguration transportConfiguration = new TransportConfiguration(InVMConnectorFactory.class.getName());
                final JndiRegistry jndiRegistry = getJndiRegistry(getContext());
                jndiRegistry.bind("transportConfigBean", transportConfiguration);
                from(FROM_ENDPOINT)
                .to("hornetq://" + DESTINATION + "?transportConfiguration=#transportConfigBean");
            }
        };
    }
    
    private JndiRegistry getJndiRegistry(final CamelContext context) {
        final Registry registry = context.getRegistry();
        if (registry instanceof PropertyPlaceholderDelegateRegistry) {
            final PropertyPlaceholderDelegateRegistry ppdr = (PropertyPlaceholderDelegateRegistry) registry;
	        return (JndiRegistry) ppdr.getRegistry();
        }
        else {
	        return (JndiRegistry) context.getRegistry();
        }
    }
}
