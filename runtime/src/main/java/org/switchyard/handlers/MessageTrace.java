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

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.HandlerException;
import org.switchyard.runtime.util.ExchangeFormatter;

/**
 * Half-baked message tracing implementation, used primarily as an example
 * at this point.
 */
public class MessageTrace implements ExchangeHandler {
    
    private static Logger _log = Logger.getLogger(MessageTrace.class);

    @Override
    public void handleFault(Exchange exchange) {
        if (_log.isInfoEnabled()) {
            _log.info(ExchangeFormatter.format(exchange, false));
        }
    }

    @Override
    public void handleMessage(Exchange exchange) throws HandlerException {
        if (_log.isInfoEnabled()) {
            _log.info(ExchangeFormatter.format(exchange, true));
        }
    }

}
