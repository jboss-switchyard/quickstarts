/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
 
package org.switchyard.component.resteasy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.resteasy.composer.RESTEasyComposition;
import org.switchyard.component.resteasy.composer.RESTEasyBindingData;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.component.resteasy.resource.ResourcePublisherFactory;
import org.switchyard.component.resteasy.util.ClientInvoker;
import org.switchyard.component.resteasy.util.MethodInvoker;
import org.switchyard.deploy.BaseServiceHandler;

/**
 * Handles invoking external RESTEasy services.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class OutboundHandler extends BaseServiceHandler {

    private static final Logger LOGGER = Logger.getLogger(OutboundHandler.class);
    private static final Class<?>[] CLASS_ARG_ARRAY = {Class.class};

    private final RESTEasyBindingModel _config;
    private String _baseAddress = "http://localhost:8080";
    private Map<String, MethodInvoker> _methodMap = new HashMap<String, MethodInvoker>();
    private MessageComposer<RESTEasyBindingData> _messageComposer;


    /**
     * Constructor.
     * @param config the configuration settings
     */
    public OutboundHandler(final RESTEasyBindingModel config) {
        _config = config;
    }

    /**
     * Start lifecycle.
     *
     * @throws RESTEasyConsumeException If unable to load the REST interface
     */
    public void start() throws RESTEasyConsumeException {
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
                    _methodMap.put(method.getName(), new ClientInvoker(path, clazz, method));
                }
            }
        }
        // Create and configure the RESTEasy message composer
        _messageComposer = RESTEasyComposition.getMessageComposer(_config);
    }

    /**
     * Stop lifecycle.
     */
    public void stop() {
    }

    /**
     * The handler method that invokes the actual RESTEasy service when the
     * component is used as a RESTEasy consumer.
     * @param exchange the Exchange
     * @throws HandlerException handler exception
     */
    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        final String opName = exchange.getContract().getProviderOperation().getName();

        RESTEasyBindingData restRequest = null;
        try {
            restRequest = _messageComposer.decompose(exchange, new RESTEasyBindingData());
        } catch (Exception e) {
            final String m = "Unexpected exception composing outbound REST request";
            LOGGER.error(m, e);
            throw new HandlerException(m, e);
        }

        Object response = null;
        MethodInvoker methodInvoker = _methodMap.get(opName);
        if (methodInvoker == null) {
            final String m = "Unable to map " + opName + " among resources " + _methodMap.keySet();
            throw new HandlerException(m);
        }

        try {
            RESTEasyBindingData restResponse = methodInvoker.invoke(restRequest.getParameters(), restRequest.getHeaders());
            restResponse.setOperationName(opName);
            Message out = _messageComposer.compose(restResponse, exchange);
            // Our transformer magic transforms the entity appropriately here :)
            exchange.send(out);
        } catch (Exception e) {
            final String m = "Unexpected exception composing inbound Message";
            LOGGER.error(m, e);
            throw new HandlerException(m, e);
        }
    }
}
