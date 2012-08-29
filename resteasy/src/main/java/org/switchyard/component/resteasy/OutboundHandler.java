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

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.common.rest.RsMethod;
import org.switchyard.component.common.rest.RsMethodUtil;
import org.switchyard.component.resteasy.composer.RESTEasyComposition;
import org.switchyard.component.resteasy.composer.RESTEasyBindingData;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;
import org.switchyard.deploy.BaseServiceHandler;

/**
 * Handles invoking external RESTEasy services.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class OutboundHandler extends BaseServiceHandler {

    private static final Logger LOGGER = Logger.getLogger(OutboundHandler.class);

    private final RESTEasyBindingModel _config;
    private String _baseAddress = "http://localhost:8080";
    private Map<String, RsMethod> _resourcePaths;
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
        _resourcePaths = RsMethodUtil.parseResources(resourceIntfs);
        String address = _config.getAddress();
        if (address != null) {
            _baseAddress = address;
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
        final RsMethod restMethod = _resourcePaths.get(exchange.getContract().getServiceOperation().toString());
        final String opName = exchange.getContract().getServiceOperation().getName();
        if (restMethod == null) {
            throw new RuntimeException("Could not map " + opName + " to any RESTEasy method.");
        }
        try {
            String path = RsMethodUtil.getPath(restMethod, exchange);
            String contextPath = _config.getContextPath();

            if (contextPath != null) {
                path = "/" + contextPath + path;
            }

            // Support for proxy client may be added later, please do not remove this commented code, which is for reference.
            /*Object restProxy = ProxyFactory.create(restMethod.getResource(), _baseAddress);
            Method method = null;
            Object response = null;
            if (restMethod.getRequestType() != null) {
                method = restMethod.getResource().getMethod(opName, restMethod.getRequestType());
                response = method.invoke(restProxy, content);
            } else {
                method = restMethod.getResource().getMethod(opName);
                method.invoke(restProxy);
            }
            Message out = exchange.createMessage();
            out.setContent(response);
            exchange.send(out);*/

            // Support for manual client
            ClientRequest request = new ClientRequest(_baseAddress + path);
            RESTEasyBindingData restRequest = _messageComposer.decompose(exchange, new RESTEasyBindingData());
            Object content = restRequest.getContent();
            if ((restMethod.getRequestType() != null) && (content != null) && !restMethod.hasParam()) {
                // Factor based on media type
                if (restMethod.getConsumes().contains(MediaType.TEXT_PLAIN_TYPE)) {
                    request.body(MediaType.TEXT_PLAIN, content);
                } else if (restMethod.getConsumes().contains(MediaType.APPLICATION_XML_TYPE) || restMethod.getConsumes().contains(MediaType.WILDCARD_TYPE)) {
                    JAXBContext jaxbContext = JAXBContext.newInstance(restMethod.getRequestType());
                    Marshaller marshaller = jaxbContext.createMarshaller();
                    Writer sw = new StringWriter();
                    marshaller.marshal(content, sw);
                    request.body(MediaType.APPLICATION_XML, sw.toString());
                } else if (restMethod.getConsumes().contains(MediaType.TEXT_XML_TYPE)) {
                    JAXBContext jaxbContext = JAXBContext.newInstance(restMethod.getRequestType());
                    Marshaller marshaller = jaxbContext.createMarshaller();
                    Writer sw = new StringWriter();
                    marshaller.marshal(content, sw);
                    request.body(MediaType.TEXT_XML, sw.toString());
                } else if (restMethod.getConsumes().contains(MediaType.APPLICATION_JSON_TYPE)) {
                    ObjectMapper mapper = new ObjectMapper();
                    Writer sw = new StringWriter();
                    mapper.writeValue(sw, content);
                    request.body(MediaType.APPLICATION_JSON, sw.toString());
                }
                // Other types coming soon
            } else if (restMethod.hasQueryParam()) {
                request.queryParameter(restMethod.getParamName(), content);
            } else if (restMethod.hasPathParam()) {
                request.pathParameters(content);
            } else if (restMethod.hasMatrixParam()) {
                request.matrixParameter(restMethod.getParamName(), content);
            }
            request.getHeaders().putAll(restRequest.getHeaders());
            ClientResponse<?> response = request.httpMethod(restMethod.getMethod(), restMethod.getResponseType());
            if (response.getStatus() == 200) {
                RESTEasyBindingData restResponse = new RESTEasyBindingData();
                restResponse.setContent(response.getEntity());
                restResponse.setHeaders(response.getHeaders());
                Message out = _messageComposer.compose(restResponse, exchange, true);
                // Our transformer magic transforms the entity appropriately here :)
                exchange.send(out);
            }
        } catch (Exception e) {
            final String m = "Unexpected exception handling outbound REST request";
            LOGGER.error(m, e);
            throw new HandlerException(m, e);
        }
    }
}
