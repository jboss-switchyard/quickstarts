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

package org.switchyard.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.deploy.internal.Deployment;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.transform.Transformer;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for writing SwitchYard tests.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public abstract class SwitchYardTestCase {

    /**
     * Test configuration model.
     */
    private SwitchYardModel _configModel;
    /**
     * The deployment.
     */
    private AbstractDeployment _deployment;
    /**
     * Test Mix-Ins.
     */
    private TestMixIns _testMixIns;
    private List<TestMixIn> _testMixInInstances = new ArrayList<TestMixIn>();

    /**
     * Public default constructor.
     */
    public SwitchYardTestCase() {
        SwitchYardDeploymentConfig deploymentConfig = getClass().getAnnotation(SwitchYardDeploymentConfig.class);
        if (deploymentConfig != null && deploymentConfig.value() != null) {
            _configModel = createSwitchYardModel(getClass().getResourceAsStream(deploymentConfig.value()));
        }
        _testMixIns = getClass().getAnnotation(TestMixIns.class);
    }

    /**
     * Public constructor.
     * @param configModel Configuration model stream.
     */
    public SwitchYardTestCase(InputStream configModel) {
        _configModel = createSwitchYardModel(configModel);
    }

    /**
     * Public constructor.
     * <p/>
     * Loads the config model from the classpath.
     *
     * @param configModelPath Configuration model classpath path.
     */
    public SwitchYardTestCase(String configModelPath) {
        Assert.assertNotNull("Test 'configModel' is null.", configModelPath);
        _configModel = createSwitchYardModel(getClass().getResourceAsStream(configModelPath));
    }

    /**
     * Public constructor.
     * @param configModel Configuration model.
     */
    public SwitchYardTestCase(SwitchYardModel configModel) {
        Assert.assertNotNull("Test 'configModel' is null.", configModel);
        _configModel = configModel;
    }

    /**
     * Get the configuration model driving this test instance, if one exists.
     * <p/>
     * An abstract deployment is created if no configuration model is supplied on construction.
     *
     * @return The config model, or null if no config model was used to construct the TestCase instance.
     */
    public SwitchYardModel getConfigModel() {
        return _configModel;
    }

    /**
     * Create and initialise the deployment.
     * @throws Exception creating the deployment.
     */
    @Before
    public final void deploy() throws Exception {
        MockInitialContextFactory.install();
        createMixInInstances();
        mixInSetup();
        _deployment = createDeployment();
        _deployment.init();
        mixInBefore();
        _deployment.start();
    }

    /**
     * Undeploy the deployment.
     */
    @After
    public final void undeploy() {
        assertDeployed();
        _deployment.stop();
        mixInAfter();
        _deployment.destroy();
        mixInTearDown();
        MockInitialContextFactory.clear();
    }

    /**
     * Create the deployment instance.
     * @return The deployment instance.
     * @throws Exception creating the deployment.
     */
    protected AbstractDeployment createDeployment() throws Exception {
        if (_configModel != null) {
            return new Deployment(_configModel);
        } else {
            return new SimpleTestDeployment();
        }
    }

    /**
     * Get the ServiceDomain.
     * @return The service domain.
     */
    public ServiceDomain getServiceDomain() {
        assertDeployed();
        return _deployment.getDomain();
    }

    /**
     * Register an IN_OUT Service.
     * <p/>
     * Registers a {@link MockHandler} as the service handler.
     *
     * @param serviceName The Service name.
     * @return The {@link MockHandler} service handler.
     */
    protected MockHandler registerInOutService(String serviceName) {
        MockHandler handler = new MockHandler();
        getServiceDomain().registerService(new QName(serviceName), handler, new InOutService());
        return handler;
    }

    /**
     * Register an IN_OUT Service.
     *
     * @param serviceName The Service name.
     * @param serviceHandler The service handler.
     */
    protected void registerInOutService(String serviceName, ExchangeHandler serviceHandler) {
        getServiceDomain().registerService(new QName(serviceName), serviceHandler, new InOutService());
    }

    /**
     * Register an IN_ONLY Service.
     * <p/>
     * Registers a {@link MockHandler} as the fault service handler.
     *
     * @param serviceName The Service name.
     * @return The {@link MockHandler} service fault handler.
     */
    protected MockHandler registerInOnlyService(String serviceName) {
        MockHandler handler = new MockHandler();
        getServiceDomain().registerService(new QName(serviceName), handler, new InOnlyService());
        return handler;
    }

    /**
     * Register an IN_ONLY Service.
     *
     * @param serviceName The Service name.
     * @param serviceHandler The service handler.
     */
    protected void registerInOnlyService(String serviceName, ExchangeHandler serviceHandler) {
        getServiceDomain().registerService(new QName(serviceName), serviceHandler, new InOnlyService());
    }

    /**
     * Add a Transformer instance.
     * @param transformer The transformer instance.
     */
    public void addTransformer(Transformer transformer) {
        getServiceDomain().getTransformerRegistry().addTransformer(transformer);
    }

    /**
     * Create a new {@link Invoker} instance for invoking a Service in the test ServiceDomain.
     * @param serviceName The target Service name.
     * @return The invoker instance.
     */
    protected Invoker newInvoker(QName serviceName) {
        return new Invoker(getServiceDomain(), serviceName);
    }

    /**
     * Create a new {@link Invoker} instance for invoking a Service in the test ServiceDomain.
     * @param serviceName The target Service name.  Can be a serialized {@link QName}.  Can also
     * include the operation name e.g. "OrderManagementService.createOrder".
     * @return The invoker instance.
     */
    protected Invoker newInvoker(String serviceName) {
        return new Invoker(getServiceDomain(), serviceName);
    }

    private void mixInSetup() {
        for (TestMixIn mixIn : _testMixInInstances) {
            mixIn.setUp();
        }
    }

    private void mixInBefore() {
        for (TestMixIn mixIn : _testMixInInstances) {
            mixIn.before(_deployment);
        }
    }

    private void mixInAfter() {
        // Apply after MixIns in reverse order...
        for (int i = _testMixInInstances.size() - 1; i >= 0; i--) {
            _testMixInInstances.get(i).after(_deployment);
        }
    }

    private void mixInTearDown() {
        // TearDown MixIns in reverse order...
        for (int i = _testMixInInstances.size() - 1; i >= 0; i--) {
            _testMixInInstances.get(i).tearDown();
        }
    }

    private void createMixInInstances() {
        _testMixInInstances.clear();

        if (_testMixIns == null) {
            // No Mix-Ins...
            return;
        }

        for (Class<? extends TestMixIn> mixInType : _testMixIns.value()) {
            try {
                _testMixInInstances.add(mixInType.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail("Failed to create instance of TestMixIn type " + mixInType.getName() + ".  Make sure it defines a public default constructor.");
            }
        }
    }

    private void assertDeployed() {
        if (_deployment == null) {
            Assert.fail("TestCase deployment not yet deployed.  You may need to make an explicit call to the deploy() method.");
        }
    }

    private static SwitchYardModel createSwitchYardModel(InputStream configModel) {
        Assert.assertNotNull("Test 'configModel' is null.", configModel);
        try {
            return new ModelResource<SwitchYardModel>().pull(configModel);
        } catch (java.io.IOException ioEx) {
            throw new RuntimeException("Failed to read switchyard config.", ioEx);
        }
    }
}
