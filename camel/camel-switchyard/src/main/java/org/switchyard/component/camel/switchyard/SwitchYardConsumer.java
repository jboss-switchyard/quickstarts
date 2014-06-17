/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.component.camel.switchyard;

import static org.switchyard.Exchange.FAULT_TYPE;
import static org.switchyard.Exchange.OPERATION_NAME;
import static org.switchyard.Exchange.SERVICE_NAME;

import java.util.concurrent.atomic.AtomicReference;

import javax.xml.namespace.QName;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultMessage;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.component.camel.common.CamelConstants;
import org.switchyard.deploy.ServiceHandler;
import org.switchyard.SwitchYardException;
import org.switchyard.metadata.ServiceOperation;
import org.w3c.dom.Node;

/**
 * A SwitchYardConsumer is both a Camel Consumer and an SwitchYard ExchangeHandler.
 * </p>
 * A Camel event driven consumer that is able to receive events, SwitchYard Exchanges, 
 * and invoke the Camel processors.
 * 
 * @author Daniel Bevenius
 */
public class SwitchYardConsumer extends DefaultConsumer implements ServiceHandler {

    private AtomicReference<State> _state = new AtomicReference<State>(State.NONE);
    
    private QName _componentName;
    private String _namespace;
    
    /**
     * Used to flag an exchange as originating from a service implementation route.
     */
    public static final String IMPLEMENTATION_ROUTE = 
            "org.switchyard.component.camel.implementation";
    
    /**
     * The name of the service component containing this camel route.
     */
    public static final String COMPONENT_NAME = 
            "org.switchyard.component.camel.componentName";

    /**
     * Sole constructor.
     * 
     * @param endpoint The Camel endpoint that this consumer was created by.
     * @param processor The Camel processor that this consumer will delegate to.
     */
    public SwitchYardConsumer(final Endpoint endpoint, final Processor processor) {
        super(endpoint, processor);
    }

    @Override
    public void handleMessage(final Exchange switchyardExchange) throws HandlerException {
        org.apache.camel.Exchange camelExchange = getEndpoint().createExchange(
                isInOut(switchyardExchange) ? org.apache.camel.ExchangePattern.InOut : org.apache.camel.ExchangePattern.InOnly);
        DefaultMessage targetMessage = ExchangeMapper.mapSwitchYardToCamel(switchyardExchange, camelExchange);
        
        // mark this as an exchange for a camel implementation service
        camelExchange.setProperty(IMPLEMENTATION_ROUTE, true);
        camelExchange.setProperty(COMPONENT_NAME, _componentName);
        
        // set the application namespace property in case producer endpoints are used in the route
        camelExchange.setProperty(CamelConstants.APPLICATION_NAMESPACE, _namespace);

        ServiceOperation operation = switchyardExchange.getContract().getProviderOperation();
        camelExchange.setProperty(OPERATION_NAME, operation.getName());
        camelExchange.setProperty(FAULT_TYPE, operation.getFaultType());
        camelExchange.setProperty(SERVICE_NAME, switchyardExchange.getProvider().getName());
        camelExchange.setIn(targetMessage);

        invokeCamelProcessor(camelExchange);
        Exception camelException = camelExchange.getException();
        
        if (camelExchange.isFailed()) {
            QName faultName = switchyardExchange.getContract().getProviderOperation().getFaultType();
            Class<?> declaredFault = faultName != null && QNameUtil.isJavaMessageType(faultName) ? QNameUtil.toJavaMessageType(faultName) : null;

            Object camelFault = camelException;
            if (camelFault == null) {
                if (camelExchange.hasOut() && camelExchange.getOut().isFault()) {
                    // Use Out body as a fault content if camelExchange.getException() returns null
                    camelFault = camelExchange.getOut().getBody();
                }
            }

            if (camelFault != null && declaredFault != null && declaredFault.isAssignableFrom(camelFault.getClass())) {
                Message msg = switchyardExchange.createMessage().setContent(camelFault);
                switchyardExchange.sendFault(msg);
            } else if (camelFault instanceof Throwable) {
                throw new HandlerException(Throwable.class.cast(camelFault));
            } else if (camelFault instanceof Node) {
                Message msg = switchyardExchange.createMessage().setContent((Node)camelFault);
                switchyardExchange.sendFault(msg);
            } else {
                String faultMessage = (camelFault == null) ? null : camelFault.toString();
                throw SwitchYardCamelComponentMessages.MESSAGES.camelExchangeFailedWithoutException(camelFault.toString());
            }
        } else if (isInOut(switchyardExchange)) {
            sendResponse(camelExchange, switchyardExchange);
        }
    }

    @Override
    public synchronized void start() {
        if (getState() == State.STARTED) {
            // already started
            return;
        } else if (getState() != State.NONE) {
            throw SwitchYardCamelComponentMessages.MESSAGES.invalidHandlerState();
        }
        setState(State.STARTING);
        try {
            super.start();
            setState(State.STARTED);
        } catch (Exception ex) {
            setState(State.NONE);
            throw new SwitchYardException(ex);
        }
    }

    @Override
    public synchronized void stop() {
        if (getState() == State.NONE) {
            // already stopped
            return;
        } else if (getState() != State.STARTED) {
            throw SwitchYardCamelComponentMessages.MESSAGES.invalidHandlerState();
        }
        setState(State.STOPPING);
        try {
            super.stop();
            setState(State.NONE);
        } catch (Exception ex) {
            setState(State.STARTED);
            throw new SwitchYardException(ex);
        }
    }

    /**
     * Set the service component name for this camel route implementation.
     * @param componentName service component name
     */
    public void setComponentName(QName componentName) {
        _componentName = componentName;
    }
    
    /**
     * Set the application namespace for this camel route implementation.
     * @param namespace namespace URI
     */
    public void setNamespace(String namespace) {
        _namespace = namespace;
    }

    private void invokeCamelProcessor(final org.apache.camel.Exchange camelExchange) throws HandlerException {
        try {
            getProcessor().process(camelExchange);
        } catch (final Exception e) {
            throw new HandlerException(e); 
        }
    }

    private void sendResponse(org.apache.camel.Exchange camelExchange, final Exchange switchyardExchange) throws HandlerException {     
        Message message = ExchangeMapper.mapCamelToSwitchYard(
                camelExchange, switchyardExchange, ExchangePhase.OUT);
        switchyardExchange.send(message);
    }

    private boolean isInOut(final Exchange exchange) {
        return exchange.getContract().getProviderOperation().getExchangePattern() == ExchangePattern.IN_OUT;
    }

    @Override
    public void handleFault(final Exchange exchange) {
        //TODO: Implement error handling.
    }

    @Override
    public State getState() {
        return _state.get();
    }

    private void setState(State newState) {
        if (newState == null) {
            throw SwitchYardCamelComponentMessages.MESSAGES.stateCannotBeNull();
        }
        _state.set(newState);
    }
}
