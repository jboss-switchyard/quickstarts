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
 
package org.switchyard.component.soap.endpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.MTOMFeature;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.addressing.impl.AddressingFeatureApplier;
import org.apache.log4j.Logger;
import org.switchyard.common.type.Classes;
import org.switchyard.component.soap.InboundHandler;

/**
 * Wrapper for CXF JAX-WS endpoints.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class CXFJettyEndpoint implements WSEndpoint {

    private static final Logger LOGGER = Logger.getLogger(CXFJettyEndpoint.class);

    private static Bus _bus;

    private JaxWsServerFactoryBean _svrFactory;
    private Server _server;
    private String _publishUrl;

    static {
        _bus = BusFactory.newInstance().createBus();
        _bus.setExtension(new AddressingFeatureApplier(), WSAddressingFeature.WSAddressingFeatureApplier.class);
    }

    /**
     * Construct a JAX-WS endpoint based on SOAP version.
     * @param bindingId The SOAP binding version
     * @param handler The handler instance that contains the actual invoke method implementation
     * @param features A list of WebService features
     */
    public CXFJettyEndpoint(final String bindingId, final InboundHandler handler, WebServiceFeature... features) {
        BaseWebService wsProvider = new BaseWebService();
        wsProvider.setInvocationClassLoader(Classes.getTCCL());
        // Hook the handler
        wsProvider.setConsumer(handler);
        //_endpoint = Endpoint.create(bindingId, wsProvider);
        _svrFactory = new JaxWsServerFactoryBean();
        _svrFactory.setServiceClass(BaseWebService.class);
        _svrFactory.setServiceBean(wsProvider);
        _svrFactory.setBindingId(bindingId);
        Map<String, Object> props = new HashMap<String, Object>();
        List<WebServiceFeature> cxfFeatures = new ArrayList<WebServiceFeature>();

        for (WebServiceFeature feature : features) {
            cxfFeatures.add(feature);
            if ((feature instanceof AddressingFeature) && ((AddressingFeature)feature).isEnabled()) {
                LOGGER.info("Addressing [enabled = " + ((AddressingFeature)feature).isEnabled() + ", required = " + ((AddressingFeature)feature).isRequired() + "]");
            } else if (feature instanceof MTOMFeature) {
                props.put("mtom-enabled", ((MTOMFeature)feature).isEnabled());
                LOGGER.info("MTOM [enabled = " + ((MTOMFeature)feature).isEnabled() + ", threshold = " + ((MTOMFeature)feature).getThreshold() + "]");
            }
        }
        ((JaxWsServiceFactoryBean)_svrFactory.getServiceFactory()).setWsFeatures(cxfFeatures);
        _svrFactory.setProperties(props);
        _svrFactory.getInInterceptors().add(new LoggingInInterceptor());
        _svrFactory.getInInterceptors().add(new org.apache.cxf.binding.soap.saaj.SAAJInInterceptor());
        _svrFactory.getInInterceptors().add(new org.apache.cxf.binding.soap.interceptor.SoapActionInInterceptor());
        _svrFactory.getOutInterceptors().add(new LoggingOutInterceptor());
        _svrFactory.getOutInterceptors().add(new org.apache.cxf.binding.soap.saaj.SAAJOutInterceptor());
    }

    /**
     * Returns the wrapped JAX-WS endpoint.
     * @return The JAX-WS endpoint
     */
    public JaxWsServerFactoryBean getEndpoint() {
        return _svrFactory;
    }

    /**
     * Publish the JAX-WS endpoint.
     * @param publishUrl The url to publish the endpoint to
     */
    public void publish(String publishUrl) {
        _publishUrl = publishUrl;
       LOGGER.info("Publishing WebService at " + _publishUrl);
        _svrFactory.setAddress(publishUrl);
        _server = _svrFactory.create();
        _server.start();
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        LOGGER.info("Stopping WebService at " + _publishUrl);
        _server.stop();
        _server.destroy();
    }
}
