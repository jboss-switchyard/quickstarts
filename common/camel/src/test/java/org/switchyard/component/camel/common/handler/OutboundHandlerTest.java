/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.switchyard.component.camel.common.handler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.switchyard.Exchange;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.component.camel.common.composer.CamelBindingData;
import org.switchyard.component.camel.common.composer.CamelComposition;
import org.switchyard.component.camel.common.model.CamelBindingModel;
import org.switchyard.component.camel.common.transaction.TransactionManagerFactory;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.naming.NamingMixIn;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/**
 * Functional test for {@link OutboundHandler}.
 * 
 * @author Daniel Bevenius
 * 
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(mixins = CDIMixIn.class)
public class OutboundHandlerTest extends CamelTestSupport {

    private NamingMixIn _mixIn;

    @EndpointInject(uri = "mock:result")
    private static MockEndpoint camelEndpoint;

    private ServiceDomain _serviceDomain;

    @ServiceOperation("TargetService")
    private Invoker _targetService;

    private CompositeReferenceModel referenceModel = mock(CompositeReferenceModel.class);
    private MessageComposer<CamelBindingData> _messageComposer;
    private ServiceReference _service;

    /**
     * Exception rule for verifying exceptions.
     */
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private CamelBindingModel bindingModel;

    @Before
    public void setupSwitchyardService() {
        bindingModel = mock(CamelBindingModel.class);
        when(bindingModel.getComponentURI()).thenReturn(URI.create("direct:to"));
        when(bindingModel.getName()).thenReturn("mockOutputHandler");
        when(bindingModel.getReference()).thenReturn(referenceModel);
        _messageComposer = CamelComposition.getMessageComposer();
        _serviceDomain.registerService(_targetService.getServiceName(),
            new InOnlyService(), 
            new OutboundHandler(bindingModel,
                (SwitchYardCamelContext) context, _messageComposer, null
            ) {
            {
                setState(State.STARTED);
            }
        }
        );
        _service = _serviceDomain.registerServiceReference(
            _targetService.getServiceName(), new InOnlyService());
    }

    @Test
    public void routeInOnlyToCamel() throws Exception {
        final String payload = "inOnly test string";
        _targetService.operation(ServiceInterface.DEFAULT_OPERATION).sendInOnly(payload);

        assertThat(camelEndpoint.getReceivedCounter(), is(1));
        final String received = (String) camelEndpoint.getReceivedExchanges().get(0).getIn().getBody();
        assertThat(received, is(equalTo(payload)));
    }

    @Test
    public void checkTransactionManagerRegistration() throws Exception {
        _mixIn.initialize();
        _mixIn.getInitialContext().bind(TransactionManagerFactory.JBOSS_USER_TRANSACTION, mock(UserTransaction.class));
        _mixIn.getInitialContext().bind(TransactionManagerFactory.JBOSS_TRANSACTION_MANANGER, mock(TransactionManager.class));
        _mixIn.getInitialContext().bind(TransactionManagerFactory.JBOSS_TRANSACTION_SYNC_REG, mock(TransactionSynchronizationRegistry.class));

        bindingModel = mock(CamelBindingModel.class);
        when(bindingModel.getComponentURI()).thenReturn(URI.create("transaction:foo?transactionManager=%23jtaTransactionManager"));
        when(bindingModel.getReference()).thenReturn(referenceModel);
        _messageComposer = CamelComposition.getMessageComposer();
        _serviceDomain.registerService(_targetService.getServiceName(),
            new InOnlyService(), 
            new OutboundHandler(bindingModel,
                (SwitchYardCamelContext) context, _messageComposer, null
            ) {
            {
                setState(State.STARTED);
            }
        }
        );
        _service = _serviceDomain.registerServiceReference(
            _targetService.getServiceName(), new InOnlyService());
        Exchange exchange = _service.createExchange();

        MockEndpoint endpoint = getMockEndpoint("mock:result");
        endpoint.expectedBodiesReceived("foo");
        exchange.send(exchange.createMessage().setContent("foo"));
    
        assertThat(context.getRegistry().lookup(TransactionManagerFactory.TM), is(notNullValue()));
        _mixIn.uninitialize();
    }

    @Test
    public void verifyThatEsbPropetiesArePassedToCamel() throws Exception {
        final String propertyKey = "testProp";
        final String propertyValue = "dummyValue";
        final Exchange exchange = _service.createExchange();
        exchange.getContext().setProperty(propertyKey, propertyValue, Scope.EXCHANGE);

        Message message = exchange.createMessage();
        message.getContext().setProperty(propertyValue, propertyKey);
        exchange.send(message);

        assertThat(camelEndpoint.getReceivedCounter(), is(1));
        org.apache.camel.Exchange ex = camelEndpoint.getReceivedExchanges().get(0);
        final String actualPropertyValue = (String) ex.getProperty(propertyKey);
        final String actualPropertyKey = (String) ex.getIn().getHeader(propertyValue);
        assertThat(actualPropertyValue, is(propertyValue));
        assertThat(actualPropertyKey, is(propertyKey));
    }

    private Message createMessageWithBody(final Exchange exchange, final Object body) {
        return exchange.createMessage().setContent(body);
    }

    @Test
    public void routeInOutToCamel() throws Exception {
        final String body = "inOut test string";
        final Exchange exchange = _service.createExchange();
        final Message message = createMessageWithBody(exchange, body);

        exchange.send(message);

        final String payload = (String) exchange.getMessage().getContent();
        assertThat(payload, is(equalTo(body)));
    }

    @Test
    public void throwsIllegalArgumentExceptionIfBindingModelIsNull() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("binding argument must not be null");
        new OutboundHandler(null, mock(SwitchYardCamelContext.class), _messageComposer, null);
    }

    @Test
    public void throwsIllegalArgumentExceptionIfBindingModelURIIsNull() {
        CamelBindingModel emptyURIModel = mock(CamelBindingModel.class);
        when(bindingModel.getComponentURI()).thenReturn(null);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("binding uri must not be null");
        new OutboundHandler(emptyURIModel, mock(SwitchYardCamelContext.class), _messageComposer, null);
    }

    @Test
    public void throwsIllegalArgumentExceptionIfCamelContextIsNull() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("camelContext argument must not be null");
        new OutboundHandler(bindingModel, null, _messageComposer, null);
    }

    @Test
    public void startStop() throws Exception {
        final ProducerTemplate producerTemplate = mock(ProducerTemplate.class);
        final OutboundHandler outboundHandler = new OutboundHandler(bindingModel,
            (SwitchYardCamelContext) context, _messageComposer, producerTemplate, null);
        outboundHandler.start();
        outboundHandler.stop();
        verify(producerTemplate).start();
        verify(producerTemplate).stop();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        return new SwitchYardCamelContext();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:to")
                .convertBodyTo(String.class)
                .log("Before Routing to mock:result body: ${body}")
                .to("mock:result");
                from("transaction:foo").to("mock:result");
            }
        };
    }

}
