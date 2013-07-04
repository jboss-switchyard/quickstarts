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
 
package org.switchyard.component.sca;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.ExchangeState;
import org.switchyard.Message;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.SwitchYardException;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.SynchronousInOutHandler;
import org.switchyard.deploy.internal.Deployment;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.http.HttpInvoker;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.SerializerFactory;

/**
 * HTTP servlet which handles inbound remote communication for remote service endpoints.
 */
public class SwitchYardRemotingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static Logger _log = Logger.getLogger(SwitchYardRemotingServlet.class);
    
    private Serializer _serializer = SerializerFactory.create(FormatType.JSON, null, true);
    private RemoteEndpointPublisher _endpointPublisher;

    /**
     * {@inheritDoc}
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ClassLoader setTCCL = null;
        
        try {
            // Grab the right service domain based on the service header
            ServiceDomain domain = findDomain(request);
            // Set our TCCL to the domain's deployment loader
            ClassLoader loader = (ClassLoader) domain.getProperty(Deployment.CLASSLOADER_PROPERTY);
            setTCCL = Classes.setTCCL(loader);
            
            RemoteMessage msg = _serializer.deserialize(request.getInputStream(), RemoteMessage.class);
            if (_log.isDebugEnabled()) {
                _log.debug("Remote servlet received request for service " + msg.getService());
            }
            
            ServiceReference service = domain.getServiceReference(msg.getService());
            SynchronousInOutHandler replyHandler = new SynchronousInOutHandler();
            Exchange ex = msg.getOperation() == null
                    ? service.createExchange(replyHandler)
                    : service.createExchange(msg.getOperation(), replyHandler);
            Message m = ex.createMessage();
            if (msg.getContext() != null) {
                ex.getContext().setProperties(msg.getContext().getProperties());
            }
            m.setContent(msg.getContent());
            
            if (_log.isDebugEnabled()) {
                _log.debug("Invoking service " + msg.getService());
            }
            ex.send(m);
            
            // handle reply or fault
            RemoteMessage reply = null;
            if (ExchangePattern.IN_OUT.equals(ex.getPattern())) {
                replyHandler.waitForOut();
                reply = createReplyMessage(ex);
            } else if (ExchangeState.FAULT.equals(ex.getState())) {
                // Even though this is in-only, we need to report a runtime fault on send
                reply = createReplyMessage(ex);
            }
            
            // If there's a reply, send it back
            if (reply != null) {
                OutputStream out = response.getOutputStream();
                if (_log.isDebugEnabled()) {
                    _log.debug("Writing reply message to HTTP response stream " + msg.getService());
                }
                _serializer.serialize(reply, RemoteMessage.class, out);
                out.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                if (_log.isDebugEnabled()) {
                    _log.debug("No content to return for invocation of " + msg.getService());
                }
            }
        } catch (SwitchYardException syEx) {
            if (_log.isDebugEnabled()) {
                _log.debug("Failed to process remote invocation", syEx);
            }
            RemoteMessage reply = new RemoteMessage();
            reply.setFault(true);
            reply.setContent(syEx);
            _serializer.serialize(reply, RemoteMessage.class, response.getOutputStream());
            response.getOutputStream().flush();
        } finally {
            if (setTCCL != null) {
                Classes.setTCCL(setTCCL);
            }
        }
    }
    
    private ServiceDomain findDomain(HttpServletRequest request) throws SwitchYardException {
        ServiceDomain domain = null;
        String service = request.getHeader(HttpInvoker.SERVICE_HEADER);
        
        if (service == null || service.trim().length() == 0) {
            throw new SwitchYardException("Required '" + HttpInvoker.SERVICE_HEADER + "' header is missing or empty");
        }

        domain = _endpointPublisher.getDomain(QName.valueOf(service));
        if (domain == null) {
            throw new SwitchYardException("Unable to find ServiceDomain for service: " + service 
                    + ". Verify the service name and namespace are registered in the runtime.");
        }
        return domain;
    }
    
    /**
     * Set the endpoint publisher used by this servlet to locate the service domain for a service.
     * @param endpointPublisher endpoint publisher
     */
    public void setEndpointPublisher(RemoteEndpointPublisher endpointPublisher) {
        _endpointPublisher = endpointPublisher;
    }
    
    RemoteMessage createReplyMessage(Exchange exchange) {
        RemoteMessage reply = new RemoteMessage();
        reply.setDomain(exchange.getProvider().getDomain().getName())
            .setOperation(exchange.getContract().getConsumerOperation().getName())
            .setService(exchange.getConsumer().getName());
        exchange.getContext().mergeInto(reply.getContext());

        if (exchange.getMessage() != null) {
            reply.setContent(exchange.getMessage().getContent());
        }
        if (exchange.getState().equals(ExchangeState.FAULT)) {
            reply.setFault(true);
        }
        return reply;
    }

}
