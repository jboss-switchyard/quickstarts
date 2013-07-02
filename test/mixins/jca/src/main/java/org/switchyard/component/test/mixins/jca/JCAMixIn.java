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
package org.switchyard.component.test.mixins.jca;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.resource.cci.MessageListener;
import javax.resource.spi.ResourceAdapter;
import javax.transaction.UserTransaction;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.hornetq.core.version.Version;
import org.hornetq.utils.VersionLoader;
import org.jboss.as.connector.util.ConnectorServices;
import org.jboss.jca.core.spi.rar.ResourceAdapterRepository;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.test.MixInDependencies;
import org.switchyard.test.ShrinkwrapUtil;
import org.switchyard.test.mixins.AbstractTestMixIn;
import org.switchyard.component.test.mixins.naming.NamingMixIn;
import org.switchyard.component.test.mixins.transaction.TransactionMixInParticipant;

import com.arjuna.ats.jta.common.JTAEnvironmentBean;

/**
 * JCA Test Mix In for deploying the IronJacamar Embedded.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
@MixInDependencies(required={NamingMixIn.class})
public class JCAMixIn extends AbstractTestMixIn implements TransactionMixInParticipant {

    private static final String JNDI_PREFIX = "java:jboss";
    private static final String JNDI_USER_TRANSACTION = JNDI_PREFIX + "/UserTransaction";
    private static final String HORNETQ_DEFAULT_CF_JNDI = "java:/JmsXA";
    private static final String HORNETQ_DEFAULT_MCF_CLASS = "org.hornetq.ra.HornetQRAManagedConnectionFactory";
    private static final String MOCK_DEFAULT_MCF_CLASS = "org.switchyard.test.mixins.jca.MockManagedConnectionFactory";
    private static final String MOCK_RESOURCE_ADAPTER_XML = "jcamixin-mock-ra.xml";
    private static final String HORNETQ_RESOURCE_ADAPTER_XML = "jcamixin-hornetq-ra.xml";
    
    private Logger _logger = Logger.getLogger(JCAMixIn.class);
    private SwitchYardIronJacamarHandler _ironJacamar;
    private ResourceAdapterRepository _resourceAdapterRepository; 
    
    @Override
    public void initialize() {
        super.initialize();
        try {
            _ironJacamar = new SwitchYardIronJacamarHandler();
            _ironJacamar.startup();
            _resourceAdapterRepository = _ironJacamar.getResourceAdapterRepository();
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail("Failed to start IronJacamar Embedded: " + t.getMessage());
        }
    }

    @Override
    public void before(AbstractDeployment deployment) {
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
     * deploy resource adapters.
     * 
     * @param adapters to deploy
     */
    public void deployResourceAdapters(ResourceAdapterConfig... adapters) {
        for (ResourceAdapterConfig adapter : adapters) {
            switch (adapter.getType()) {
            case MOCK:
                deployMockResourceAdapter(adapter.getName(), adapter.getConnectionDefinitions());
                break;
            case HORNETQ:
                deployHornetQResourceAdapter(adapter.getName(), adapter.getConnectionDefinitions());
                break;
            default:
                deployResourceAdapter(adapter.getName(), adapter.getConnectionDefinitions());
            }
        }
    }
    
    @Override
    public void uninitialize() {
        try {
            UserTransaction tx = getUserTransaction();
            if (tx.getStatus() != javax.transaction.Status.STATUS_NO_TRANSACTION) {
                _logger.warn("Invalid transaction status[" + tx.getStatus() + "] ...trying to rollback");
                tx.rollback();
            }
        } catch (Exception e) {
            _logger.warn("Failed to rollback transaction: " + e.getMessage());
            if (_logger.isDebugEnabled()) {
                e.printStackTrace();
            }
        }

        try {
            _ironJacamar.shutdown();
            _ironJacamar = null;
        } catch (Throwable t) {
            _logger.warn("An error has occured during shutting down IronJacamar embedded: " + t.getMessage());
            if (_logger.isDebugEnabled()) {
                t.printStackTrace();
            }
        }
        super.uninitialize();
    }

    /**
     * get UserTransaction.
     * 
     * @return UserTransaction
     */
    public UserTransaction getUserTransaction() {
        try {
            InitialContext ic = new InitialContext();
            return (UserTransaction) ic.lookup(JNDI_USER_TRANSACTION);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JTAEnvironmentBean locateEnvironmentBean() throws Throwable {
        return _ironJacamar.getEnvironmentBean();
    }

    // TODO support arbitrary message listener interface for inflow
    // TODO support self-made ConnectionFactory to mock up their own EIS's API for outbound
    private void deployMockResourceAdapter(String raName, Map<String, String> connDefs) {
        ResourceAdapterArchive raa =
                ShrinkWrap.create(ResourceAdapterArchive.class, stripDotRarSuffix(raName == null ? "mock-ra.rar" : raName) + ".rar");
        JavaArchive ja = ShrinkWrap.create(JavaArchive.class, UUID.randomUUID().toString() + ".jar");
        ja.addClasses(MessageListener.class, MockActivationSpec.class, MockConnection.class,
        MockConnectionFactory.class, MockConnectionManager.class,
        MockManagedConnection.class, MockManagedConnectionFactory.class,
        MockResourceAdapter.class, TransactionManagerLocator.class);
        raa.addAsLibrary(ja);
        URL url = Thread.currentThread().getContextClassLoader().getResource(MOCK_RESOURCE_ADAPTER_XML);
        raa.setResourceAdapterXML(url);
        
        if (connDefs.size() == 0) {
            connDefs.put(JNDI_PREFIX + "/" + stripDotRarSuffix(raName), MOCK_DEFAULT_MCF_CLASS);
        }
        deployResourceAdapterArchive(raa, connDefs);
    }

    private void deployHornetQResourceAdapter(String raName, Map<String, String> connDefs) {
        Version version = VersionLoader.getVersion();
        String hqVersion = version.getMajorVersion()
                + "." + version.getMinorVersion()
                + "." + version.getMicroVersion()
                + "." + version.getVersionSuffix();
        String nettyVersion = org.jboss.netty.util.Version.ID;
        if (nettyVersion.indexOf('-') != -1) {
            nettyVersion = nettyVersion.substring(0, nettyVersion.indexOf('-'));
        }
        
        ResourceAdapterArchive raa =
                ShrinkWrap.create(ResourceAdapterArchive.class, stripDotRarSuffix(raName == null ? "hornetq-ra.rar" : raName) + ".rar");
        raa.addAsLibrary(ShrinkwrapUtil.getArchive("io.netty", "netty", nettyVersion, JavaArchive.class, "jar"));
        raa.addAsLibrary(ShrinkwrapUtil.getArchive("org.hornetq", "hornetq-ra", hqVersion, JavaArchive.class, "jar"));
        raa.addAsLibrary(ShrinkwrapUtil.getArchive("org.hornetq", "hornetq-core-client", hqVersion, JavaArchive.class, "jar"));
        raa.addAsLibrary(ShrinkwrapUtil.getArchive("org.hornetq", "hornetq-jms-client", hqVersion, JavaArchive.class, "jar"));
        URL url = Thread.currentThread().getContextClassLoader().getResource(HORNETQ_RESOURCE_ADAPTER_XML);
        raa.setResourceAdapterXML(url);

        if (connDefs.size() == 0) {
            connDefs.put(HORNETQ_DEFAULT_CF_JNDI, HORNETQ_DEFAULT_MCF_CLASS);
        }
        deployResourceAdapterArchive(raa, connDefs);
    }
    
    private void deployResourceAdapter(String path, Map<String, String> connDefs) {
        URI uri = null;
        try {
            uri = Thread.currentThread().getContextClassLoader().getResource(path).toURI();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        File ra = new File(uri);
        ResourceAdapterArchive raa =
                ShrinkWrap.createFromZipFile(ResourceAdapterArchive.class, ra);

        deployResourceAdapterArchive(raa, connDefs);
    }
    
    private void deployResourceAdapterArchive(ResourceAdapterArchive raa, Map<String, String> connDefs) {
        try {
            _ironJacamar.deploy(raa, connDefs);
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
