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

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.deploy.internal.Deployment;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.TransformerFactory;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base class for writing SwitchYard tests.
 * <p/>
 * This class creates a {@link ServiceDomain} instance (via an {@link AbstractDeployment}) for your TestCase.  It
 * can be configured via the following annotations:
 * <ul>
 * <li>{@link TestMixIns}: This annotation allows you to "mix-in" the test behavior that your test requires
 * by listing {@link TestMixIn} types.  See the {@link org.switchyard.test.mixins} package for a list of the
 * {@link TestMixIn TestMixIns} available out of the box.  You can also implement your own {@link TestMixIn TestMixIn}.
 * (See {@link #getMixIn(Class)})</li>
 * <li>{@link SwitchYardDeploymentConfig}: Allows you to specify a SwitchYard application configuration file (switchyard.xml) to
 * be used to initialize your TestCase instance {@link ServiceDomain}.</li>
 * </ul>
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
    private List<Class<? extends TestMixIn>> _testMixIns;
    private List<TestMixIn> _testMixInInstances = new ArrayList<TestMixIn>();

    /**
     * Public default constructor.
     */
    public SwitchYardTestCase() {
        SwitchYardDeploymentConfig deploymentConfig = getClass().getAnnotation(SwitchYardDeploymentConfig.class);
        if (deploymentConfig != null && deploymentConfig.value() != null) {
            _configModel = createSwitchYardModel(getClass().getResourceAsStream(deploymentConfig.value()));
        }

        TestMixIns testMixInsAnnotation = getClass().getAnnotation(TestMixIns.class);
        if (testMixInsAnnotation != null) {
            _testMixIns = Arrays.asList(testMixInsAnnotation.value());
        }
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

    /**
     * Create a new {@link Transformer} instance from the specified {@link TransformModel}.
     * @param transformModel The TransformModel.
     * @return The Transformer instance.
     */
    public Transformer newTransformer(TransformModel transformModel) {
        return TransformerFactory.newTransformer(transformModel);
    }

    /**
     * Create a new {@link Transformer} instance from the specified {@link TransformModel} and
     * register it with the test ServiceDomain.
     * @param transformModel The TransformModel.
     * @return The Transformer instance.
     */
    public Transformer registerTransformer(TransformModel transformModel) {
        if (transformModel.getFrom() == null || transformModel.getTo() == null) {
            Assert.fail("Invalid TransformModel instance.  Must specify 'from' and 'to' data types.");
        }

        Transformer<?,?> transformer = TransformerFactory.newTransformer(transformModel);
        if (transformer.getFrom() == null) {
            transformer = new TransformerWrapper(transformer, transformModel);
        }
        _deployment.getDomain().getTransformerRegistry().removeTransformer(transformer);

        return transformer;
    }

    /**
     * Get the "active" {@link TestMixIn} instance of the specified type.
     * <p/>
     * This method can only be called from inside a test method.
     *
     * @param type The {@link TestMixIn} type, as specified in the {@link TestMixIns} annotation.
     * @param <T> type {@link TestMixIn} type.
     * @return The {@link TestMixIn} instance.
     */
    public <T extends TestMixIn> T getMixIn(Class<T> type) {
        if (_testMixIns == null || _testMixIns.isEmpty()) {
            Assert.fail("No TestMixIns specified on Test class instance.  Use the @TestMixIns annotation.");
        }
        if (_testMixIns.size() != _testMixInInstances.size()) {
            Assert.fail("TestMixIn instances only available during test method execution.");
        }

        int indexOf = _testMixIns.indexOf(type);
        if (indexOf == -1) {
            Assert.fail("No TestMixIn of type '' specified on the ");
        }

        return type.cast(_testMixInInstances.get(indexOf));
    }

    /**
     * Read a classpath resource and return as a byte array.
     * @param path The path to the classpath resource.  The specified path can be
     * relative to the test class' location on the classpath.
     * @return The resource as an array of bytes.
     */
    public byte[] readResourceBytes(String path) {
        if (path == null) {
            Assert.fail("Resource 'path' not specified.");
        }

        InputStream resourceStream = getClass().getResourceAsStream(path);
        if (resourceStream == null) {
            Assert.fail("Resource '" + path + "' not found on classpath relative to test class '" + getClass().getName() + "'.  May need to fix the relative path, or make the path absolute.");
        }

        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        try {
            byte[] readBuffer = new byte[128];
            int readCount = 0;

            while ((readCount = resourceStream.read(readBuffer)) != -1) {
                byteOutStream.write(readBuffer, 0, readCount);
            }
        } catch (IOException e) {
            Assert.fail("Unexpected read error reading classpath resource '" + path + "'" + e.getMessage());
        } finally {
            try {
                resourceStream.close();
            } catch (IOException e) {
                Assert.fail("Unexpected exception closing classpath resource '" + path + "'" + e.getMessage());
            }
        }

        return byteOutStream.toByteArray();
    }

    /**
     * Read a classpath resource and return as a String.
     * @param path The path to the classpath resource.  The specified path can be
     * relative to the test class' location on the classpath.
     * @return The resource as a String.
     */
    public String readResourceString(String path) {
        try {
            return new String(readResourceBytes(path), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Assert.fail("Unexpected exception reading classpath resource '" + path + "' as a String.  Perhaps this resource is a binary resource that cannot be encoded as a String." + e.getMessage());
            return null; // Keep the compiler happy.
        }
    }

    /**
     * Compare an XML string (e.g. a result) against a classpath resource.
     * @param xml The XML (as a String) to be compared against the XML in the specified
     * classpath resource.
     * @param resourcePath The path to the classpath resource against which the XML is to be
     * compared.  The specified path can be relative to the test class' location on the classpath.
     */
    public void compareXMLToResource(String xml, String resourcePath) {
        XMLUnit.setIgnoreWhitespace(true);
        try {
            XMLAssert.assertXMLEqual(readResourceString(resourcePath), xml);
        } catch (Exception e) {
            Assert.fail("Unexpected error performing XML comparison.");
        }
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

        if (_testMixIns == null || _testMixIns.isEmpty()) {
            // No Mix-Ins...
            return;
        }

        for (Class<? extends TestMixIn> mixInType : _testMixIns) {
            try {
                TestMixIn testMixIn = mixInType.newInstance();
                testMixIn.setTestCase(this);
                _testMixInInstances.add(testMixIn);
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

    private final class TransformerWrapper implements Transformer {

        private Transformer _transformer;
        private TransformModel _transformModel;

        private TransformerWrapper(Transformer transformer, TransformModel transformModel) {
            this._transformer = transformer;
            this._transformModel = transformModel;
        }

        @Override
        public Object transform(Object from) {
            return _transformer.transform(from);
        }

        @Override
        public Class getFromType() {
            return _transformer.getFromType();
        }

        @Override
        public Class getToType() {
            return _transformer.getToType();
        }

        @Override
        public QName getFrom() {
            return _transformModel.getFrom();
        }

        @Override
        public QName getTo() {
            return _transformModel.getTo();
        }
    }
}
