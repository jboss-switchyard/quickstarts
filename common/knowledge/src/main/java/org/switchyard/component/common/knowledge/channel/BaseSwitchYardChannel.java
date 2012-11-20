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
package org.switchyard.component.common.knowledge.channel;

import org.switchyard.ServiceReference;

/**
 * A base implementation of a SwitchyardChannel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class BaseSwitchYardChannel implements SwitchYardChannel {

    private String _operation;
    private ServiceReference _reference;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOperation() {
        return _operation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SwitchYardChannel setOperation(String operation) {
        _operation = operation;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceReference getReference() {
        return _reference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SwitchYardChannel setReference(ServiceReference reference) {
        _reference = reference;
        return this;
    }

}
