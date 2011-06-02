/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.switchyard.deployment.torquebox;

import org.jruby.RubyHash;
import org.jruby.runtime.builtin.IRubyObject;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.ServiceReference;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.metadata.ServiceOperation;
import org.switchyard.metadata.java.JavaService;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service Invoker.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ServiceInvoker {

    private ServiceReference _serviceReference;

    /**
     * Public constructor.
     * @param serviceReference Reference to the target Service.
     */
    public ServiceInvoker(ServiceReference serviceReference) {
        if (serviceReference == null) {
            throw new IllegalArgumentException("null 'serviceReference' arg in method call.");
        }
        this._serviceReference = serviceReference;
    }

    /**
     * Send a message hash to the specified operation.
     * @param operationName The operation name.
     * @param rubyHash The message hash.
     * @return The response object if an IN_OUT operation was invoked, otherwise null.
     * @throws Throwable An exception occurred while invoking the target operation.
     */
    public Object send(String operationName, RubyHash rubyHash) throws Throwable {
        if (operationName == null) {
            throw new IllegalArgumentException("null 'operationName' argument.");
        }
        if (rubyHash == null) {
            throw new IllegalArgumentException("null 'rubyHash' argument.");
        }

        ServiceOperation operation = _serviceReference.getInterface().getOperation(operationName);

        if (operation == null) {
            throw new IllegalArgumentException("Unknown operation name '" + operationName + "' on Service '" + _serviceReference.getName() + "'.");
        }

        // Clone the RubyHash to convert it to a normal Map based graph.  This makes it possible
        // to more safely transport the payload data out of the ruby app via a SwitchYard Exchange...
        Map<String,Object> payload = deepClone(rubyHash);

        // Create the exchange contract...
        BaseExchangeContract exchangeContract = new BaseExchangeContract(operation);

        // Set the input type...
        exchangeContract.getInvokerInvocationMetaData().setInputType(JavaService.toMessageType(payload.getClass()));

        if (operation.getExchangePattern() == ExchangePattern.IN_OUT) {
            final BlockingQueue<Exchange> responseQueue = new ArrayBlockingQueue<Exchange>(1);

            AtomicReference<ExchangeHandler> responseExchangeHandler = new AtomicReference<ExchangeHandler>(new ExchangeHandler() {
                public void handleMessage(Exchange exchange) throws HandlerException {
                    responseQueue.offer(exchange);
                }

                public void handleFault(Exchange exchange) {
                    responseQueue.offer(exchange);
                }
            });

            Exchange exchange = _serviceReference.createExchange(exchangeContract, responseExchangeHandler.get());
            Message message = exchange.createMessage().setContent(payload);

            exchange.send(message);
            Exchange exchangeOut = null;
            try {
                exchangeOut = responseQueue.take();
            } catch (InterruptedException e) {
                throw new SwitchYardException("Operation '" + operationName + "' on Service '" + _serviceReference.getName() + "' interrupted.", e);
            }

            if (exchangeOut.getState() == ExchangeState.OK) {
                return exchangeOut.getMessage().getContent();
            } else {
                Object failureObj = exchangeOut.getMessage().getContent();
                if (failureObj instanceof Throwable) {
                    if (failureObj instanceof InvocationTargetException) {
                        throw ((Throwable)failureObj).getCause();
                    } else {
                        throw (Throwable) failureObj;
                    }
                } else {
                    throw new SwitchYardException("Service invocation failure.  Service '" + _serviceReference.getName() + "', operation '" + operationName + "'.  Non Throwable failure message payload: " + failureObj);
                }
            }
        } else {
            Exchange exchange = _serviceReference.createExchange(exchangeContract);
            Message message = exchange.createMessage().setContent(payload);

            exchange.send(message);
        }

        return null;
    }

    /**
     * Create a deep clone of the supplied {@link Map} instance.
     * @param sourceMap The Map to be cloned.
     * @return The cloned Map.
     */
    public static Map<String, Object> deepClone(Map<String, Object> sourceMap) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        Set<Map.Entry<String,Object>> mapEntries = sourceMap.entrySet();

        for (Map.Entry<String,Object> entry : mapEntries) {
            map.put(entry.getKey(), deepClone(entry.getValue()));
        }

        return map;
    }

    private static Collection<Object> deepClone(Collection<Object> sourceCollection) {
        Collection<Object> collection = createCollection(sourceCollection.getClass());

        for (Object entry : sourceCollection) {
            collection.add(deepClone(entry));
        }

        return collection;
    }

    private static Object deepClone(Object object) {
        if (object instanceof Map) {
            return deepClone((Map)object);
        } else if (object instanceof Collection) {
            return deepClone((Collection)object);
        } else if (object instanceof IRubyObject) {
            return object.toString();
        }
        return object;
    }

    private static Collection<Object> createCollection(Class<? extends Collection> colClass) {
        if (Set.class.isAssignableFrom(colClass)) {
            return new LinkedHashSet();
        } else if (List.class.isAssignableFrom(colClass)) {
            return new ArrayList();
        } else {
            return new ArrayList();
        }
    }
}
