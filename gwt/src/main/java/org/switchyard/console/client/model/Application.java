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

/**
 * Application
 * 
 * Represents a SwitchYard application.
 * 
 * @author Rob Cernich
 */
public interface Application extends HasQName {

    /**
     * @return the services provided by this application.
     */
    public List<Service> getServices();

    /**
     * @param services the services provided by this application.
     */
    public void setServices(List<Service> services);

    /**
     * @return the component services defined within this application.
     */
    public List<ComponentService> getComponentServices();

    /**
     * @param componentServices the component services defined within this
     *            application.
     */
    public void setComponentServices(List<ComponentService> componentServices);

    /**
     * @return the transformers defined within this application.
     */
    public List<Transformer> getTransformers();

    /**
     * @param transformers the transforms defined within this application.
     */
    public void setTransformers(List<Transformer> transformers);

    /**
     * @return the artifacts referenced by this application.
     */
    public List<ArtifactReference> getArtifacts();

    /**
     * @param artifacts the artifacts referenced by this application.
     */
    public void setArtifacts(List<ArtifactReference> artifacts);

    /**
     * @return the validators used by this application.
     */
    public List<Validator> getValidators();

    /**
     * @param validators the validators used by this application.
     */
    public void setValidators(List<Validator> validators);

}
