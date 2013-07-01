/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.component.camel.common.model;

import java.net.URI;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.composite.BindingModel;

/**
 * A binding definition that specifies how communication between Apache Camel
 * and SwitchYard components is done.
 * 
 * Example of a binding implementation:
 * <pre>{
 *  <sca:service name="SimpleCamelService">
 *     <camel:binding.xyz>
 *        <switchyard:operationSelector operationName="print"/>
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
     * Name of 'produce' element.
     */
    String PRODUCE = "produce";

    /**
     * Name of 'consume' element.
     */
    String CONSUME = "consume";

    /**
     * Gets the component URI from the underlying model. 
     * Implementations are responsible for building an uri from the elements and attributes
     * of the configuration in question.
     * 
     * @return URI The Camel components URI.
     */
    URI getComponentURI();

    /**
     * Sets the global configuration.
     * 
     * @param config the environment/global config
     */
    void setEnvironment(Configuration config);

}
