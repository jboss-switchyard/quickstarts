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
package org.switchyard.component.camel;

import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.HandlerException;

/**
 * A handler that is capable of exposing Apache Camel components directly as Switchyard services. 
 * 
 * @author Daniel Bevenius
 *
 */
public class OutboundHandler extends BaseHandler {
    /**
     * Namespace prefix used Apache Camel endpoints that are registered in Switchyard.
     */
    public static final String CAMEL_PREFIX = "camel";
    
    private ProducerTemplate _producerTemplate;
    
    private static final Pattern BRACKET_PATTERN = Pattern.compile("\\{(" + CAMEL_PREFIX +")\\}(.*)");

    private final CamelContext _camelContext;

    /**
     * Sole constructor.
     * 
     * @param context The {@link CamelContext}.
     */
    public OutboundHandler(final CamelContext context) {
        this._camelContext = context;
        if (context == null) {
            throw new IllegalArgumentException("camelContext must not be null");
        }
        _producerTemplate = context.createProducerTemplate();
    }
    
    /**
     * Starts the CamelContext.
     * 
     * @throws Exception If an error occurs while trying to start the {@link CamelContext}.
     */
    public void start() throws Exception {
        _camelContext.start();
        _producerTemplate = _camelContext.createProducerTemplate();
    }
    
    /**
     * Stops the {@link CamelContext} and the producerTempalate.
     * 
     * @throws Exception If an error occurs while trying to start the {@link CamelContext}.
     */
    public void stop() throws Exception {
        _producerTemplate.stop();
        _camelContext.stop();
    }

    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        if (exchange.getContract().getServiceOperation().getExchangePattern() == ExchangePattern.IN_ONLY) {
            handleInOnly(exchange);
        } else {
            handleInOut(exchange);
        }
    }

    private void handleInOnly(final Exchange exchange) throws HandlerException {
        try {
            _producerTemplate.send(getCamelDestination(exchange), createProcessor(exchange));
        } catch (final CamelExecutionException e) {
            throw new HandlerException(e);
        }
    }
    
    private String getCamelDestination(final Exchange exchange) {
        return exchange.getService().getName().getLocalPart();
    }

    private void handleInOut(final Exchange exchange) throws HandlerException {
        final String toEndpoint = getCamelDestination(exchange);
        try {
            final org.apache.camel.Exchange camelExchange = _producerTemplate.request(toEndpoint, createProcessor(exchange));
            exchange.getMessage().setContent(camelExchange.getOut().getBody());
        } catch (final CamelExecutionException e) {
            throw new HandlerException(e);
        }
    }
    
    private Processor createProcessor(final Exchange switchyardExchange) {
        return new Processor() {
            @Override
            public void process(org.apache.camel.Exchange exchange) throws Exception {
                for (Entry<String, Object> entry : switchyardExchange.getContext().getProperties().entrySet()) {
                    exchange.setProperty(entry.getKey(), entry.getValue());
                }
                exchange.getIn().setBody(switchyardExchange.getMessage().getContent());
            }
        };
    }
    
    /**
     * A convenience method for creating service names for camel endpoints.
     * Will simply add {@value #CAMEL_PREFIX} as the namespace for the QName
     * 
     * @param serviceName A new QName with {@value #CAMEL_PREFIX} as the QName namespace.
     * @return QName The name of the service prefix with {@link #CAMEL_PREFIX}.
     */
    public static QName composeCamelServiceName(final String serviceName) {
        return new QName(CAMEL_PREFIX, serviceName);
    }

    /**
     * A convenience method for creating service names from a composite QName represented
     * as a String (qname.toString()).
     * 
     * @param serviceName Service name string in the format "{camel}serviceName"
     * @return QName A QName representation of the passed in serviceName.
     */
    public static QName parseServiceName(final String serviceName) {
        final Matcher matcher = BRACKET_PATTERN.matcher(serviceName);
        if (matcher.matches()) {
            return new QName(matcher.group(1), matcher.group(2));
        }
        
        return new QName(serviceName);
    }

}
