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
package org.switchyard.component.camel.core.model.v1;

import static org.switchyard.component.camel.core.model.Constants.CORE_NAMESPACE_V1;

import java.net.URI;

import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A generic binding definintion for Apache Camel components.
 * 
 * Example of a binding implemem
 * .tation:
 * <pre>
 * {@code
 *  <sca:service name="SimpleCamelService">
 *     <camel:binding.uri configURI="direct://input">
 *        <switchyard:operationSelector operationName="print"/>
 *     </camel:binding.uri>
 *  </sca:service>
 * }
 * </pre>
 * 
 * <ul>
 * <li> The 'configURI' attribute can be used to specify a Camel component uri.</li>
 * </ul>
 * 
 * @author Daniel Bevenius
 */
public class V1CamelBindingModel extends V1BaseCamelBindingModel {

    /**
     * The binding type (binding.camel).
     */
    public static final String URI = "uri";

    /**
     * This is the name of the uri attribute. 
     */
    public static final String CONFIG_URI = "configURI";

    /**
     * This is the name of the transacted attribute. 
     */
    public static final String TRANSACTED = "transacted";

    /**
     * The name of the transacedRef attribute.
     */
    public static final String TRANSACTED_REF = "transactedRef";

    /**
     * Create a new CamelBindingModel.
     */
    public V1CamelBindingModel() {
        super(URI, CORE_NAMESPACE_V1);
    }

    /**
     * Create a CamelBindingModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder();
    }

    /**
     * Gets the binding uri attribute from the underlying model.
     * 
     * @return URI The binding uri attribute.
     */
    public URI getConfigURI() {
        return java.net.URI.create(getModelAttribute(CONFIG_URI));
    }

    /**
     * Sets the "uri" element on the underlying model.
     * 
     * @param uri The Camel Component URI
     * @return {@link CamelBindingModel} to support method chaining.
     */
    public V1CamelBindingModel setConfigURI(URI uri) {
        setModelAttribute(CONFIG_URI, uri.toString());
        return this;
    }

    @Override
    public URI getComponentURI() {
        return getConfigURI();
    }

}
