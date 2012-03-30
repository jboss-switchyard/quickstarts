/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.test.mixins.jca;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

import javax.resource.cci.MappedRecord;
import javax.resource.cci.MessageListener;
import javax.resource.spi.ResourceAdapter;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.jboss.as.connector.ConnectorServices;
import org.jboss.jca.core.spi.rar.ResourceAdapterRepository;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.test.ShrinkwrapUtil;
import org.switchyard.test.mixins.AbstractTestMixIn;


/**
 * JCA Test Mix In for deploying the IronJacamar Embedded.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public class JCAMixIn extends AbstractTestMixIn {

    private static final String MOCK_RESOURCE_ADAPTER_XML = "jcamixin-mock-ra.xml";
    private static final String HORNETQ_RESOURCE_ADAPTER_XML = "jcamixin-hornetq-ra.xml";
    private static final String ENV_HORNETQ_VERSION = "HORNETQ_VERSION";
    private static final String ENV_NETTY_VERSION = "NETTY_VERSION";
    
    private Logger _logger = Logger.getLogger(JCAMixIn.class);
    private SwitchYardIronJacamarHandler _ironJacamar;
    private ResourceAdapterRepository _resourceAdapterRepository; 
    private String _mockResourceAdapterName;
    private String _hornetqResourceAdapterName;
    
    @Override
    public void initialize() {
        try {
            _ironJacamar = new SwitchYardIronJacamarHandler();
            _ironJacamar.startup();
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail("Failed to start IronJacamar Embedded: " + t.getMessage());
        }
    }

    @Override
    public void before(AbstractDeployment deployment) {
        JCAMixInConfig config = getTestKit().getTestInstance()
                                            .getClass()
                                            .getAnnotation(JCAMixInConfig.class);
        // deploy built-in resource adapter..
        if (!config.mockResourceAdapter().equals("")) {
            _mockResourceAdapterName = config.mockResourceAdapter();
            deployMockResourceAdapter(_mockResourceAdapterName);
        }
        if (!config.hornetQResourceAdapter().equals("")) {
            _hornetqResourceAdapterName = config.hornetQResourceAdapter();
            deployHornetQResourceAdapter(_hornetqResourceAdapterName);
        }
        
        // deploy other resource adapter from RAR archive..
        for (String raName : config.resourceAdapters()) {
            deployResourceAdapter(raName);
        }
        
        try {
            _resourceAdapterRepository = _ironJacamar.getResourceAdapterRepository();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not acquire ResourceAdapterRepository");
        }
        
        for (Activator activator : getTestKit().getActivators()) {
            for (Field f : activator.getClass().getDeclaredFields()) {
                if (f.getType() == ResourceAdapterRepository.class) {
                    f.setAccessible(true);
                    try {
                        f.set(activator, _resourceAdapterRepository);
                    } catch (Exception e) {
                        _logger.warn("Failed to inject ResourceAdapterRepository into " + activator.getClass(), e);
                    } finally {
                    f.setAccessible(false);
                    }
                }
            }
        }
    }

    /**
     * get {@link MockResourceAdapter}.
     * 
     * @return {@link MockResourceAdapter}
     */
    public MockResourceAdapter getMockResourceAdapter() {
        return MockResourceAdapter.class.cast(getResourceAdapter(_mockResourceAdapterName));
    }
    
    /**
     * get {@link ResourceAdapter}.
     * 
     * @param name adapter name
     * 
     * @return {@link ResourceAdapter}
     */
    public ResourceAdapter getResourceAdapter(String name) {
        try {
            String raid = ConnectorServices.getRegisteredResourceAdapterIdentifier(stripDotRarSuffix(name));
            return _resourceAdapterRepository.getResourceAdapter(raid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * deploy rar archive.
     * 
     * @param path path of the rar archive to deploy
     */
    private void deployResourceAdapter(String path) {
        URI uri = null;
        try {
            uri = Thread.currentThread().getContextClassLoader().getResource(path).toURI();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        File ra = new File(uri);
        ResourceAdapterArchive raa =
                ShrinkWrap.createFromZipFile(ResourceAdapterArchive.class, ra);

        deployResourceAdapterArchive(raa);
    }
    
    /**
     * Create empty record.
     * 
     * @return empty Record instance
     */
    public MappedRecord createCCIMappedRecord() {
        return new MockMappedRecord();
    }
    
    @Override
    public void uninitialize() {
        try {
            _ironJacamar.shutdown();
            _ironJacamar = null;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    private void deployMockResourceAdapter(String raName) {
        ResourceAdapterArchive raa =
                ShrinkWrap.create(ResourceAdapterArchive.class, stripDotRarSuffix(raName) + ".rar");
        JavaArchive ja = ShrinkWrap.create(JavaArchive.class, UUID.randomUUID().toString() + ".jar");
        ja.addClasses(MessageListener.class, MockActivationSpec.class, MockConnection.class,
        MockConnectionFactory.class, MockConnectionManager.class,
        MockConnectionInterface.class, MockManagedConnection.class,
        MockManagedConnectionFactory.class, MockResourceAdapter.class);
        raa.addAsLibrary(ja);
        URL url = Thread.currentThread().getContextClassLoader().getResource(MOCK_RESOURCE_ADAPTER_XML);
        raa.setResourceAdapterXML(url);
        deployResourceAdapterArchive(raa);
    }

    private void deployHornetQResourceAdapter(String raName) {
        String hqVersion = System.getenv(ENV_HORNETQ_VERSION);
        String nettyVersion = System.getenv(ENV_NETTY_VERSION);
        
        ResourceAdapterArchive raa =
                ShrinkWrap.create(ResourceAdapterArchive.class, stripDotRarSuffix(raName) + ".rar");
        raa.addAsLibrary(ShrinkwrapUtil.getArchive("org.jboss.netty", "netty", nettyVersion, JavaArchive.class, "jar"));
        raa.addAsLibrary(ShrinkwrapUtil.getArchive("org.hornetq", "hornetq-ra", hqVersion, JavaArchive.class, "jar"));
        raa.addAsLibrary(ShrinkwrapUtil.getArchive("org.hornetq", "hornetq-core-client", hqVersion, JavaArchive.class, "jar"));
        raa.addAsLibrary(ShrinkwrapUtil.getArchive("org.hornetq", "hornetq-jms-client", hqVersion, JavaArchive.class, "jar"));
        URL url = Thread.currentThread().getContextClassLoader().getResource(HORNETQ_RESOURCE_ADAPTER_XML);
        raa.setResourceAdapterXML(url);

        deployResourceAdapterArchive(raa);
    }
    
    private void deployResourceAdapterArchive(ResourceAdapterArchive raa) {
        try {
            _ironJacamar.deploy(raa);
            String raname = stripDotRarSuffix(raa.getName());
            String raid = _ironJacamar.getResourceAdapterIdentifier(raname);

            ConnectorServices.registerResourceAdapter(raname);
            ConnectorServices.registerResourceAdapterIdentifier(raname, raid);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private String stripDotRarSuffix(final String raName) {
        if (raName == null) {
            return null;
        }
        // See RaDeploymentParsingProcessor
        if (raName.endsWith(".rar")) {
            return raName.substring(0, raName.indexOf(".rar"));
        }
        return raName;
    }
}
