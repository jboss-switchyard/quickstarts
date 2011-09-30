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
package org.switchyard.component.bpm.exchange;

import javax.xml.namespace.QName;

import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;

/**
 * The ExchangeHandler for the BPM component.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface BPMExchangeHandler extends ExchangeHandler {

    /**
     * Initializes the BPMExchangeHandler.
     * @param qname the qualified name
     * @param model the configuration
     */
    public void init(QName qname, BPMComponentImplementationModel model);

    /**
     * Starts the BPMExchangeHandler.
     * @param serviceRef the service reference
     */
    public void start(ServiceReference serviceRef);

    /**
     * Stops the BPMExchangeHandler.
     * @param serviceRef the service reference
     */
    public void stop(ServiceReference serviceRef);

    /**
     * Destroys the BPMExchangeHandler.
     * @param serviceRef the service reference
     */
    public void destroy(ServiceReference serviceRef);

}
