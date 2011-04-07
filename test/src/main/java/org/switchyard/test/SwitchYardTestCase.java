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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.common.type.Classes;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.config.model.MergeScanner;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.Models;
import org.switchyard.config.model.Scannable;
import org.switchyard.config.model.Scanner;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.ScannerOutput;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.deploy.internal.Deployment;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.test.mixins.AbstractTestMixIn;
import org.switchyard.transform.BaseTransformer;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.TransformerFactory;
import org.w3c.dom.Document;

/**
 * Base class for writing SwitchYard tests.
 * <p/>
 * This class creates a {@link ServiceDomain} instance (via an {@link AbstractDeployment}) for your TestCase.  It
 * can be configured via the {@link SwitchYardTestCaseConfig} annotation:
 * <ul>
 * <li><b>mixins</b>:  This value allows you to "mix-in" the test behavior that your test requires
 * by listing {@link TestMixIn} types.  See the {@link org.switchyard.test.mixins} package for a list of the
 * {@link TestMixIn TestMixIns} available out of the box.  You can also implement your own {@link TestMixIn TestMixIn}.
 * (See {@link #getMixIn(Class)})</li>
 * <li><b>config</b>:  This value allows you to specify a SwitchYard application configuration file (switchyard.xml) to
 * be used to initialize your TestCase instance {@link ServiceDomain}.</li>
 * <li><b>scanners</b>:  This value allows you to specify which {@link Scanner Scanners} are to be used to augment the
 * configuration model.</li>
 * </ul>
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@RunWith(SwitchYardTestCase.TestRunner.class)
public abstract class SwitchYardTestCase {

    /**
     * Logger.
     */
    private static Logger _logger = Logger.getLogger(SwitchYardTestCase.class);
    /**
     * Constant for the {@link org.switchyard.test.SwitchYardTestCaseConfig#config()} default.
     */
    protected static final String NULL_CONFIG = "$$NULL_SW_CONFIG$$";

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

    private static final ThreadLocal<TestRunner> TEST_RUNNER = new ThreadLocal<TestRunner>();

    /**
     * Public default constructor.
     */
    public SwitchYardTestCase() {
        SwitchYardTestCaseConfig testCaseConfig = getClass().getAnnotation(SwitchYardTestCaseConfig.class);

        if (testCaseConfig != null) {
            String config = testCaseConfig.config();
            if (config != null && !config.equals(NULL_CONFIG)) {
                InputStream is = null;
                try {
                    is = getResourceAsStream(config);
                    _configModel = createSwitchYardModel(is, createScanners(testCaseConfig));
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (Throwable t) {
                        // just to keep checkstyle happy
                        t.getMessage();
                    }
                }
            }
            Class<? extends TestMixIn>[] testMixIns = testCaseConfig.mixins();
            if (testMixIns == null) {
                // No MixIns...
                _logger.debug("No TestMixIns for test.");
            } else if (testMixIns.length == 1 && testMixIns[0] == NullMixIns.class) {
                // No MixIns...
                _logger.debug("No TestMixIns for test.");
            } else {
                _testMixIns = Arrays.asList(testMixIns);
            }
        }
        MockInitialContextFactory.install();
        createMixInInstances();
        initializeMixIns();

        TEST_RUNNER.get().setTestCase(this);
    }

    /**
     * Cleanup
     */
    private void cleanup() {
        cleanupMixIns();
        MockInitialContextFactory.clear();
    }

    /**
     * Public constructor.
     * @param configModel Configuration model stream.
     */
    public SwitchYardTestCase(InputStream configModel) {
        SwitchYardTestCaseConfig testCaseConfig = getClass().getAnnotation(SwitchYardTestCaseConfig.class);
        _configModel = createSwitchYardModel(configModel, createScanners(testCaseConfig));
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
        SwitchYardTestCaseConfig testCaseConfig = getClass().getAnnotation(SwitchYardTestCaseConfig.class);
        InputStream is = null;
        try {
            is = getResourceAsStream(configModelPath);
            _configModel = createSwitchYardModel(is, createScanners(testCaseConfig));
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Throwable t) {
                // just to keep checkstyle happy
                t.getMessage();
            }
        }
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
    }

    /**
     * Create the deployment instance.
     * @return The deployment instance.
     * @throws Exception creating the deployment.
     */
    protected AbstractDeployment createDeployment() throws Exception {
        if (_configModel != null) {
            return new Deployment(ServiceDomainManager.createDomain(), _configModel);
        } else {
            return new SimpleTestDeployment(ServiceDomainManager.createDomain());
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
        getServiceDomain().registerService(QName.valueOf(serviceName), handler, new InOutService());
        return handler;
    }

    /**
     * Register an IN_OUT Service.
     *
     * @param serviceName The Service name.
     * @param serviceHandler The service handler.
     */
    protected void registerInOutService(String serviceName, ExchangeHandler serviceHandler) {
        getServiceDomain().registerService(QName.valueOf(serviceName), serviceHandler, new InOutService());
    }

    /**
     * Register an IN_OUT Service.
     *
     * @param serviceName The Service name.
     * @param serviceHandler The service handler.
     * @param metadata Service interface.
     */
    protected void registerInOutService(String serviceName, ExchangeHandler serviceHandler, ServiceInterface metadata) {
        getServiceDomain().registerService(QName.valueOf(serviceName), serviceHandler, metadata);
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
        getServiceDomain().registerService(QName.valueOf(serviceName), handler, new InOnlyService());
        return handler;
    }

    /**
     * Register an IN_ONLY Service.
     *
     * @param serviceName The Service name.
     * @param serviceHandler The service handler.
     */
    protected void registerInOnlyService(String serviceName, ExchangeHandler serviceHandler) {
        getServiceDomain().registerService(QName.valueOf(serviceName), serviceHandler, new InOnlyService());
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
     * @param type The {@link TestMixIn} type, as specified in the {@link SwitchYardTestCaseConfig} annotation.
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
            Assert.fail("Required TestMixIn '" + type.getName() + "' is not specified on TestCase '" + getClass().getName() + "'.");
        }

        return type.cast(_testMixInInstances.get(indexOf));
    }

    /**
     * Finds a resource with a given name.
     * <p/>
     * Searches relative to the implementing class definition.
     *
     * @param name Name of the desired resource
     * @return A {@link java.io.InputStream} object or <tt>null</tt> if no resource with this name is found.
     *
     * @see Classes#getResourceAsStream(String,Class)
     */
    public InputStream getResourceAsStream(String name) {
        try {
            return Classes.getResourceAsStream(name, getClass());
        } catch (IOException ioe) {
            return null;
        }
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

        InputStream resourceStream = getResourceAsStream(path);
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
     * Read a classpath resource and return as an XML DOM Document.
     *
     * @param path The path to the classpath resource.  The specified path can be
     * relative to the test class' location on the classpath.
     * @return The resource as a Document.
     */
    public Document readResourceDocument(String path) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(getResourceAsStream(path));
        } catch (Exception e) {
            Assert.fail("Unexpected exception reading classpath resource '" + path + "' as a DOM Document." + e.getMessage());
            return null; // Keep the compiler happy.
        }
    }

    /**
     * Load the SwitchYard configuration model specified by the configModel stream.
     * @param configModel The config model stream.
     * @return The SwitchYard config model.
     */
    public SwitchYardModel loadSwitchYardModel(InputStream configModel) {
        return loadConfigModel(configModel, SwitchYardModel.class);
    }

    /**
     * Load the SwitchYard configuration model specified by the configModel stream.
     * @param <M> Model type.
     * @param configModel The config model stream.
     * @param modelType Model type.
     * @return The SwitchYard config model.
     */
    public <M extends Model> M loadConfigModel(InputStream configModel, Class<M> modelType) {
        if (configModel == null) {
            throw new IllegalArgumentException("null 'configModel' arg.");
        }
        try {
            return new ModelResource<M>().pull(configModel);
        } catch (IOException e) {
            Assert.fail("Unexpected error building " + modelType.getSimpleName() + ": " + e.getMessage());
        } finally {
            try {
                configModel.close();
            } catch (IOException e) {
                Assert.fail("Unexpected error closing " + modelType.getSimpleName() + " stream: " + e.getMessage());
            }
        }
        return null;
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
            Assert.fail("Unexpected error performing XML comparison: " + e.getMessage());
        }
    }

    /**
     * Compare an XML String (e.g. a result) against another String.
     * @param xml The XML (as a String) to be compared against the XML in the specified
     * classpath resource.
     * @param string The String against which the XML is to be
     * compared.
     */
    public void compareXMLToString(String xml, String string) {
        XMLUnit.setIgnoreWhitespace(true);
        try {
            XMLAssert.assertXMLEqual(string, xml);
        } catch (Exception e) {
            Assert.fail("Unexpected error performing XML comparison.");
        }
    }

    private void initializeMixIns() {
        for (TestMixIn mixIn : _testMixInInstances) {
            mixIn.initialize();
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

    private void cleanupMixIns() {
        // Destroy MixIns in reverse order...
        for (int i = _testMixInInstances.size() - 1; i >= 0; i--) {
            _testMixInInstances.get(i).uninitialize();
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

    private SwitchYardModel createSwitchYardModel(InputStream configModel, List<Scanner<V1SwitchYardModel>> scanners) {
        Assert.assertNotNull("Test 'configModel' is null.", configModel);

        try {
            SwitchYardModel model = loadSwitchYardModel(configModel);
            ClassLoader classLoader = getClass().getClassLoader();

            if (scanners != null && !scanners.isEmpty() && classLoader instanceof URLClassLoader) {
                MergeScanner<V1SwitchYardModel> merge_scanner = new MergeScanner<V1SwitchYardModel>(V1SwitchYardModel.class, true, scanners);
                List<URL> scanURLs = getScanURLs((URLClassLoader)classLoader);

                ScannerInput<V1SwitchYardModel> scanner_input = new ScannerInput<V1SwitchYardModel>().setName("").setURLs(scanURLs);
                V1SwitchYardModel scannedModel = merge_scanner.scan(scanner_input).getModel();

                return Models.merge(scannedModel, model, false);
            } else {
                return model;
            }
        } catch (java.io.IOException ioEx) {
            throw new RuntimeException("Failed to read switchyard config.", ioEx);
        }
    }

    private void assertDeployed() {
        if (_deployment == null) {
            Assert.fail("TestCase deployment not yet deployed.  You may need to make an explicit call to the deploy() method.");
        }
    }

    private List<Scanner<V1SwitchYardModel>> createScanners(SwitchYardTestCaseConfig testCaseConfig) {
        List<Scanner<V1SwitchYardModel>> scanners = new ArrayList<Scanner<V1SwitchYardModel>>();

        if (testCaseConfig != null) {
            Class<? extends Scanner>[] scannerClasses = testCaseConfig.scanners();

            if (scannerClasses == null) {
                // No Scanners
                _logger.debug("No Scanners for test.");
            } else if (scannerClasses.length == 1 && scannerClasses[0] == NullScanners.class) {
                // No Scanners
                _logger.debug("No Scanners for test.");
            } else {
                for (Class<? extends Scanner> scannerClass : scannerClasses) {
                    try {
                        scanners.add(scannerClass.newInstance());
                    } catch (Exception e) {
                        Assert.fail("Exception creating instance of Scanner class '" + scannerClass.getName() + "': " + e.getMessage());
                    }
                }
            }
        }

        return scanners;
    }

    private List<URL> getScanURLs(URLClassLoader classLoader) {
        URL[] classPathURLs = classLoader.getURLs();
        List<URL> scanURLs = new ArrayList<URL>();

        // Only scan directories.  Above all, we want to make sure we don't
        // start scanning JDK jars etc...
        for (URL classpathURL : classPathURLs) {
            try {
                File file = ClasspathScanner.toClassPathFile(classpathURL);
                if (file.isDirectory()) {
                    scanURLs.add(classpathURL);
                }
            } catch (IOException e) {
                Assert.fail("Failed to convert classpath URL '" + classpathURL + "' to a File instance.");
            }
        }

        return scanURLs;
    }

    @Scannable(false)
    private static final class TransformerWrapper extends BaseTransformer {

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
        public QName getFrom() {
            return _transformModel.getFrom();
        }

        @Override
        public QName getTo() {
            return _transformModel.getTo();
        }
    }

    /**
     * Test Runner
     */
    protected static class TestRunner extends BlockJUnit4ClassRunner {

        private SwitchYardTestCase _testCase;

        /**
         * Public constructor.
         * @param klass The test Class.
         * @throws InitializationError Initialization error.
         */
        public TestRunner(Class<?> klass) throws InitializationError {
            super(klass);
        }

        private void setTestCase(SwitchYardTestCase testCase) {
            this._testCase = testCase;
        }

        @Override
        public void run(RunNotifier notifier) {
            try {
                TEST_RUNNER.set(this);
                super.run(notifier);
            } finally {
                try {
                    _testCase.cleanup();
                } finally {
                    TEST_RUNNER.remove();
                }
            }
        }
    }

    /**
     * Hidden marker type to provide a valid NULL
     * Scanners configuration for {@link SwitchYardTestCaseConfig}.
     */
    protected static final class NullScanners implements Scanner {
        @Override
        public ScannerOutput scan(ScannerInput scannerInput) throws IOException {
            return null;
        }
    }

    /**
     * Hidden marker type to provide a valid NULL
     * TestMixIns configuration for {@link SwitchYardTestCaseConfig}.
     */
    protected static final class NullMixIns extends AbstractTestMixIn {
    }
}
