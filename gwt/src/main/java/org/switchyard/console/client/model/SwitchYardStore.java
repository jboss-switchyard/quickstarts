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

package org.switchyard.console.client.model;

import java.util.List;

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

}
