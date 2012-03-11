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

package org.switchyard.component.camel.processor;

import org.apache.camel.Processor;
import org.switchyard.Exchange;
import org.switchyard.component.camel.CamelConstants;
import org.switchyard.composer.MessageComposer;

/**
 * Creates Camel Processors based on configuration.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public final class ProcessorFactory {

    /** 
     * No need to directly instantiate.
     */
    private ProcessorFactory() {
    }

    /**
     * Create a Camel Processor based on ConfigURI.
     *
     * @param composer the message composer to be used
     * @param exchange the switchayrd exchange
     * @param uri the configuration uri
     * @return the Camel processor
     */
    public static Processor newProcessor(MessageComposer composer, Exchange exchange, String uri) {
        if ((uri != null) && uri.startsWith(CamelConstants.CXFRS_SCHEME)) {
            if (uri.contains(CamelConstants.RESOURCE_CLASSES)) {
                return new CxfRsHttpDynamicProcessor(composer, exchange, uri);
            }
            return new CxfRsHttpProcessor(composer, exchange);
        }
        return new DefaultProcessor(composer, exchange);
    }
}
