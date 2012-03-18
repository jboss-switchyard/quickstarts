/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.console.client;

import org.switchyard.console.client.model.Application;
import org.switchyard.console.client.model.ArtifactReference;
import org.switchyard.console.client.model.ArtifactReferenceCategory;
import org.switchyard.console.client.model.Binding;
import org.switchyard.console.client.model.ComponentReference;
import org.switchyard.console.client.model.ComponentService;
import org.switchyard.console.client.model.MessageMetrics;
import org.switchyard.console.client.model.QNameCategory;
import org.switchyard.console.client.model.Service;
import org.switchyard.console.client.model.ServiceMetrics;
import org.switchyard.console.client.model.SystemDetails;
import org.switchyard.console.client.model.Transformer;
import org.switchyard.console.components.client.model.Component;

import com.google.gwt.autobean.shared.AutoBean;
import com.google.gwt.autobean.shared.AutoBeanFactory;
import com.google.gwt.autobean.shared.AutoBeanFactory.Category;

/**
 * BeanFactory
 * 
 * Factory for SwitchYard specific model beans.
 * 
 * @author Rob Cernich
 */
@Category({QNameCategory.class, ArtifactReferenceCategory.class})
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

}
