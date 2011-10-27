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

package org.switchyard.admin;

import java.util.List;

import javax.xml.namespace.QName;

/**
 * Application
 * 
 * Represents an application deployed within the SwitchYard runtime.
 */
public interface Application {
    /**
     * @return the services exported by this application.
     */
    public List<Service> getServices();

    /**
     * @param serviceName the name of a service provided by this application.
     * @return the requested service, may be null
     */
    public Service getService(QName serviceName);

    /**
     * @return the component services contained by this application.
     */
    public List<ComponentService> getComponentServices();

    /**
     * @param componentServiceName the name of a component service contained by
     *            this application.
     * @return the requested service, may be null
     */
    public ComponentService getComponentService(QName componentServiceName);

    /**
     * @return the transformers provided by this application
     */
    public List<Transformer> getTransformers();

    /**
     * @return the validators provided by this application
     */
    public List<Validator> getValidators();

    /**
     * Returns the name of the application. This will be the name specified
     * within the switchyard.xml file, if one exists. If a name is not specified
     * within the switchyard.xml file, the deployment archive name will be
     * returned.
     * 
     * @return the name of this application.
     */
    public QName getName();
}
