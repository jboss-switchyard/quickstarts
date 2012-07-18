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
package org.switchyard.as7.extension.deployment;

import org.jboss.as.server.deployment.AttachmentKey;
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
