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
package org.switchyard.component.camel.common.deploy;

import java.util.List;

import org.switchyard.ServiceDomain;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.BaseComponent;

/**
 * Base class for camel binding components.
 */
public abstract class BaseCamelComponent extends BaseComponent {

    /**
     * Default constructor.
     * 
     * @param name Component name.
     * @param types Names of supported elements.
     */
    protected BaseCamelComponent(String name, String ... types) {
        super(types);
        setName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Activator createActivator(ServiceDomain domain) {
        List<String> activationTypes = getActivationTypes();
        String[] types = activationTypes.toArray(new String[activationTypes.size()]);

        SwitchYardCamelContext camelContext = (SwitchYardCamelContext) domain
            .getProperty(SwitchYardCamelContext.CAMEL_CONTEXT_PROPERTY);
        BaseCamelActivator activator = createActivator(camelContext, types);
        activator.setServiceDomain(domain);
        activator.setEnvironment(getConfig());
        return activator;
    }

    /**
     * Creates activator for component.
     * 
     * @param camelContext Camel context.
     * @param types Activation types supported by component.
     * @return Activator instance.
     */
    protected abstract BaseCamelActivator createActivator(SwitchYardCamelContext camelContext, String ... types);

}

