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
package org.switchyard.console.client;

import org.jboss.as.console.spi.BeanFactoryExtension;
import org.switchyard.console.client.model.Application;
import org.switchyard.console.client.model.ArtifactReference;
import org.switchyard.console.client.model.ArtifactReferenceCategory;
import org.switchyard.console.client.model.Binding;
import org.switchyard.console.client.model.ComponentReference;
import org.switchyard.console.client.model.ComponentService;
import org.switchyard.console.client.model.MessageMetrics;
import org.switchyard.console.client.model.QNameCategory;
import org.switchyard.console.client.model.Reference;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.client.model.ServiceMetrics;
import org.switchyard.console.client.model.SystemDetails;
import org.switchyard.console.client.model.Throttling;
import org.switchyard.console.client.model.Transformer;
import org.switchyard.console.components.client.model.Component;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * BeanFactory
 * 
 * Factory for SwitchYard specific model beans.
 * 
 * @author Rob Cernich
 */
@BeanFactoryExtension
@AutoBeanFactory.Category({QNameCategory.class, ArtifactReferenceCategory.class})
public interface BeanFactory extends AutoBeanFactory {

    /**
     * @return a new AutoBean<SystemDetails>
     */
    AutoBean<SystemDetails> systemDetails();

    /**
     * @return a new AutoBean<Application>
     */
    AutoBean<Application> application();

    /**
     * @return a new AutoBean<Component>
     */
    AutoBean<Component> component();

    /**
     * @return a new AutoBean<Service>
     */
    AutoBean<Service> service();

    /**
     * @return a new AutoBean<Reference>
     */
    AutoBean<Reference> reference();

    /**
     * @return a new AutoBean<ComponentService>
     */
    AutoBean<ComponentService> componentService();

    /**
     * @return a new AutoBean<Binding>
     */
    AutoBean<Binding> binding();

    /**
     * @return a new AutoBean<Transformer>
     */
    AutoBean<Transformer> transformer();

    /**
     * @return a new AutoBean<ComponentReference>
     */
    AutoBean<ComponentReference> componentReference();

    /**
     * @return a new AutoBean<MessageMetrics>
     */
    AutoBean<MessageMetrics> messageMetrics();

    /**
     * @return a new AutoBean<ServiceMetrics>
     */
    AutoBean<ServiceMetrics> serviceMetrics();

    /**
     * @return a new AutoBean<ArtifactReference>
     */
    AutoBean<ArtifactReference> artifactReference();

    /**
     * @return a new AutoBean<Throttling>
     */
    AutoBean<Throttling> throttling();

}
