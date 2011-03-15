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

import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;


/**
 * This is the abstract base class for a SOAP messages.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
@WebServiceProvider
@ServiceMode(Mode.MESSAGE)
public class BaseWebService implements Provider<SOAPMessage> {
    private InboundHandler _serviceConsumer;
    private ClassLoader _invocationClassLoader;

    protected BaseWebService() {
    }

    /**
     * Sets the service handler.
     * @param serviceConsumer the service handler.
     */
    public void setConsumer(final InboundHandler serviceConsumer) {
        _serviceConsumer = serviceConsumer;
    }

    /**
     * Sets the Invocation TCCL.
     * @param classLoader the classloader to be set.
     */
    public void setInvocationClassLoader(final ClassLoader classLoader) {
        _invocationClassLoader = classLoader;
    }

    /**
     * The Webservice implementation method, invokes the service handler.
     * @param request the SOAP request
     * @return the SOAP response
     */
    public SOAPMessage invoke(final SOAPMessage request) {
        ClassLoader original = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(_invocationClassLoader);
        SOAPMessage response = _serviceConsumer.invoke(request);
        Thread.currentThread().setContextClassLoader(original);
        return response;
    }
}

