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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceFeature;

import org.apache.log4j.Logger;
import org.switchyard.common.type.Classes;
import org.switchyard.component.soap.InboundHandler;

/**
 * Wrapper for JAX-WS endpoints.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class JAXWSEndpoint implements WSEndpoint {

    private static final Logger LOGGER = Logger.getLogger(JAXWSEndpoint.class);

    private Endpoint _endpoint;
    private String _publishUrl;

    /**
     * Construct a JAX-WS endpoint based on SOAP version.
     * @param bindingId The SOAP binding version
     * @param handler The handler instance that contains the actual invoke method implementation
     * @param features A list of WebService features
     */
    public JAXWSEndpoint(final String bindingId, final InboundHandler handler, WebServiceFeature... features) {
        BaseWebService wsProvider = new BaseWebService();
        wsProvider.setInvocationClassLoader(Classes.getTCCL());
        // Hook the handler
        wsProvider.setConsumer(handler);
        _endpoint = Endpoint.create(bindingId, wsProvider);
        try {
            Method method = _endpoint.getBinding().getClass().getSuperclass().getMethod("addFeature", new Class<?>[]{WebServiceFeature.class});
            for (WebServiceFeature feature : features) {
                method.invoke(_endpoint.getBinding(), feature);
            }
        } catch (NoSuchMethodException nsme) {
            // Silent fail
            LOGGER.error(nsme);
        } catch (IllegalAccessException iae) {
            // Silent fail
            LOGGER.error(iae);
        } catch (InvocationTargetException ite) {
            // Silent fail
            LOGGER.error(ite);
        }
    }

    /**
     * Returns the wrapped JAX-WS endpoint.
     * @return The JAX-WS endpoint
     */
    public Endpoint getEndpoint() {
        return _endpoint;
    }

    /**
     * Publish the JAX-WS endpoint.
     * @param publishUrl The url to publish the endpoint to
     */
    public void publish(String publishUrl) {
        _publishUrl = publishUrl;
       LOGGER.info("Publishing WebService at " + _publishUrl);
        _endpoint.publish(_publishUrl);
    }

    /**
     * {@inheritDoc}
     */
    public void stop() {
        LOGGER.info("Stopping WebService at " + _publishUrl);
        _endpoint.stop();
    }
}
