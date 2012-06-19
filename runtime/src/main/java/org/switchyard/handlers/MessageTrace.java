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

package org.switchyard.handlers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Property;
import org.switchyard.Scope;

/**
 * Half-baked message tracing implementation, used primarily as an example
 * at this point.
 */
public class MessageTrace implements ExchangeHandler {
    
    private static final String INDENT = System.getProperty("line.separator");
    private static Logger _log = Logger.getLogger(MessageTrace.class);

    @Override
    public void handleFault(Exchange exchange) {
        if (_log.isInfoEnabled()) {
            _log.info(createTrace(exchange));
        }
    }

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        if (_log.isInfoEnabled()) {
            _log.info(createTrace(exchange));
        }
    }

    String createTrace(Exchange exchange) {
        StringBuilder summary = new StringBuilder()
            .append(indent(0) + "------- Begin Message Trace -------")
            .append(indent(0) + "Service -> " + exchange.getServiceName())
            .append(indent(0) + "Operation -> " + exchange.getContract().getServiceOperation().getName())
            .append(indent(0) + "Phase -> " + exchange.getPhase())
            .append(indent(0) + "State -> " + exchange.getState());
        
        // Add context properties
        Context ctx = exchange.getContext();
        summary.append(indent(0) + "Exchange Context -> ");
        for (Property p : ctx.getProperties(Scope.EXCHANGE)) {
            summary.append(indent(1) + p.getName() + " : " + p.getValue());
        }
        summary.append(indent(0) + "Message Context -> ");
        for (Property p : ctx.getProperties(Scope.valueOf(exchange.getPhase().toString()))) {
            summary.append(indent(1) + p.getName() + " : " + p.getValue());
        }
        
        // Add message content
        String content = null;
        try {
            // try to convert the payload to a string
            Message msg = exchange.getMessage();
            content = msg.getContent(String.class);
            
            // check to see if we have to put content back into the message 
            // after the conversion to string
            if (InputStream.class.isAssignableFrom(msg.getContent().getClass())) {
                msg.setContent(new ByteArrayInputStream(content.getBytes()));
            } else if (Reader.class.isAssignableFrom(msg.getContent().getClass())) {
                msg.setContent(new StringReader(content));
            }
        } catch (Exception ex) {
            // conversion failed, fall back on toString()
            if (exchange.getMessage().getContent() != null) {
                content = exchange.getMessage().getContent().toString();
            }
        }
        summary.append(indent(0) + "Message Content -> ")
            .append(indent(0) + content);
        
        summary.append(indent(0) + "------ End Message Trace -------");
        return summary.toString();
    }
    
    private String indent(int column) {
        String indent = INDENT;
        if (column > 0) {
            for (int i = 0; i < column; i++) {
                indent += "\t";
            }
        }
        return indent;
    }
}
