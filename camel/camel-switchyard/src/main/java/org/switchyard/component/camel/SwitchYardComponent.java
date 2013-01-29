/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *  *
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
package org.switchyard.component.camel;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.switchyard.component.camel.common.composer.BindingDataCreatorResolver;

/**
 * SwitchYardComponent enable Switchyard services to be exposed through Apache Camel.
 * <p/>
 * This classes {@link #createEndpoint(String, String, Map)} creates a {@link SwitchYardEndpoint}.
 * 
 * Example usage using Camel's Java DSL:
 * <pre>
 * from("switchyard://someIncomingService")
 * ...
 * .to("switchyard://mySwitchyardService")
 * </pre>
 *
 * @author Daniel Bevenius
 */
public class SwitchYardComponent extends DefaultComponent {

    private BindingDataCreatorResolver _bindingDataCreatorResolver = new BindingDataCreatorResolver();

    @Override
    protected Endpoint createEndpoint(final String uri, final String path, final Map<String, Object> parameters) throws Exception {
        final String namespace = (String) parameters.remove("namespace");
        final String operationName = (String) parameters.remove("operationName");
        return new SwitchYardEndpoint(uri, this, namespace, operationName);
    }

    /**
     * Returns binding data creator resolver.
     * 
     * @return An instance of binding data creator.
     */
    public BindingDataCreatorResolver getBindingDataCreatorResolver() {
        return _bindingDataCreatorResolver;
    }

    /**
     * Setter which allows to specify custom bindingDataCreator.
     * 
     * @param bindingDataCreatorResolver Binding data creator to use.
     */
    public void setBindingDataCreatorResolver(BindingDataCreatorResolver bindingDataCreatorResolver) {
        this._bindingDataCreatorResolver = bindingDataCreatorResolver;
    }

}
