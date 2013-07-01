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
