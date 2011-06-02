/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.component.soap;

import org.apache.log4j.Logger;
import org.switchyard.ServiceDomain;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.exception.SwitchYardException;


/**
 * SOAP Gateway acts as an adapter to expose SwitchYard services as a Webservice
 * and also to invoke other Webservices.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SOAPGateway {
    private static final Logger LOGGER = Logger.getLogger(SOAPGateway.class);

    private InboundHandler _wsProvider;
    private OutboundHandler _wsConsumer;
    private SOAPBindingModel _config;
    private ServiceDomain _domain;

    /**
     * Constructor.
     */
    public SOAPGateway() {
    }

    /**
     * Initialization code.
     * @param config the configuration settings
     * @param domain the service domain
     */
    public void init(final SOAPBindingModel config, ServiceDomain domain) {
        _domain = domain;
        _config = config;
        if (config.getPublishAsWS()) {
            // Consume the SwitchYard service
            _wsProvider = new InboundHandler(config);
        } else {
            // Create a WS Client for our service
            _wsConsumer = new OutboundHandler(config);
            _domain.registerService(config.getServiceName(), _wsConsumer);
        }
    }

    /**
     * Start lifecycle.
     */
    public void start() {
        if (_wsProvider != null) {
            try {
                _wsProvider.start(_domain.getService(_config.getServiceName()));
            } catch (Exception e) {
                LOGGER.error(e);
                throw new SwitchYardException("WebService could not be published!");
            }
        }
        if (_wsConsumer != null) {
            try {
                _wsConsumer.start();
            } catch (Exception e) {
                LOGGER.error(e);
                throw new SwitchYardException("WebService could not be consumed!");
            }
        }
    }

    /**
     * Stop lifecycle.
     */
    public void stop() {
        if (_wsProvider != null) {
            _wsProvider.stop();
        }
        if (_wsConsumer != null) {
            _wsConsumer.stop();
        }
    }

    /**
     * Destroy lifecycle.
     */
    public void destroy() {
    }
}
