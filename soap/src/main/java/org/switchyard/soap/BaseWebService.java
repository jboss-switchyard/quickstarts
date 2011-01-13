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
 
package org.switchyard.soap;

import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Provider;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceProvider;


/**
 * This is the abstract base class for a SOAP messages.
 * @author <a href="mailto:mageshbk@jboss.com">Magesh Kumar B</a>
 */
@WebServiceProvider
@ServiceMode(Mode.MESSAGE)
public class BaseWebService implements Provider<SOAPMessage> {
    private InboundHandler _serviceConsumer;

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
     * The Webservice implementation method, invokes the service handler.
     * @param request the SOAP request
     * @return the SOAP response
     */
    public SOAPMessage invoke(final SOAPMessage request) {
        return _serviceConsumer.invoke(request);
    }
}
