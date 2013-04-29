/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.bus.camel;

import org.apache.log4j.Logger;
import org.switchyard.Exchange;
import org.switchyard.ExchangePattern;
import org.switchyard.common.lang.Strings;

/**
 * Default error listener.
 */
public class DefaultErrorListener implements ErrorListener {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(DefaultErrorListener.class);

    @Override
    public void notify(Exchange exchange, Throwable throwable) {
        ExchangePattern pattern = exchange.getContract().getConsumerOperation().getExchangePattern();

        String message = String.format("Caught exception of type %s with message: %s", throwable.getClass().getName(), throwable.getMessage());
        String causeTrace = "";

        if (throwable.getCause() != null) {
            String causedBy = "\n%sCaused by exception of type %s, message: %s";
            Throwable cause = throwable.getCause();
            int depth = 0;
            while (cause != null) {
                causeTrace += String.format(causedBy, Strings.repeat("  ", ++depth), cause.getClass().getName(), cause.getMessage());
                cause = cause.getCause();
            }
        }

        if (pattern == ExchangePattern.IN_ONLY) {
            LOG.error(message + causeTrace, throwable);
        } else {
            LOG.debug(message + causeTrace, throwable);
        }
    }

}
