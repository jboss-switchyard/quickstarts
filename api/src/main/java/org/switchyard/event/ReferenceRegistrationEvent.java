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

package org.switchyard.event;

import java.util.EventObject;
import org.switchyard.ServiceReference;

/**
 * Fired when a ServiceReference has been registered in the domain.
 */
public class ReferenceRegistrationEvent extends EventObject {
    
    private static final long serialVersionUID = 6157615236549484485L;

    /**
     * Create a new ReferenceRegistrationEvent.
     * @param reference the reference that was registered
     */
    public ReferenceRegistrationEvent(ServiceReference reference) {
        super(reference);
    }

    /**
     * Get the registered reference associated with this event.
     * @return registered service reference
     */
    public ServiceReference getReference() {
        return (ServiceReference)getSource();
    }
}
