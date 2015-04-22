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

package org.switchyard.console.client.model;

import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.switchyard.console.client.BeanFactory;
import org.switchyard.console.components.client.model.Component;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * SwitchYardStore
 * 
 * Interface used for loading domain objects.
 * 
 * @author Rob Cernich
 */
public interface SwitchYardStore {

    /**
     * @return the BeanFactory used by the store.
     */
    BeanFactory getBeanFactory();

    /**
     * Load details about the SwitchYard subsystem.
     * 
     * @param callback the callback.
     */
    void loadSystemDetails(AsyncCallback<SystemDetails> callback);

    /**
     * Load applications deployed on the SwitchYard subsystem.
     * 
     * @param callback the callback.
     */
    void loadApplications(AsyncCallback<List<Application>> callback);

    /**
     * Load details for a specific application.
     * 
     * @param applicationName the name of the application to load.
     * @param callback the callback.
     */
    void loadApplication(String applicationName, AsyncCallback<Application> callback);

    /**
     * Load components registered with the SwitchYard subsystem.
     * 
     * @param callback the callback.
     */
    void loadComponents(AsyncCallback<List<Component>> callback);

    /**
     * Load details for a specific component.
     * 
     * @param componentName the name of the component to load.
     * @param callback the callback.
     */
    void loadComponent(String componentName, AsyncCallback<Component> callback);

    /**
     * Load services deployed on the SwitchYard subsystem.
     * 
     * @param callback the callback.
     */
    void loadServices(AsyncCallback<List<Service>> callback);

    /**
     * Load details for a specific service.
     * 
     * @param serviceName the name of the service to load.
     * @param applicationName the name of the application containing the
     *            service.
     * @param callback the callback.
     */
    void loadService(String serviceName, String applicationName, AsyncCallback<Service> callback);

    /**
     * Loads message metrics for the specified service.
     * 
     * @param serviceName the name of the service
     * @param asyncCallback the callback
     */
    void loadServiceMetrics(String serviceName, AsyncCallback<ServiceMetrics> asyncCallback);

    /**
     * Loads message metrics for the specified service.
     * 
     * @param asyncCallback the callback
     */
    void loadAllServiceMetrics(AsyncCallback<List<ServiceMetrics>> asyncCallback);

    /**
     * Loads message metrics for the specified service.
     * 
     * @param asyncCallback the callback
     */
    void loadAllReferenceMetrics(AsyncCallback<List<ServiceMetrics>> asyncCallback);

    /**
     * Loads message metrics for the entire system.
     * 
     * @param asyncCallback the callback
     */
    void loadSystemMetrics(AsyncCallback<MessageMetrics> asyncCallback);

    /**
     * Loads artifact references for the entire system.
     * 
     * @param asyncCallback the callback
     */
    void loadArtifactReferences(AsyncCallback<List<ArtifactReference>> asyncCallback);

    /**
     * Load details for a specific reference.
     * 
     * @param referenceName the name of the reference to load.
     * @param applicationName the name of the application containing the
     *            reference.
     * @param callback the callback.
     */
    void loadReference(String referenceName, String applicationName, AsyncCallback<Reference> callback);

    /**
     * Load references deployed on the SwitchYard subsystem.
     * 
     * @param callback the callback.
     */
    void loadReferences(AsyncCallback<List<Reference>> callback);

    /**
     * Sets the property on the application.
     * 
     * @param applicationName the application
     * @param prop the property
     * @param callback the callback
     */
    void setApplicationProperty(String applicationName, PropertyRecord prop, final AsyncCallback<Void> callback);

    /**
     * Resets all metrics in the system.
     * 
     * @param callback the callback
     */
    void resetSystemMetrics(AsyncCallback<Void> callback);

    /**
     * Resets metrics for the named service/reference.
     * 
     * @param name the name of the service/reference.
     * @param applicationName the name of the containing application
     * @param callback the callback.
     */
    void resetMetrics(String name, String applicationName, AsyncCallback<Void> callback);

    /**
     * Start the specified gateway/binding.
     * 
     * @param name the gateway/binding name
     * @param serviceName the service/reference name
     * @param applicationName the application name
     * @param callback the callback
     */
    void startGateway(String name, String serviceName, String applicationName, AsyncCallback<Void> callback);

    /**
     * Stop the specified gateway/binding.
     * 
     * @param name the gateway/binding name
     * @param serviceName the service/reference name
     * @param applicationName the application name
     * @param callback the callback
     */
    void stopGateway(String name, String serviceName, String applicationName, AsyncCallback<Void> callback);

    /**
     * Updates the throttling configuration for the specified service.
     * 
     * @param service the service
     * @param throttling the new throttling configuration
     * @param callback the callback
     */
    void updateThrottling(Service service, Throttling throttling, AsyncCallback<Void> callback);

    /**
     * Create a new object from the change set.
     * 
     * @param <T> the type of object
     * @param type the type of object
     * @param original the original object
     * @param changeSet the changes
     * @param merge true if the new object should include values merged from the
     *            original
     * @return a new object
     */
    <T> T processChangeSet(Class<T> type, T original, Map<String, Object> changeSet, boolean merge);
}
