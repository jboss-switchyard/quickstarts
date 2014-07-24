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
package org.switchyard.as7.extension.deployment;

import org.jboss.as.server.deployment.AttachmentKey;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.msc.service.ServiceName;
import org.jboss.vfs.VirtualFile;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Information about a SwitchYard deployment that is attached to the DU.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class SwitchYardMetaData {

    /** The attachment key. */
    public static final AttachmentKey<SwitchYardMetaData> ATTACHMENT_KEY = AttachmentKey.create(SwitchYardMetaData.class);

    /** The service name attachment key. */
    public static final AttachmentKey<ServiceName> SERVICENAME_ATTACHMENT_KEY = AttachmentKey.create(ServiceName.class);

    /** The service name attachment key. */
    public static final AttachmentKey<ModuleIdentifier> BEAN_COMPONENT_ATTACHMENT_KEY = AttachmentKey.create(ModuleIdentifier.class);

    /** The name of the SwitchYard archive. */
    private String _archiveName = null;

    /** The deployment name. The SwitchYard archive name without the .esb suffix */
    private String _deploymentName = null;

    private VirtualFile _switchYardFile = null;

    private SwitchYardModel _switchYardModel = null;

    /**
     * Create a new SwitchYard metadata.
     * @param archiveName name of the deployment archive
     * @param deploymentName name of the deployment
     */
    public SwitchYardMetaData(String archiveName, String deploymentName) {
        _archiveName = archiveName;
        _deploymentName = deploymentName;
    }

    /**
     * Gets the name of the archive that this metadata came from.
     *
     * @return String   The name of the archive that this metadata came from.
     */
    public final String getArchiveName() {
        return _archiveName;
    }

    /**
     * The deployment name is the name of the .esb archive without the .esb suffix.
     *
     * @return String The name of the deployment. This is the archive name without the .esb suffix.
     */
    public String getDeploymentName() {
        return _deploymentName;
    }

    
    /**
     * Getter for the file containing the SwitchYard configuration.
     * @return switchyardFile SwitchYard configuration file
     */
    public VirtualFile getSwitchYardFile() {
        return _switchYardFile;
    }

    /**
     * Setter for the file containing the SwitchYard configuration.
     * @param switchYardFile SwitchYard configuration file 
     */
    public void setSwitchYardFile(VirtualFile switchYardFile) {
        _switchYardFile = switchYardFile;
    }

    /**
     * Getter for SwitchYard Model.
     * @param switchYardModel the SwitchYardModel to set
     */
    public void setSwitchYardModel(SwitchYardModel switchYardModel) {
        this._switchYardModel = switchYardModel;
    }

    /**
     * Setter for SwitchYard Model.
     * @return the SwitchYardModel
     */
    public SwitchYardModel getSwitchYardModel() {
        return _switchYardModel;
    }
}
