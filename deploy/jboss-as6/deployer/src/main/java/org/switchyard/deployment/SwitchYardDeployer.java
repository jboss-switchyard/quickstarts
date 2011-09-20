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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.vfs.spi.deployer.AbstractSimpleVFSRealDeployer;
import org.jboss.deployers.vfs.spi.structure.VFSDeploymentUnit;
import org.switchyard.common.type.Classes;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.Component;
import org.switchyard.deploy.ServiceDomainManager;

/**
 * AS6 Deployer for SwitchYard.
 * 
 * The list of available components are provided as Map<String, Configuration>,
 * where the key is the implementation Class Name of that component.
 * 
 * <pre>
 * &lt;property name="modules"&gt;
 * &nbsp;&nbsp;&lt;map class="java.util.Hashtable" keyClass="java.lang.String" valueClass="org.switchyard.config.Configuration"&gt;
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;entry&gt;&lt;key&gt;org.switchyard.component.bean.deploy.BeanComponent&lt;/key&gt;&lt;value&gt;&lt;inject bean="BeanConfiguration"/&gt;&lt;/value&gt;&lt;/entry&gt;
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;entry&gt;&lt;key&gt;org.switchyard.component.bean.deploy.SOAPComponent&lt;/key&gt;&lt;value&gt;&lt;inject bean="SOAPConfiguration"/&gt;&lt;/value&gt;&lt;/entry&gt;
 * &nbsp;&nbsp;&lt;/map&gt;
 * &lt;/property&gt;
 * </pre>
 * 
 * The configuration is read from a file.
 * 
 * <pre>
 * &lt;bean name="ConfigPuller" class="org.switchyard.config.ConfigurationPuller"/&gt;
 * 
 * &lt;bean name="SOAPConfiguration" class="org.switchyard.config.Configuration"&gt;
 * &nbsp;&nbsp;&lt;constructor factoryMethod="pull"&gt;
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;factory bean="ConfigPuller"/&gt;
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;parameter class="java.io.Reader"&gt;
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;inject bean="soap"/&gt;
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/parameter&gt;
 * &nbsp;&nbsp;&lt;/constructor&gt;
 * &lt;/bean&gt;
 * 
 * &lt;bean name="soap" class="java.io.FileReader"&gt;
 * &nbsp;&nbsp;&lt;constructor&gt;&lt;parameter class="java.io.File"&gt;${jboss.server.home.dir}/deployers/switchyard.deployer/META-INF/soapconfig.xml&lt;/parameter>&lt;/constructor&gt;
 * &lt;/bean&gt;
 * </pre>
 * 
 * The contents of the configuration file should start with properties. For example:
 * <pre>
 * &lt;properties&gt;
 * &nbsp;&nbsp;&lt;serverHost&gt;localhost&lt;/serverHost&gt;
 * &nbsp;&nbsp;&lt;serverPort&gt;18001&lt;/serverPort&gt;
 * &lt;/properties&gt;
 * </pre>
 */
public class SwitchYardDeployer extends AbstractSimpleVFSRealDeployer<SwitchYardMetaData> {

    private Logger _log = Logger.getLogger(SwitchYardConfigParser.class);

    private static final String BEAN_PREFIX = "switchyard";

    private ServiceDomainManager _domainManager;

    private List<Component> _components;

    /**
     * No args constructor.
     */
    public SwitchYardDeployer() {
        super(SwitchYardMetaData.class);
        setOutput(BeanMetaData.class);
        setStage(DeploymentStages.REAL);
    }

    /**
     * Set the {@link ServiceDomainManager} instance for the deployment.
     * @param domainManager The domain manager.
     */
    public void setDomainManager(ServiceDomainManager domainManager) {
        this._domainManager = domainManager;
    }

    /**
     * Set the list of configured components.
     *
     * @param modules The list of modules with its configuration.
     */
    public void setModules(Map<String, Configuration> modules) {
        List<Component> components = new ArrayList<Component>();
        Set<Entry<String,Configuration>> entries = modules.entrySet();
        for (Entry<String,Configuration> entry : entries) {
            String className = entry.getKey();
            try {
                Class<?> componentClass = Classes.forName(className);
                if (componentClass != null) {
                    Component component = (Component) componentClass.newInstance();
                    component.init(entry.getValue());
                    components.add(component);
                } else {
                    _log.error("Unable to load class " + className);
                }
            } catch (InstantiationException e2) {
                _log.error("Unable to instantiate class " + className);
            } catch (IllegalAccessException e3) {
                _log.error("Unable to access constructor for " + className);
            }
        }
        this._components = components;
    }

    @Override
    public void deploy(VFSDeploymentUnit unit, SwitchYardMetaData metaData)
        throws DeploymentException {
        try {
            parseSwitchYardConfig(metaData);
            _log.debug("Successfully parsed SwitchYard configuration for deployment unit '" + unit.getName() + "'.");
        } catch (IOException ioe) {
            throw new DeploymentException(ioe);
        }
        BeanMetaData beanMetaData = createBeanMetaData(unit, metaData);
        unit.addAttachment(BeanMetaData.class, beanMetaData);
        unit.addAttachment("components", _components);
    }
    
    /**
     * Parse the SwitchYard configuration
     * @param metaData SwitchYard MetaData
     * @throws IOException IOException
     */
    private void parseSwitchYardConfig(SwitchYardMetaData metaData) throws IOException {
        InputStream is = metaData.getSwitchYardFile().openStream();
        try {
            SwitchYardModel switchyardModel = new ModelPuller<SwitchYardModel>().pull(is);
            metaData.setSwitchYardModel(switchyardModel);
        } finally {
            is.close();
        }
    }
    
    /**
     * Creates a {@link BeanMetaData} that describes the {@link SwitchYardDeployment} class.
     *
     * @param deploymentUnit The deployment unit to deploy.
     * @param metadata The SwitchYard MetaData that is associated with the deployment unit.
     * @return BeanMetaData The {@link BeanMetaData} describing the SwitchYardDeployment class
     */
    private BeanMetaData createBeanMetaData(final DeploymentUnit deploymentUnit, final SwitchYardMetaData metadata)
    {
        BeanMetaDataBuilder bmdBuilder = BeanMetaDataBuilder.createBuilder(BEAN_PREFIX + "." + deploymentUnit.getName(), SwitchYardDeployment.class.getName());
        // Setup the first constructor argument.
        bmdBuilder.addConstructorParameter(String.class.getName(), metadata.getArchiveName());
        // Setup the second constructor argument.
        bmdBuilder.addConstructorParameter(VFSDeploymentUnit.class.getName(), deploymentUnit);
        // Setup the third constructor argument.
        bmdBuilder.addConstructorParameter(SwitchYardModel.class.getName(), metadata.getSwitchYardModel());
        // Setup the fourth constructor argument.
        bmdBuilder.addConstructorParameter(ServiceDomainManager.class.getName(), _domainManager);

        bmdBuilder.addDependency(deploymentUnit.getName());
        bmdBuilder.addDependency(deploymentUnit.getName() + "_WeldBootstrapBean");

        _log.debug("Successfully created AS6 BeanMetaData instance for deployment unit '" + deploymentUnit.getName() + "'.");

        return bmdBuilder.getBeanMetaData();
    }

}
