/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.clojure.deploy;

import javax.xml.namespace.QName;

import org.switchyard.component.clojure.config.model.ClojureComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.deploy.BaseActivator;
import org.switchyard.deploy.ServiceHandler;

/**
 * Activator for implemenations.clojure.
 * 
 * @author Daniel Bevenius
 *
 */
public class ClojureActivator extends BaseActivator {
    
    private static final String[] TYPES = new String[] {"clojure"};
    
    /**
     * Sole constructor .
     */
    public ClojureActivator() {
        super(TYPES);
    }
    
    @Override
    public ServiceHandler activateService(QName name, ComponentModel config) {
        return new ClojureHandler((ClojureComponentImplementationModel)config.getImplementation());
    }

    @Override
    public void deactivateService(QName name, ServiceHandler handler) {
        // Nothing to do here
    }
}
