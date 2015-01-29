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

package org.switchyard.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataSource;
import javax.xml.namespace.QName;

import org.junit.Assert;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangeState;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;

/**
 * Unit Test Invoker.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class Invoker {

    private ServiceDomain _domain;
    private QName _serviceName;
    private String _operationName;
    private ServiceOperation _serviceOperation;
    private ExchangeHandlerProxy _exchangeHandlerProxy;
    private long _timeoutMillis = 10000;
    private QName _inputType;
    private QName _expectedOutputType;
    private QName _expectedFaultType;
    private Map<Scope, Map<String, Object>> _properties = new HashMap<Scope, Map<String, Object>>();
    private Map<String, DataSource> _attachments = new HashMap<String, DataSource>();

    /**
     * Protected invoker.
     * @param domain The ServiceDomain.
     * @param serviceName The Service name.
     */
    protected Invoker(ServiceDomain domain, String serviceName) {
        this(domain, XMLHelper.createQName(domain.getName().getNamespaceURI(), serviceName));
    }

    /**
     * Protected invoker.
     * @param domain The ServiceDomain.
     * @param serviceName The Service name.
     */
    public Invoker(ServiceDomain domain, QName serviceName) {
        _domain = domain;

        String[] serviceNameTokens = serviceName.getLocalPart().split("\\.");

        if (serviceNameTokens.length == 1) {
            _serviceName = serviceName;
        } else if (serviceNameTokens.length == 2) {
            _serviceName = XMLHelper.createQName(serviceName.getNamespaceURI(), serviceNameTokens[0]);
            _operationName = serviceNameTokens[1];
        } else if (serviceNameTokens.length >= 3) {
            // in case the service name contains dot
            String serviceOpName = serviceName.getLocalPart();
            int operDotIndex = serviceOpName.lastIndexOf('.');
            _serviceName = XMLHelper.createQName(serviceName.getNamespaceURI(), serviceOpName.substring(0, operDotIndex));
            _operationName = serviceOpName.substring(operDotIndex+1);
        }
    }

    /**
     * Get the target Service Name.
     * @return The target Service Name.
     */
    public QName getServiceName() {
        return _serviceName;
    }

    /**
     * Get the target Operation Name.
     * @return The target Operation Name.
     */
    public String getOperationName() {
        return _operationName;
    }

    /**
     * Set the target operation name.
     * @param operationName The operation name.
     * @return This invoker instance.
     */
    public Invoker operation(String operationName) {
        _operationName = operationName;
        return this;
    }

    /**
     * Set the target operation.
     * @param serviceOperation The target operation.
     * @return This invoker instance.
     */
    public Invoker operation(ServiceOperation serviceOperation) {
        _serviceOperation = serviceOperation;
        return this;
    }

    /**
     * Set the response handler.
     * @param handler The response handler.
     * @return This invoker instance.
     */
    public Invoker responseHandler(ExchangeHandler handler) {
        if (handler != null) {
            _exchangeHandlerProxy = createHandlerProxy(handler);
        }
        return this;
    }

    /**
     * Set the timeout for in-out invocations.
     * <p/>
     * Default is 10000ms (10s).
     *
     * @param timeoutMillis The timeout time in milliseconds.
     * @return This invoker instance.
     */
    public Invoker timeout(long timeoutMillis) {
        _timeoutMillis = timeoutMillis;
        return this;
    }

    /**
     * Set the input type for the exchange contract.
     * <p/>
     * Not relevant if an {@link #contract ExchangeContract is set} on this Invoker instance.
     *
     * @param inputType The input type for the exchange contract.
     * @return This invoker instance.
     */
    public Invoker inputType(QName inputType) {
        _inputType = inputType;
        return this;
    }

    /**
     * Set the output type for the exchange contract.
     * <p/>
     * Not relevant if an {@link #contract ExchangeContract is set} on this Invoker instance.
     *
     * @param expectedOutputType The output type for the exchange contract.
     * @return This invoker instance.
     */
    public Invoker expectedOutputType(QName expectedOutputType) {
        _expectedOutputType = expectedOutputType;
        return this;
    }

    /**
     * Set the expected fault type for the exchange contract.
     * <p/>
     * Not relevant if an {@link #contract ExchangeContract is set} on this Invoker instance.
     *
     * @param expectedFaultType The expected fault type for the exchange contract.
     * @return This invoker instance.
     */
    public Invoker expectedFaultType(QName expectedFaultType) {
        _expectedFaultType = expectedFaultType;
        return this;
    }

    /**
     * Sets a property at {@link Scope.MESSAGE}.
     * @param name the name
     * @param value the value
     * @return This invoker instance.
     */
    public Invoker property(String name, Object value) {
        return property(name, value, null);
    }

    /**
     * Sets a property at the specified scope.
     * @param name the name
     * @param value the value
     * @param scope the scope
     * @return This invoker instance.
     */
    public Invoker property(String name, Object value, Scope scope) {
        if (name != null) {
            if (scope == null) {
                scope = Scope.MESSAGE;
            }
            Map<String, Object> map = _properties.get(scope);
            if (map == null) {
                map = new HashMap<String, Object>();
                _properties.put(scope, map);
            }
            if (value != null) {
                map.put(name, value);
            } else {
                map.remove(name);
            }
        }
        return this;
    }

    private void setProperties(Exchange exchange, Message message) {
        Context exchangeContext = exchange.getContext();
        Map<String, Object> exchangeProperties = _properties.get(Scope.EXCHANGE);
        if (exchangeProperties != null) {
            for (Map.Entry<String, Object> exchangeProperty : exchangeProperties.entrySet()) {
                exchangeContext.setProperty(exchangeProperty.getKey(), exchangeProperty.getValue(), Scope.EXCHANGE);
            }
        }
        Context messageContext = exchange.getContext(message);
        Map<String, Object> messageProperties = _properties.get(Scope.MESSAGE);
        if (messageProperties != null) {
            for (Map.Entry<String, Object> messageProperty : messageProperties.entrySet()) {
                messageContext.setProperty(messageProperty.getKey(), messageProperty.getValue(), Scope.MESSAGE);
            }
        }
    }

    /**
     * Adds an attachment for created Messages.
     * @param name the name of the attachment
     * @param attachment the attachment
     * @return This invoker instance.
     */
    public Invoker attachment(String name, DataSource attachment) {
        if (name == null) {
            name = attachment.getName();
        }
        if (name != null) {
            if (attachment != null) {
                _attachments.put(name, attachment);
            } else {
                _attachments.remove(name);
            }
        }
        return this;
    }

    private void addAttachments(Message message) {
        for (Map.Entry<String, DataSource> entry : _attachments.entrySet()) {
            message.addAttachment(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Send an IN_ONLY message to the target Service.
     * @param messagePayload The message payload.
     * @throws InvocationFaultException if the message exchange produces a fault
     */
    public void sendInOnly(Object messagePayload) throws InvocationFaultException {
        ExchangeHandlerProxy exchangeHandlerProxy = _exchangeHandlerProxy;
        ResponseCatcher responseCatcher = null;

        if (exchangeHandlerProxy == null) {
            responseCatcher = new ResponseCatcher();
            exchangeHandlerProxy = createHandlerProxy(responseCatcher);
        }

        Exchange exchange = createExchange(ExchangePattern.IN_ONLY, exchangeHandlerProxy._exchangeHandlerProxy);

        Message message = exchange.createMessage().setContent(messagePayload);
        setProperties(exchange, message);
        addAttachments(message);
        exchange.send(message);
        
        if (exchange.getState().equals(ExchangeState.FAULT)) {
            throw new InvocationFaultException(exchange.getMessage());
        }
    }

    /**
     * Send an IN_OUT message to the target Service.
     * @param messagePayload The message payload.
     * @return The response message.
     * @throws InvocationFaultException if the message exchange produces a fault
     */
    public Message sendInOut(Object messagePayload) throws InvocationFaultException {
        ExchangeHandlerProxy exchangeHandlerProxy = _exchangeHandlerProxy;
        ResponseCatcher responseCatcher = null;

        if (exchangeHandlerProxy == null) {
            responseCatcher = new ResponseCatcher();
            exchangeHandlerProxy = createHandlerProxy(responseCatcher);
        }

        Exchange exchange = createExchange(ExchangePattern.IN_OUT, exchangeHandlerProxy._exchangeHandlerProxy);

        Message message = exchange.createMessage().setContent(messagePayload);
        setProperties(exchange, message);
        addAttachments(message);
        exchange.send(message);
        exchangeHandlerProxy._proxyInvocationHandler.waitForResponse(_timeoutMillis);

        if (responseCatcher != null) {
            if (responseCatcher._isFault) {
                throw new InvocationFaultException(responseCatcher._response);
            } else {
                return responseCatcher._response;
            }
        }

        return null;
    }

    /**
     * Create an {@link Exchange} instance for the target service operation.
     * @param handler The ExchangeHandler to be used on the Exchange.
     * @return The Exchange instance.
     */
    public Exchange createExchange(ExchangeHandler handler) {
        return createExchange(null, handler);
    }

    private Exchange createExchange(ExchangePattern pattern, ExchangeHandler handler) {
        
        ServiceOperation operation = _serviceOperation;
        ServiceReference reference;
        
        if (operation == null) {
            if (ExchangePattern.IN_ONLY.equals(pattern)) {
                operation = new InOnlyOperation(_operationName, _inputType);
            } else {
                operation = new InOutOperation(_operationName, _inputType, _expectedOutputType, _expectedFaultType);
            }
        }
        
        reference = _domain.registerServiceReference(_serviceName, new BaseService(operation), handler);
        return _operationName == null ? reference.createExchange() : reference.createExchange(_operationName);
    }

    private ExchangeHandlerProxy createHandlerProxy(ExchangeHandler handler) {
        ProxyInvocationHandler proxyInvocationHandler = new ProxyInvocationHandler(handler);
        ExchangeHandler exchangeHandlerProxy = (ExchangeHandler) Proxy.newProxyInstance(ExchangeHandler.class.getClassLoader(),
                                                                                        new Class[]{ExchangeHandler.class},
                                                                                        proxyInvocationHandler);

        return new ExchangeHandlerProxy(proxyInvocationHandler, exchangeHandlerProxy);
    }

    private static final class ExchangeHandlerProxy {
        private ProxyInvocationHandler _proxyInvocationHandler;
        private ExchangeHandler _exchangeHandlerProxy;

        private ExchangeHandlerProxy(ProxyInvocationHandler proxyInvocationHandler, ExchangeHandler exchangeHandlerProxy) {
            _proxyInvocationHandler = proxyInvocationHandler;
            _exchangeHandlerProxy = exchangeHandlerProxy;
        }
    }

    private static final class ProxyInvocationHandler implements InvocationHandler {

        private ExchangeHandler _handler;
        private boolean _responseReceived;

        public ProxyInvocationHandler(ExchangeHandler handler) {
            _handler = handler;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(_handler, args);
            } finally {
                if (method.getName().equals("handleMessage") || method.getName().equals("handleFault")) {
                    _responseReceived = true;
                }
            }
        }

        private void waitForResponse(long timeout) {
            long startTime = System.currentTimeMillis();
            while (!_responseReceived) {
                if (System.currentTimeMillis() > startTime + timeout) {
                    // timed out...
                    Assert.fail("Timed out waiting on response.");
                    break;
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Assert.fail("Unexpected InterruptedException: " + e.getMessage());
                    }
                }
            }
        }
    }

    private static final class ResponseCatcher implements ExchangeHandler {

        private Message _response;
        private boolean _isFault;

        @Override
        public void handleMessage(Exchange exchange) throws HandlerException {
            _response = exchange.getMessage();
        }

        @Override
        public void handleFault(Exchange exchange) {
            _isFault = true;
            _response = exchange.getMessage();
        }
    }
}
