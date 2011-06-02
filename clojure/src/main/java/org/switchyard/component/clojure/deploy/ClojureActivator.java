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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.component.clojure.config.model.ClojureComponentImplementationModel;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.exception.SwitchYardException;

import org.switchyard.deploy.BaseActivator;

/**
 * Activator for implemenations.clojure.
 * 
 * @author Daniel Bevenius
 *
 */
public class ClojureActivator extends BaseActivator {
    
    private static final String[] TYPES = new String[] {"clojure"};
    
    private Map<QName, ClojureHandler> _handlers = new HashMap<QName, ClojureHandler>();
    
    /**
     * Sole constructor .
     */
    public ClojureActivator() {
        super(TYPES);
    }

    @Override
    public ExchangeHandler init(final QName name, final Model config) {
        if (isComponentService(config)) {
            return handleImplemenation(config, name);
        }
        return null;
    }
    
    private ClojureHandler handleImplemenation(final Model model, final QName serviceName) {
        final ComponentImplementationModel implModel = getComponentImplementationModel(model);
        if (implModel instanceof ClojureComponentImplementationModel) {
            final ClojureHandler clojureHandler = new ClojureHandler((ClojureComponentImplementationModel) implModel);
            _handlers.put(serviceName, clojureHandler);
            return clojureHandler;
        }
        return null;
    }
    
    private boolean isComponentService(final Model config) {
        return config instanceof ComponentServiceModel;
    }
    
    private ComponentImplementationModel getComponentImplementationModel(final Model config) {
        final Model modelParent = ((ComponentServiceModel)config).getModelParent();
        final ComponentModel componentModel = (ComponentModel) modelParent;
        return componentModel.getImplementation();
    }

    @Override
    public void start(final ServiceReference service) {
        for (ClojureHandler handler : _handlers.values()) {
            try {
                handler.start(service);
            } catch (Exception e) {
                throw new SwitchYardException(e);
            }
        }
    }

    @Override
    public void stop(final ServiceReference service) {
    }

    @Override
    public void destroy(final ServiceReference service) {
    }

}
