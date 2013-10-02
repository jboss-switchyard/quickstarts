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
 
package org.switchyard.component.resteasy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.jboss.logging.Logger;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.resteasy.composer.RESTEasyComposition;
import org.switchyard.component.resteasy.composer.RESTEasyBindingData;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.component.resteasy.resource.ResourcePublisherFactory;
import org.switchyard.component.resteasy.util.ClientInvoker;
import org.switchyard.component.resteasy.util.MethodInvoker;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

/**
 * Handles invoking external RESTEasy services.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class OutboundHandler extends BaseServiceHandler {

    private static final Logger LOGGER = Logger.getLogger(OutboundHandler.class);
    private static final Class<?>[] CLASS_ARG_ARRAY = {Class.class};

    private final RESTEasyBindingModel _config;
    private final String _bindingName;
    private final String _referenceName;
    private String _baseAddress = "http://localhost:8080";
    private Map<String, MethodInvoker> _methodMap = new HashMap<String, MethodInvoker>();
    private MessageComposer<RESTEasyBindingData> _messageComposer;


    /**
     * Constructor.
     * @param config the configuration settings
     * @param domain the service domain.
     */
    public OutboundHandler(final RESTEasyBindingModel config, final ServiceDomain domain) {
        super(domain);
        _config = config;
        _bindingName = config.getName();
        _referenceName = config.getReference().getName();
    }

    /**
     * Start lifecycle.
     *
     * @throws RESTEasyConsumeException If unable to load the REST interface
     */
    protected void doStart() throws RESTEasyConsumeException {
        if (_methodMap.isEmpty()) {
            String resourceIntfs = _config.getInterfaces();
            String address = _config.getAddress();
            if (address != null) {
                _baseAddress = address;
            }
            String path = _baseAddress;
            String contextPath = _config.getContextPath();
            if ((contextPath != null) && !ResourcePublisherFactory.ignoreContext()) {
                path = path + "/" + contextPath;
            }
            StringTokenizer st = new StringTokenizer(resourceIntfs, ",");
            while (st.hasMoreTokens()) {
                String className = st.nextToken().trim();
                Class<?> clazz = Classes.forName(className);
                for (Method method : clazz.getMethods()) {
                    // ignore the as method to allow declaration in client interfaces
                    if (!("as".equals(method.getName()) && Arrays.equals(method.getParameterTypes(), CLASS_ARG_ARRAY))) {
                        _methodMap.put(method.getName(), new ClientInvoker(path, clazz, method, _config));
                    }
                }
            }
            // Create and configure the RESTEasy message composer
            _messageComposer = RESTEasyComposition.getMessageComposer(_config);
        }
    }

    /**
     * The handler method that invokes the actual RESTEasy service when the
     * component is used as a RESTEasy consumer.
     * @param exchange the Exchange
     * @throws HandlerException handler exception
     */
    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        // identify ourselves
        exchange.getContext().setProperty(ExchangeCompletionEvent.GATEWAY_NAME, _bindingName, Scope.EXCHANGE)
                .addLabels(BehaviorLabel.TRANSIENT.label());
        if (getState() != State.STARTED) {
            throw RestEasyMessages.MESSAGES.referenceBindingNotStarted(_referenceName, _bindingName);
        }

        final String opName = exchange.getContract().getProviderOperation().getName();

        RESTEasyBindingData restRequest = null;
        try {
            restRequest = _messageComposer.decompose(exchange, new RESTEasyBindingData());
        } catch (Exception e) {
            final String m = RestEasyMessages.MESSAGES.unexpectedExceptionComposingRESTRequest();
            LOGGER.error(m, e);
            throw new HandlerException(m, e);
        }

        Object response = null;
        MethodInvoker methodInvoker = _methodMap.get(opName);
        if (methodInvoker == null) {
            final String m = RestEasyMessages.MESSAGES.unableToMapAmongResources(opName, _methodMap.keySet().toString());
            throw new HandlerException(m);
        }

        try {
            RESTEasyBindingData restResponse = methodInvoker.invoke(restRequest.getParameters(), restRequest.getHeaders());
            restResponse.setOperationName(opName);
            Message out = _messageComposer.compose(restResponse, exchange);
            // Our transformer magic transforms the entity appropriately here :)
            exchange.send(out);
        } catch (Exception e) {
            final String m = "Unexpected exception composing inbound Message from Outbound";
            LOGGER.error(m, e);
            throw new HandlerException(m, e);
        }
    }
}
