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

package org.switchyard.deployment;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.vfs.VirtualFile;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.switchyard.SwitchYardModel;


/**
 * SwitchYardConfigParser is a Microcontainer deployer that picks up switchyard.xml files, parses the content
 * and produces an {@link SwitchYardMetaData} instance.
 * <p/>
 */
public class SwitchYardConfigParser extends AbstractVFSParsingDeployer<SwitchYardMetaData>
{
    /**
     * Location of the meta-inf directory.
     */
    private static final String META_INF_DIR = "META_INF";

    /**
     * Name of the switchyard configuration file.
     */
    private static final String SWITCHYARD_XML_FILE = "switchyard.xml";

    /**
     * Name and path of the SwitchYard deployment.xml file.
     */
    private static final String SWITCHYARD_DEPLOYMENT_XML = META_INF_DIR + "/" + SWITCHYARD_XML_FILE;

    /**
     * Extension of a JAR file.
     */
    private static final String JAR_FILE_EXTENSION = ".jar";

    /**
     * Logger.
     */
    private Logger _log = Logger.getLogger(SwitchYardConfigParser.class);

    /**
     * Sole constructor that performs the following steps:
     * <ul>
     *  <li>Sets the output of this deployer to be {@link SwitchYardMetaData}.</li>
     *  <li>Sets this deployers deployment stage to {@link DeploymentStages#PARSE}.</li>
     * </ul>
     */
    public SwitchYardConfigParser() {
        super(SwitchYardMetaData.class);
        setSuffix(SWITCHYARD_XML_FILE);
        setJarExtension(JAR_FILE_EXTENSION);
        setStage(DeploymentStages.PARSE);
    }

    /**
     * Will parse the VirtualFile representing the deployment and parse the SwitchYard configuration xml and extract information from 
     * the archive to create an {@link SwitchYardMetaData} instance that will be returned.
     */
    @Override
    protected SwitchYardMetaData parse(final VFSDeploymentUnit deploymentUnit, final VirtualFile file, 
            final SwitchYardMetaData metadata) throws Exception {
        VirtualFile configFile = findSwitchYardConfigFile(file);
        final String archiveName = deploymentUnit.getSimpleName();
        final String deploymentName = getDeploymentName(deploymentUnit);

        if (!configFile.isFile()) {
            throw new DeploymentException("Could not find " + SWITCHYARD_DEPLOYMENT_XML);
        }

        final SwitchYardMetaData switchYardMetaData = new SwitchYardMetaData(archiveName, deploymentName);
        _log.debug("Parsed SwitchYard configuration'" + switchYardMetaData + "'");
        switchYardMetaData.setSwitchYardFile(configFile);
        
        return switchYardMetaData;
    }

    /**
     * Tries to find a file called "switchyard.xml".
     *
     * @param file          The virtual file. Can point to a file or a directory which will be searched.
     * @return VirtualFile  VirtualFile representing a found configuration file.
     * @throws DeploymentException If not configuration file could be found, or more than one was found.
     * @throws IOException 
     */
    private VirtualFile findSwitchYardConfigFile(final VirtualFile file) throws DeploymentException, IOException {
        if (file.getName().endsWith(SWITCHYARD_XML_FILE)) {
            return file;
        }

        VirtualFile child = file.getChild(META_INF_DIR);
        VirtualFile xml = child.getChild(SWITCHYARD_DEPLOYMENT_XML);
        return xml;
    }    

    String getDeploymentName(final VFSDeploymentUnit deploymentUnit) {
        final String simpleName = deploymentUnit.getSimpleName();
        int idx = simpleName.indexOf(JAR_FILE_EXTENSION);
        if (idx == -1) {
            return simpleName;
        }
        return simpleName.substring(0, simpleName.indexOf(JAR_FILE_EXTENSION));
    }    
}
