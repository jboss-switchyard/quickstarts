/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.tools.forge.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.forge.test.AbstractShellTest;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.v1.V1BindingModel;
import org.switchyard.config.model.composite.v1.V1ComponentImplementationModel;
import org.switchyard.config.model.composite.v1.V1ComponentModel;
import org.switchyard.config.model.composite.v1.V1ComponentReferenceModel;
import org.switchyard.config.model.composite.v1.V1ComponentServiceModel;
import org.switchyard.config.model.composite.v1.V1CompositeServiceModel;
import org.switchyard.config.model.selector.JavaOperationSelectorModel;
import org.switchyard.config.model.selector.RegexOperationSelectorModel;
import org.switchyard.config.model.selector.StaticOperationSelectorModel;
import org.switchyard.config.model.selector.XPathOperationSelectorModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.policy.Policy;
import org.switchyard.policy.PolicyFactory;
import org.switchyard.tools.forge.GenericTestForge;
import org.switchyard.transform.config.model.JAXBTransformModel;
import org.switchyard.transform.config.model.JSONTransformModel;
import org.switchyard.transform.config.model.JavaTransformModel;
import org.switchyard.transform.config.model.SmooksTransformModel;
import org.switchyard.transform.config.model.XsltTransformModel;
import org.switchyard.validate.config.model.JavaValidateModel;
import org.switchyard.validate.config.model.XmlSchemaType;
import org.switchyard.validate.config.model.XmlValidateModel;

/**
 * Test for {@link SwitchyardFacet}.
 *
 * @author Mario Antollini
 */
public class ForgeSwitchyardTest extends GenericTestForge {

    private static final String SERVICE_TEST = "ForgeServiceTest";

    private static final String ENABLE_TRACING_MSG = "Message tracing has been enabled";

    private static final String DISABLE_TRACING_MSG = "Message tracing has been disabled";

    private static final String SHOW_CONFIG_MSG = "[Public]" 
        + System.getProperty("line.separator") 
        + System.getProperty("line.separator") 
        + "[Private]";

    private static String VERSION_MSG;

    /**
     * Constructor.
     */
    public ForgeSwitchyardTest() {
        VERSION_MSG = "SwitchYard version " + getSwitchyardVersion();
    }
    
    /**
     * The deployment method is where you references to classes, packages, and
     * configuration files are added via  Arquillian.
     * @return the Traditional JAR (Java Archive) structure
     */
    @Deployment
    public static JavaArchive getDeployment() {
        // The deployment method is where references to classes, packages, 
        // and configuration files are added via Arquillian.
        JavaArchive archive = AbstractShellTest.getDeployment();
        archive.addPackages(true, SwitchYardFacet.class.getPackage());
        return archive;
    }
    
    /**
     * The single test containing some test cases.
     */
    @Test
    public void runTest() throws Exception {
        try {
            createTestService();
            testTraceMessages();
            testGetVersion();
            testShowConfig();
            testAddTransformer();
            testAddValidator();
            testAddPolicy();
            testAddOperationSelector();
        
        } catch (Exception e) {
            System.out.println(getOutput());
            throw e;
        }
    }
    
    /**
     * Tests the creation of a test service. 
     * @throws Exception if a problem occurs during execution
     */
    public void createTestService() throws Exception {
        resetOutputStream();
        getShell().execute("switchyard create-service-test --serviceName " + SERVICE_TEST);
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains("Created unit test " + SERVICE_TEST + "Test.java"));
    }
    
    /**
     * Tests the trace-messages command. 
     * @throws Exception if a problem occurs during execution
     */
    public void testTraceMessages() throws Exception {
        resetOutputStream();
        queueInputLines("Y");
        getShell().execute("switchyard trace-messages");
        Assert.assertTrue(getOutput().contains(ENABLE_TRACING_MSG));
        queueInputLines("n");
        getShell().execute("switchyard trace-messages");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(DISABLE_TRACING_MSG));
    }

    /**
     * Tests the get-version command.
     * @throws Exception if a problem occurs during execution
     */
    public void testGetVersion() throws Exception {
        resetOutputStream();
        getShell().execute("switchyard get-version");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(VERSION_MSG));
    }

    /**
     * Tests the show-config command.
     * @throws Exception if a problem occurs during execution
     */
    public void testShowConfig() throws Exception {
        resetOutputStream();
        getShell().execute("switchyard show-config");
        System.out.println(getOutput());
        Assert.assertTrue(getOutput().contains(SHOW_CONFIG_MSG));
    }
    
    public void testAddTransformer() throws Exception {
        resetOutputStream();
        SwitchYardFacet switchYard = getProject().getFacet(SwitchYardFacet.class);
        String from = "\"{urn:switchyard:forge-test:0.1.0}order\"";
        String to = "\"{urn:switchyard:forge-test:0.1.0}orderAck\"";

        // Java
        queueInputLines("1", this.getClass().getName());
        getShell().execute("switchyard add-transformer --from " + from + " --to " + to);
        // Smooks
        queueInputLines("2", "/smooks/OrderXML.xml", "1");
        getShell().execute("switchyard add-transformer --from " + from + " --to " + to);
        // XSLT
        queueInputLines("3", "xslt/order.xslt", "Y");
        getShell().execute("switchyard add-transformer --from " + from + " --to " + to);
        // JSON
        queueInputLines("4");
        getShell().execute("switchyard add-transformer --from " + from + " --to " + to);
        // JAXB
        queueInputLines("5");
        getShell().execute("switchyard add-transformer --from " + from + " --to " + to);
        
        // Verify generated transformers
        List<String> expected = new ArrayList<String>(Arrays.asList(new String[]{"Java", "Smooks", "XSLT", "JSON", "JAXB"}));
        for (TransformModel transform : switchYard.getSwitchYardConfig().getTransforms().getTransforms()) {
            if (transform instanceof JavaTransformModel) {
                JavaTransformModel java = JavaTransformModel.class.cast(transform);
                Assert.assertEquals(this.getClass().getName(), java.getClazz());
                expected.remove("Java");
            } else if (transform instanceof SmooksTransformModel) {
                SmooksTransformModel smooks = SmooksTransformModel.class.cast(transform);
                Assert.assertEquals("/smooks/OrderXML.xml", smooks.getConfig());
                Assert.assertEquals("SMOOKS", smooks.getTransformType());
                expected.remove("Smooks");
            } else if (transform instanceof XsltTransformModel) {
                XsltTransformModel xslt = XsltTransformModel.class.cast(transform);
                Assert.assertEquals("xslt/order.xslt", xslt.getXsltFile());
                Assert.assertEquals(true, xslt.failOnWarning());
                expected.remove("XSLT");
            } else if (transform instanceof JSONTransformModel) {
                expected.remove("JSON");
            } else if (transform instanceof JAXBTransformModel) {
                expected.remove("JAXB");
            } else {
                Assert.fail("Unknown transformer detected " + transform);
            }
        }
        System.out.println(getOutput());
        Assert.assertEquals(0,  expected.size());
    }
    
    public void testAddValidator() throws Exception {
        resetOutputStream();
        SwitchYardFacet switchYard = getProject().getFacet(SwitchYardFacet.class);
        String type = "\"{urn:switchyard:forge-test:0.1.0}order\"";

        // Java
        queueInputLines("1", this.getClass().getName());
        getShell().execute("switchyard add-validator --type" + type);
        // XML
        queueInputLines("2", "2", "/xsd/catalog.xml", "/xsd/orders.xsd", "Y", "Y");
        getShell().execute("switchyard add-validator --type" + type);

        // Verify generated validators
        List<String> expected = new ArrayList<String>(Arrays.asList(new String[]{"Java", "XML"}));
        for (ValidateModel validate : switchYard.getSwitchYardConfig().getValidates().getValidates()) {
            if (validate instanceof JavaValidateModel) {
                JavaValidateModel java = JavaValidateModel.class.cast(validate);
                Assert.assertEquals(this.getClass().getName(), java.getClazz());
                expected.remove("Java");
            } else if (validate instanceof XmlValidateModel) {
                XmlValidateModel xml = XmlValidateModel.class.cast(validate);
                Assert.assertEquals(XmlSchemaType.XML_SCHEMA, xml.getSchemaType());
                Assert.assertEquals("/xsd/orders.xsd", xml.getSchemaFiles().getEntries().get(0).getFile());
                Assert.assertEquals("/xsd/catalog.xml", xml.getSchemaCatalogs().getEntries().get(0).getFile());
                Assert.assertEquals(true, xml.failOnWarning());
                Assert.assertEquals(true, xml.namespaceAware());
                expected.remove("XML");
            }
        }
        System.out.println(getOutput());
        Assert.assertEquals(0, expected.size());
    }
    
    public void testAddPolicy() throws Exception {
        resetOutputStream();
        SwitchYardFacet switchYard = getProject().getFacet(SwitchYardFacet.class);
        ComponentModel component = new V1ComponentModel();
        component.setName("TestComponent");
        ComponentServiceModel service = new V1ComponentServiceModel();
        service.setName("TestService");
        component.addService(service);
        ComponentReferenceModel reference = new V1ComponentReferenceModel();
        reference.setName("TestReference");
        component.addReference(reference);
        ComponentImplementationModel implementation = new V1ComponentImplementationModel("bean");
        component.setImplementation(implementation);
        switchYard.getSwitchYardConfig().getComposite().addComponent(component);
        switchYard.saveConfig();
        
        queueInputLines("1", "1");
        getShell().execute("switchyard add-required-policy --componentName " + component.getName());
        queueInputLines("2", "TestReference", "2");
        getShell().execute("switchyard add-required-policy --componentName " + component.getName());
        queueInputLines("3", "1");
        getShell().execute("switchyard add-required-policy --componentName " + component.getName());
        
        // Verify generated policies
        System.out.println(getOutput());
        component = switchYard.getSwitchYardConfig().getComposite().getComponents().get(0);
        Assert.assertEquals(PolicyFactory.getAvailableInteractionPolicies().toArray(new Policy[0])[0].getName(), component.getServices().get(0).getPolicyRequirements().iterator().next());
        Assert.assertEquals(PolicyFactory.getAvailableImplementationPolicies().toArray(new Policy[0])[0].getName(), component.getImplementation().getPolicyRequirements().iterator().next());
        Assert.assertEquals(PolicyFactory.getAvailableInteractionPolicies().toArray(new Policy[0])[1].getName(), component.getReferences().get(0).getPolicyRequirements().iterator().next());
    }
    
    public void testAddOperationSelector() throws Exception {
        SwitchYardFacet switchYard = getProject().getFacet(SwitchYardFacet.class);
        String serviceName = "ForgeTestService";
        CompositeServiceModel service = new V1CompositeServiceModel();
        service.setName(serviceName);
        service.addBinding(new V1BindingModel("bean"));
        switchYard.getSwitchYardConfig().getComposite().addService(service);
        switchYard.saveConfig();
        
        String operation = "myOperation";
        queueInputLines("1", "1", operation);
        getShell().execute("switchyard add-operation-selector --serviceName " + serviceName);
        BindingModel model = service.getBindings().get(0);
        StaticOperationSelectorModel staticSelector = StaticOperationSelectorModel.class.cast(model.getOperationSelector());
        Assert.assertEquals(operation, staticSelector.getOperationName());
        
        String xpath = "//person/language";
        queueInputLines("1", "2", xpath);
        getShell().execute("switchyard add-operation-selector --serviceName " + serviceName);
        XPathOperationSelectorModel xpathSelector = XPathOperationSelectorModel.class.cast(model.getOperationSelector());
        Assert.assertEquals(xpath, xpathSelector.getExpression());
        
        String regex = "*";
        queueInputLines("1", "3", regex);
        getShell().execute("switchyard add-operation-selector --serviceName " + serviceName);
        RegexOperationSelectorModel regexSelector = RegexOperationSelectorModel.class.cast(model.getOperationSelector());
        Assert.assertEquals(regex, regexSelector.getExpression());

        String clazz = this.getClass().getName();
        queueInputLines("1", "4", clazz);
        getShell().execute("switchyard add-operation-selector --serviceName " + serviceName);
        JavaOperationSelectorModel javaSelector = JavaOperationSelectorModel.class.cast(model.getOperationSelector());
        Assert.assertEquals(clazz, javaSelector.getClazz());
    }
    
}
