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
package org.switchyard.component.camel.config.model;

import java.net.URI;

import org.switchyard.config.model.composite.BindingModel;

/**
 * A binding definition that specifies how communication between Apache Camel
 * and SwitchYard components is done.
 * 
 * Example of a binding implementation:
 * <pre>{
 *  <sca:service name="SimpleCamelService">
 *     <camel:binding.xyz>
 *        <camel:operationSelector operationName="print"/>
 *     </camel:binding.xyz>
 *  </sca:service>
 * }</pre>
 * 
 * <ul>
 * <li> The operationsSelector can be used to specify an operation name of the target 
 * service. 
 * This is required when you have components are consumers where there is
 * no way for the the operation name to be derived. For example, this might be the case when 
 * you are using the file component. This way you can specify the target operation name statically. 
 * This is an option element as there can be case when the target service can be derived. An example 
 * of this could be an webservice component where the operation name can be derived from the incoming
 * SOAPMessage and the WSDL.
 * </li>
 * </ul>
 * 
 * @author Daniel Bevenius
 */
public interface CamelBindingModel extends BindingModel {
    
    /**
     * Gets the component URI from the underlying model. 
     * Implementations are responsible for building an uri from the elements and attributes
     * of the configuration in question.
     * 
     * @return URI The Camel components URI.
     */
    URI getComponentURI();
    
    /**
     * Gets the {@link OperationSelector} on the underlying binding model.
     * 
     * @return {@link OperationSelector} The operationSelector for this binding.
     */
    OperationSelector getOperationSelector();
    
    /**
     * Sets the {@link OperationSelector} on the underlying binding model.
     * 
     * @param operationSelector The operationSelector for this binding.
     * @return {@link CamelBindingModel} to support method chaining.
     */
    CamelBindingModel setOperationSelector(OperationSelector operationSelector);
    
}
