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
package org.switchyard.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
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
import org.junit.Assert;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.common.type.Classes;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.config.model.MergeScanner;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.Models;
import org.switchyard.config.model.Scannable;
import org.switchyard.config.model.Scanner;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.ScannerOutput;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.deploy.ServiceDomainManager;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.deploy.internal.Deployment;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.test.mixins.AbstractTestMixIn;
import org.switchyard.transform.BaseTransformer;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerUtil;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SwitchYardTestKit {

    /**
     * Logger.
     */
    private static Logger _logger = Logger.getLogger(SwitchYardTestKit.class);
    /**
     * Constant for the {@link org.switchyard.test.SwitchYardTestCaseConfig#config()} default.
     */
    protected static final String NULL_CONFIG = "$$NULL_SW_CONFIG$$";

    /**
     * Class test instance.
     */
    private Object _testInstance;
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
     * @param testInstance The test instance.
     * @exception Exception Error initializing test kit.
     */
    public SwitchYardTestKit(Object testInstance) throws Exception {
        this._testInstance = testInstance;

        SwitchYardTestCaseConfig testCaseConfig = testInstance.getClass().getAnnotation(SwitchYardTestCaseConfig.class);

        if (testCaseConfig != null) {
            String config = testCaseConfig.config();
            if (config != null && !config.equals(NULL_CONFIG)) {
                InputStream is = getResourceAsStream(config);

                if (is == null) {
                    // Try the file system...
                    File file = new File(config);
                    if (file.isFile()) {
                        is = new FileInputStream(file);
                    }
                }

                if (is == null) {
                    Assert.fail("Failed to locate test configuration '" + config + "' on the classpath or project sub-directory. See the @" + SwitchYardTestCaseConfig.class.getSimpleName() + " annotation on test class '" + _testInstance.getClass().getName() + "'.");
                }

                try {
                    _configModel = createSwitchYardModel(is, createScanners(testCaseConfig));
                } finally {
                    try {
                        is.close();
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
        intialize();
    }

    /**
     * Initialize.
     */
    private void intialize() throws Exception {
        initializeMixIns();
        deploy();
    }

    /**
     * Cleanup.
     */
    public void cleanup() {
        undeploy();
        cleanupMixIns();
        MockInitialContextFactory.clear();
    }

    /**
     * Get the test class instance.
     * @return The test class instance.
     */
    public Object getTestInstance() {
        return _testInstance;
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
    private final void deploy() throws Exception {
        _deployment = createDeployment();
        _deployment.init(ServiceDomainManager.createDomain(
                ServiceDomainManager.ROOT_DOMAIN, _deployment.getConfig()));
        mixInBefore();
        _deployment.start();
    }

    /**
     * Undeploy the deployment.
     */
    private final void undeploy() {
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
    private AbstractDeployment createDeployment() throws Exception {
        if (_configModel != null) {
            return new Deployment(_configModel);
        } else {
            return new SimpleTestDeployment();
        }
    }

    /**
     * Get the deployment instance associated with the test case.
     * @return The deployment instance associated with the test case.
     */
    public AbstractDeployment getDeployment() {
        return _deployment;
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
     * Creates a QName given this test kit's config model's targetNamespace + the specified localPart.
     * @param localPart the specified localPart
     * @return the QName
     */
    public QName createQName(String localPart) {
        final String tns;
        if (_configModel != null) {
            CompositeModel composite = _configModel.getComposite();
            tns = composite != null ? composite.getTargetNamespace() : _configModel.getTargetNamespace();
        } else {
            tns = null;
        }
        return XMLHelper.createQName(tns, localPart);
    }

    /**
     * Register an IN_OUT Service.
     * <p/>
     * Registers a {@link MockHandler} as the service handler.
     *
     * @param serviceName The Service name.
     * @return The {@link MockHandler} service handler.
     */
    public MockHandler registerInOutService(String serviceName) {
        MockHandler handler = new MockHandler();
        getServiceDomain().registerService(createQName(serviceName), handler, new InOutService());
        return handler;
    }

    /**
     * Register an IN_OUT Service.
     *
     * @param serviceName The Service name.
     * @param serviceHandler The service handler.
     */
    public void registerInOutService(String serviceName, ExchangeHandler serviceHandler) {
        getServiceDomain().registerService(createQName(serviceName), serviceHandler, new InOutService());
    }

    /**
     * Register an IN_OUT Service.
     *
     * @param serviceName The Service name.
     * @param serviceHandler The service handler.
     * @param metadata Service interface.
     */
    public void registerInOutService(String serviceName, ExchangeHandler serviceHandler, ServiceInterface metadata) {
        getServiceDomain().registerService(createQName(serviceName), serviceHandler, metadata);
    }

    /**
     * Register an IN_ONLY Service.
     * <p/>
     * Registers a {@link MockHandler} as the fault service handler.
     *
     * @param serviceName The Service name.
     * @return The {@link MockHandler} service fault handler.
     */
    public MockHandler registerInOnlyService(String serviceName) {
        MockHandler handler = new MockHandler();
        getServiceDomain().registerService(createQName(serviceName), handler, new InOnlyService());
        return handler;
    }

    /**
     * Register an IN_ONLY Service.
     *
     * @param serviceName The Service name.
     * @param serviceHandler The service handler.
     */
    public void registerInOnlyService(String serviceName, ExchangeHandler serviceHandler) {
        getServiceDomain().registerService(createQName(serviceName), serviceHandler, new InOnlyService());
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
    public Invoker newInvoker(QName serviceName) {
        return new Invoker(getServiceDomain(), serviceName);
    }

    /**
     * Create a new {@link Invoker} instance for invoking a Service in the test ServiceDomain.
     * @param serviceName The target Service name.  Can be a serialized {@link QName}.  Can also
     * include the operation name e.g. "OrderManagementService.createOrder".
     * @return The invoker instance.
     */
    public Invoker newInvoker(String serviceName) {
        return newInvoker(createQName(serviceName));
    }

    /**
     * Create a new {@link Transformer} instance from the specified {@link org.switchyard.config.model.transform.TransformModel}.
     * @param transformModel The TransformModel.
     * @return The Transformer instance.
     */
    public Transformer newTransformer(TransformModel transformModel) {
        return TransformerUtil.newTransformer(transformModel);
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

        Transformer<?,?> transformer = TransformerUtil.newTransformer(transformModel);
        if (transformer.getFrom() == null) {
            transformer = new TransformerWrapper(transformer, transformModel);
        }
        _deployment.getDomain().getTransformerRegistry().removeTransformer(transformer);

        return transformer;
    }

    /**
     * Get the {@link TestMixIn} instances associated with this test instance.
     * @return The {@link TestMixIn} instances associated with this test instance.
     */
    public List<TestMixIn> getMixIns() {
        return _testMixInInstances;
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
            Assert.fail("Required TestMixIn '" + type.getName() + "' is not specified on TestCase '" + _testInstance.getClass().getName() + "'.");
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
     * @see org.switchyard.common.type.Classes#getResourceAsStream(String,Class)
     */
    public InputStream getResourceAsStream(String name) {
        try {
            return Classes.getResourceAsStream(name, _testInstance.getClass());
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
            Assert.fail("Resource '" + path + "' not found on classpath relative to test class '" + _testInstance.getClass().getName() + "'.  May need to fix the relative path, or make the path absolute.");
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
        return loadSwitchYardModel(configModel, true);
    }

    /**
     * Load the SwitchYard configuration model specified by the configModel stream.
     * @param configModel The config model stream.
     * @param validate Validate the model?
     * @return The SwitchYard config model.
     */
    public SwitchYardModel loadSwitchYardModel(InputStream configModel, boolean validate) {
        return loadConfigModel(configModel, SwitchYardModel.class, validate);
    }

    /**
     * Load the configuration model specified by the configModel stream.
     * @param <M> Model type.
     * @param configModel The config model stream.
     * @param modelType Model type.
     * @return The config model.
     */
    public <M extends Model> M loadConfigModel(InputStream configModel, Class<M> modelType) {
        return loadConfigModel(configModel, modelType, true);
    }

    /**
     * Load the SwitchYard configuration model specified by the configModel stream.
     * @param <M> Model type.
     * @param configModel The config model stream.
     * @param modelType Model type.
     * @param validate Validate the model?
     * @return The SwitchYard config model.
     */
    public <M extends Model> M loadConfigModel(InputStream configModel, Class<M> modelType, boolean validate) {
        if (configModel == null) {
            throw new IllegalArgumentException("null 'configModel' arg.");
        }
        try {
            M pulledModel = new ModelPuller<M>().pull(configModel);
            if (validate) {
                pulledModel.assertModelValid();
            }
            return pulledModel;
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
    public static void compareXMLToString(String xml, String string) {
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
                TestMixIn testMixIn = newMixInInstance(mixInType, _testInstance);
                testMixIn.setTestKit(this);
                _testMixInInstances.add(testMixIn);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail("Failed to create instance of TestMixIn type " + mixInType.getName() + ".  Make sure it defines a public default constructor.");
            }
        }
    }

    protected static <T extends TestMixIn> T newMixInInstance(Class<T> mixInType, Object testInstance) {
        Class<? extends Object> testClass = testInstance.getClass();
        Method[] methods = testClass.getDeclaredMethods();

        // Check for a factory method for the MixIn type...
        for (Method method : methods) {
            int modifiers = method.getModifiers();

            if (Modifier.isPublic(modifiers)) {
                if (method.getReturnType() == mixInType && method.getParameterTypes().length == 0) {
                    try {
                        if (Modifier.isStatic(modifiers)) {
                            return mixInType.cast(method.invoke(null));
                        } else {
                            return mixInType.cast(method.invoke(testInstance));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail("Failed to create instance of TestMixIn type " + mixInType.getName() + ".  Error invoking the MixIn factory method '" + method.getName() + "': " + e.getMessage());
                        return null;
                    }
                }
            }
        }

        try {
            return mixInType.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed to create instance of TestMixIn type " + mixInType.getName() + " via public default constructor: " + e.getMessage());
            return null;
        }
    }

    private SwitchYardModel createSwitchYardModel(InputStream configModel, List<Scanner<V1SwitchYardModel>> scanners) {
        Assert.assertNotNull("Test 'configModel' is null.", configModel);

        final SwitchYardModel returnModel;
        try {
            SwitchYardModel model = loadSwitchYardModel(configModel, false);
            ClassLoader classLoader = _testInstance.getClass().getClassLoader();

            if (scanners != null && !scanners.isEmpty() && classLoader instanceof URLClassLoader) {
                MergeScanner<V1SwitchYardModel> merge_scanner = new MergeScanner<V1SwitchYardModel>(V1SwitchYardModel.class, true, scanners);
                List<URL> scanURLs = getScanURLs((URLClassLoader)classLoader);

                ScannerInput<V1SwitchYardModel> scanner_input = new ScannerInput<V1SwitchYardModel>().setName(model.getName()).setURLs(scanURLs);
                V1SwitchYardModel scannedModel = merge_scanner.scan(scanner_input).getModel();

                returnModel = Models.merge(scannedModel, model, false);
            } else {
                returnModel = model;
            }
            returnModel.assertModelValid();
            return returnModel;
        } catch (java.io.IOException ioEx) {
            throw new SwitchYardException("Failed to read switchyard config.", ioEx);
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

        // Temp hack to work around SWITCHYARD-343 (https://issues.jboss.org/browse/SWITCHYARD-343)...
        if (scanURLs.isEmpty()) {
            try {
                scanURLs.add(new File("target/test-classes").toURI().toURL());
                scanURLs.add(new File("target/classes").toURI().toURL());
            } catch (MalformedURLException e) {
                Assert.fail("Unexpected exception adding target test classes folders to test scan URLs: " + e.getMessage());
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
