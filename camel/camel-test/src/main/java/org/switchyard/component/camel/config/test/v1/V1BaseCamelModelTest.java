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
package org.switchyard.component.camel.config.test.v1;

import java.io.InputStream;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.BeforeClass;
import org.switchyard.common.type.Classes;
import org.switchyard.component.camel.common.model.CamelBindingModel;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.CompositeReferenceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Base class for camel model bindings. Contains common code, mostly related to
 * model parsing and resource lookups.
 * 
 * @author Lukasz Dywicki
 *
 * @param <T> Model type.
 */
public abstract class V1BaseCamelModelTest<T extends CamelBindingModel> {

    /**
     * Static instance of context used to create endpoint instances.
     */
    private static CamelContext context;

    /**
     * Prepare test environment.
     */
    @BeforeClass
    public static void setUpClass() {
        context = new DefaultCamelContext();
    }

    /**
     * Lookup camel binding model in SCA configuration and use Switchard model
     * as root and return first element from it.
     * 
     * @param config Configuration location.
     * @return Camel binding.
     * @throws Exception In case of any problems exception is not handled.
     */
    @SuppressWarnings("unchecked")
    protected T getFirstCamelBinding(final String config) throws Exception {
        final InputStream in = Classes.getResourceAsStream(config, getClass());
        final SwitchYardModel model = new ModelPuller<SwitchYardModel>().pull(in);
        final List<CompositeServiceModel> services = model.getComposite().getServices();
        final CompositeServiceModel compositeServiceModel = services.get(0);
        final List<BindingModel> bindings = compositeServiceModel.getBindings();
        return (T) bindings.get(0);
    }

    /**
     * Lookup camel reference binding model in SCA configuration and use Switchard
     * model as root and return first element from it.
     * 
     * @param config Configuration location.
     * @return Camel binding.
     * @throws Exception In case of any problems exception is not handled.
     */
    @SuppressWarnings("unchecked")
    protected T getFirstCamelReferenceBinding(final String config) throws Exception {
        final InputStream in = Classes.getResourceAsStream(config, getClass());
        final SwitchYardModel model = new ModelPuller<SwitchYardModel>().pull(in);
        final List<CompositeReferenceModel> services = model.getComposite().getReferences();
        final CompositeReferenceModel compositeServiceModel = services.get(0);
        final List<BindingModel> bindings = compositeServiceModel.getBindings();
        return (T) bindings.get(0);
    }

    /**
     * Lookup camel binding model without usage of SCA parser.
     * 
     * @param config Configuration location.
     * @return Camel binding.
     * @throws Exception In case of any problems exception is not handled.
     */
    @SuppressWarnings("unchecked")
    protected T getFirstCamelModelBinding(final String config) throws Exception {
        final InputStream in = Classes.getResourceAsStream(config, getClass());
        final CamelBindingModel model = new ModelPuller<CamelBindingModel>().pull(in);
        return (T) model;
    }

    /**
     * Creates endpoint instance from given model.
     * 
     * @param model Endpoint configuration model (will be used to obtain uri).
     * @param enpointType Expected endpoint type.
     * @return Endpoint instance created with model URI.
     */
    protected <E extends Endpoint> E getEndpoint(CamelBindingModel model, Class<E> enpointType) {
        String configUri = model.getComponentURI().toString();
        return context.getEndpoint(configUri, enpointType);
    }

}
