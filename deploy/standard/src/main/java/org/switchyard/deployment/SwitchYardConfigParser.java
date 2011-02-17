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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.jboss.deployers.spi.DeploymentException;

import org.apache.log4j.Logger;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.vfs.spi.deployer.AbstractVFSParsingDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.jboss.vfs.VirtualFile;
import org.w3c.dom.Element;

import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;


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
    private static final String SWITCHYARD_DEPLOYMENT_XML = META_INF_DIR + "/"
    	+ SWITCHYARD_XML_FILE;
    
    /**
     * Extension of a JAR file.
     */
    private static final String JAR_FILE_EXTENSION = ".jar";
        
    /**
     * Deployment prefix for SwitchYard deployments.
     */
    private String switchyardDeploymentName = "org.switchyard:deployment=";
    
    /**
     * Logger.
     */
    private Logger log = Logger.getLogger(SwitchYardConfigParser.class);

    /**
     * Sole constructor that performs the following steps:
     * <lu>
     *  <li>Sets the output of this deployer to be {@link SwitchYardMetaData}.</li>
     *  <li>Sets this deployers deployment stage to {@link DeploymentStages#PARSE}./li>
     * </lu>
     */
    public SwitchYardConfigParser()
    {
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
    		final SwitchYardMetaData metadata) throws Exception
    {
        VirtualFile configFile = findSwitchYardConfigFile(file);
        final String archiveName = deploymentUnit.getSimpleName();
        final String deploymentName = getDeploymentName(deploymentUnit);
        
        if (!configFile.isFile()) {
        	throw new DeploymentException("Could not find " + SWITCHYARD_DEPLOYMENT_XML);
        }
        
        final SwitchYardMetaData switchYardMetaData = new SwitchYardMetaData(archiveName, deploymentName);
        log.debug("Parsed SwitchYard configuration'" + switchYardMetaData + "'");
        InputStream is = configFile.openStream();

        // TODO : Pulling the config in isn't working.  The stream is fine, 
        // tested that throught System.out.println.    Fix this.
        // TODO : Rather than pull in the CompositeModel, pull in the 
        // the Switchyard config like David demonstrates in http://community.jboss.org/thread/162741?tstart=0
        CompositeModel compositeModel = (CompositeModel) new ModelResource().pull(is);
        switchYardMetaData.setCompositeModel(compositeModel);
        
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
    private VirtualFile findSwitchYardConfigFile(final VirtualFile file) throws DeploymentException, IOException
    {
        if (file.getName().endsWith(SWITCHYARD_XML_FILE)) {
            return file;
        }
       
        VirtualFile child = file.getChild(META_INF_DIR);
        VirtualFile xml = child.getChild(SWITCHYARD_DEPLOYMENT_XML);
        log.info("META-INF : " + xml);
        return xml;
     }    
    
    String getDeploymentName(final VFSDeploymentUnit deploymentUnit)
    {
        final String simpleName = deploymentUnit.getSimpleName();
        int idx = simpleName.indexOf(JAR_FILE_EXTENSION);
        if (idx == -1) {
            return simpleName;
        }
        return simpleName.substring(0, simpleName.indexOf(JAR_FILE_EXTENSION));
    }    
}
