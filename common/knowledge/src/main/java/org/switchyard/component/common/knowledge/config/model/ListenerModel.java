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
package org.switchyard.component.common.knowledge.config.model;

import java.util.EventListener;

import org.switchyard.config.model.Model;

/**
 * EventListenerModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface ListenerModel extends Model {

    /**
     * The listener XML element.
     */
    public static final String LISTENER = "listener";

    /**
     * Gets the Listener class.
     * @param loader the ClassLoader to use
     * @return the Listener class
     */
    public Class<? extends EventListener> getClazz(ClassLoader loader);

    /**
     * Sets the Listener class.
     * @param clazz the Listener class
     * @return this ListenerModel (useful for chaining)
     */
    public ListenerModel setClazz(Class<? extends EventListener> clazz);

}
