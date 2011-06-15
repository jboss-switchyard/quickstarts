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
     * Load SwitchYard deployments.
     * 
     * @param callback
     *            the callback.
     */
    void loadDeployments(AsyncCallback<List<SwitchYardDeployment>> callback);

    /**
     * Load modules registered with the SwitchYard subsystem.
     * 
     * @param callback
     *            the callback.
     */
    void loadModules(AsyncCallback<List<SwitchYardModule>> callback);

    /**
     * Load details for a specific deployment.
     * 
     * @param deploymentName
     *            the name of the deployment to load.
     * @param callback
     *            the callback.
     */
    void loadDeployment(String deploymentName, AsyncCallback<SwitchYardDeployment> callback);
}
