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

package org.switchyard.test;

import org.junit.Assert;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.metadata.BaseExchangeContract;
import org.switchyard.metadata.ExchangeContract;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.metadata.ServiceOperation;

import javax.xml.namespace.QName;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
    private ExchangeContract _exchangeContract;
    private ExchangeHandlerProxy _exchangeHandlerProxy;
    private long _timeoutMillis = 10000;
    private QName _inputType;
    private QName _expectedOutputType;
    private QName _expectedFaultType;

    /**
     * Protected invoker.
     * @param domain The ServiceDomain.
     * @param serviceName The Service name.
     */
    protected Invoker(ServiceDomain domain, String serviceName) {
        _domain = domain;

        String[] serviceNameTokens = serviceName.split("\\.");

        if (serviceNameTokens.length == 1) {
            _serviceName = QName.valueOf(serviceName);
        } else if (serviceNameTokens.length == 2) {
            _serviceName = QName.valueOf(serviceNameTokens[0]);
            _operationName = serviceNameTokens[1];
        }
    }

    /**
     * Protected invoker.
     * @param domain The ServiceDomain.
     * @param serviceName The Service name.
     */
    public Invoker(ServiceDomain domain, QName serviceName) {
        _domain = domain;
        _serviceName = serviceName;
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
     * Set the target exchange contract.
     * @param exchangeContract The ExchangeContract.
     * @return This invoker instance.
     */
    public Invoker contract(ExchangeContract exchangeContract) {
        _exchangeContract = exchangeContract;
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
     * Send an IN_ONLY message to the target Service.
     * @param messagePayload The message payload.
     */
    public void sendInOnly(Object messagePayload) {
        ExchangeHandlerProxy exchangeHandlerProxy = _exchangeHandlerProxy;
        ResponseCatcher responseCatcher = null;

        if (exchangeHandlerProxy == null) {
            responseCatcher = new ResponseCatcher();
            exchangeHandlerProxy = createHandlerProxy(responseCatcher);
        }

        Exchange exchange = createExchange(exchangeHandlerProxy, ExchangePattern.IN_ONLY);

        Message message = exchange.createMessage().setContent(messagePayload);
        exchange.send(message);
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

        Exchange exchange = createExchange(exchangeHandlerProxy, ExchangePattern.IN_OUT);

        Message message = exchange.createMessage().setContent(messagePayload);
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

    private Exchange createExchange(ExchangeHandlerProxy exchangeHandlerProxy, ExchangePattern pattern) {
        ServiceReference service;
        ServiceOperation serviceOperation = _serviceOperation;
        ExchangeContract exchangeContract;

        service = _domain.getService(_serviceName);
        if (service == null) {
            Assert.fail("Unknown Service '" + _serviceName + "'.");
        }

        if (_exchangeContract ==  null) {
            BaseExchangeContract baseExchangeContract;

            if (serviceOperation == null) {
                // service operation has not been specified, check the registered service
                serviceOperation = service.getInterface().getOperation(_operationName != null ? _operationName : ServiceInterface.DEFAULT_OPERATION);
                if (serviceOperation == null) {
                    // nothing on the registered service, need to create our own
                    if (pattern == ExchangePattern.IN_ONLY) {
                        serviceOperation = new InOnlyOperation(_operationName);
                    } else if (pattern == ExchangePattern.IN_OUT) {
                        serviceOperation = new InOutOperation(_operationName);
                    }
                }
            }

            baseExchangeContract = new BaseExchangeContract(serviceOperation);
            baseExchangeContract.getInvokerInvocationMetaData().
                    setInputType(_inputType).
                    setOutputType(_expectedOutputType).
                    setFaultType(_expectedFaultType);

            exchangeContract = baseExchangeContract;
        } else {
            exchangeContract = _exchangeContract;
        }

        if (exchangeHandlerProxy != null) {
            return _domain.createExchange(service, exchangeContract, exchangeHandlerProxy._exchangeHandlerProxy);
        } else {
            return _domain.createExchange(service, exchangeContract);
        }
    }

    private ExchangeHandlerProxy createHandlerProxy(ExchangeHandler handler) {
        ProxyInvocationHandler proxyInvocationHandler = new ProxyInvocationHandler(handler);
        ExchangeHandler exchangeHandlerProxy = (ExchangeHandler) Proxy.newProxyInstance(ExchangeHandler.class.getClassLoader(),
                                                                                        new Class[]{ExchangeHandler.class},
                                                                                        proxyInvocationHandler);

        return new ExchangeHandlerProxy(proxyInvocationHandler, exchangeHandlerProxy);
    }

    private final class ExchangeHandlerProxy {
        private ProxyInvocationHandler _proxyInvocationHandler;
        private ExchangeHandler _exchangeHandlerProxy;

        private ExchangeHandlerProxy(ProxyInvocationHandler proxyInvocationHandler, ExchangeHandler exchangeHandlerProxy) {
            _proxyInvocationHandler = proxyInvocationHandler;
            _exchangeHandlerProxy = exchangeHandlerProxy;
        }
    }

    private class ProxyInvocationHandler implements InvocationHandler {

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

    private class ResponseCatcher implements ExchangeHandler {

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
