/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.deployment;

import java.io.Serializable;
import java.util.Set;

import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;

import javax.management.ObjectName;

/**
 * SwitchYardMetaData represents the config data that the SwitchYard deployer
 * picks up and passes down the chain of deployment.
 * 
 * @author tcunning
 */
public class SwitchYardMetaData implements Serializable {
    /** Serial version unique identifier. */
    private static final long serialVersionUID = 0L;

    /** The name of the SwitchYard archive. */
    private String _archiveName = null;

    /** The deployment name. The SwitchYard archive name without the .esb suffix */
    private String _deploymentName = null;

    private CompositeModel _compositeModel = null;
    
    public SwitchYardMetaData(String archiveName, String deploymentName) {
    	_archiveName = archiveName;
    	_deploymentName = deploymentName;
    }

    public SwitchYardMetaData(String archiveName, String deploymentName, 
    		CompositeModel compositeModel) {
    	_archiveName = archiveName;
    	_deploymentName = deploymentName;
    	_compositeModel = compositeModel;
    }
    
    /**
     * Gets the name of the archive that this metadata came from.
     *
     * @return String   The name of the archive that this metadata came from.
     */
    public final String getArchiveName()
    {
        return _archiveName;
    }

    /**
     * The deployment name is the name of the .esb archive without the .esb suffix.
     *
     * @return String The name of the deployment. This is the archive name without the .esb suffix.
     */
    public String getDeploymentName()
    {
        return _deploymentName;
    }
    
    /**
     * Get the composite model.
     * @return compositeModel
     */
    public CompositeModel getCompositeModel() {
    	return _compositeModel;
    }
    
    /**
     * Set the composite model.
     * @param compositeModel compositeModel
     */
    public void setCompositeModel(CompositeModel compositeModel) {
    	_compositeModel = compositeModel;
    }
}
