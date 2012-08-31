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
package org.switchyard.component.bpm.deploy;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.bpm.exchange.BPMExchangeHandler;
import org.switchyard.component.bpm.exchange.BPMExchangeHandlerFactory;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;

/**
 * Activator for the BPM component.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class BPMActivator extends BaseActivator {
    
    /**
     * BPM component activator type name.
     */
    public static final String BPM_TYPE = "bpm";

    private Map<QName,BPMExchangeHandler> _handlers = new HashMap<QName,BPMExchangeHandler>();

    /**
     * Constructs a new Activator of type "bpm".
     */
    public BPMActivator() {
        super(BPM_TYPE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceHandler activateService(QName name, ComponentModel config) {
        BPMExchangeHandler handler = BPMExchangeHandlerFactory.instance().newBPMExchangeHandler(getServiceDomain());
        BPMComponentImplementationModel bciModel = (BPMComponentImplementationModel)config.getImplementation();
        handler.init(name, bciModel);
        _handlers.put(name, handler);
        return handler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deactivateService(QName name, ServiceHandler handler) {
        try {
            ((BPMExchangeHandler)handler).destroy();
        } finally {
            _handlers.remove(name);
        }
    }
}
