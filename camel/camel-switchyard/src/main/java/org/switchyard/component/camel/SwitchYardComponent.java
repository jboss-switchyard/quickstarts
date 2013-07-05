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
        final String operationName = (String) parameters.remove("operationName");
        return new SwitchYardEndpoint(uri, this, operationName);
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
