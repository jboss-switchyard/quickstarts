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
package org.switchyard.config.model.composite;

import java.io.StringReader;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.Models;
import org.switchyard.config.model.composite.test.bogus.BogusImplementationModel;
import org.switchyard.config.model.composite.test.soap.PortModel;
import org.switchyard.config.model.composite.test.soap.SOAPBindingModel;
import org.switchyard.config.model.composite.test.soap.WSDLModel;
import org.switchyard.config.model.composite.v1.V1ComponentImplementationModel;
import org.switchyard.config.test.xmlunit.SchemaLocationDifferenceListener;

/**
 * CompositeModelTests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class CompositeModelTests {

    private static final String INCOMPLETE_XML = "/org/switchyard/config/model/composite/CompositeModelTests-Incomplete.xml";
    private static final String FRAGMENT_XML = "/org/switchyard/config/model/composite/CompositeModelTests-Fragment.xml";
    private static final String COMPLETE_XML = "/org/switchyard/config/model/composite/CompositeModelTests-Complete.xml";
    private static final String EXTENDED_XML = "/org/switchyard/config/model/composite/CompositeModelTests-Extended.xml";

    private static ModelPuller<CompositeModel> _puller;

    @Before
    public void before() throws Exception {
        _puller = new ModelPuller<CompositeModel>();
    }

    @Test
    public void testCreateEmptyModel() throws Exception {
        String namespace = CompositeModel.DEFAULT_NAMESPACE;
        String name = CompositeModel.COMPOSITE;
        Model model = new ModelPuller<Model>().pull(XMLHelper.createQName(namespace, name));
        Assert.assertTrue(model instanceof CompositeModel);
        Assert.assertEquals(name, model.getModelConfiguration().getName());
        Assert.assertEquals(new QName(namespace, name), model.getModelConfiguration().getQName());
    }

    @Test
    public void testMerge() throws Exception {
        CompositeModel incomplete_composite = _puller.pull(INCOMPLETE_XML, getClass());
        CompositeModel fragment_composite = _puller.pull(FRAGMENT_XML, getClass());
        CompositeModel merged_composite = Models.merge(fragment_composite, incomplete_composite);
        CompositeModel complete_composite = _puller.pull(COMPLETE_XML, getClass());
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(complete_composite.toString(), merged_composite.toString());
        diff.overrideDifferenceListener(new SchemaLocationDifferenceListener());
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testReadCustomViaConfig() throws Exception {
        CompositeModel composite = _puller.pull(COMPLETE_XML, getClass());
        CompositeServiceModel compositeService = composite.getServices().get(0);
        BindingModel binding = compositeService.getBindings().get(0);
        Assert.assertEquals("soap", binding.getType());
        Configuration port_config = binding.getModelConfiguration().getFirstChild(PortModel.PORT);
        Assert.assertEquals("MyWebService/SOAPPort", port_config.getValue());
        Assert.assertEquals("true", port_config.getAttribute(PortModel.SECURE));
        Configuration wsdl_config = binding.getModelConfiguration().getFirstChild(WSDLModel.WSDL);
        Assert.assertEquals("service.wsdl", wsdl_config.getValue());
        Assert.assertEquals("foobar", wsdl_config.getAttribute(WSDLModel.DESCRIPTION));
    }

    @Test
    public void testReadCustomViaModel() throws Exception {
        CompositeModel composite = _puller.pull(COMPLETE_XML, getClass());
        CompositeServiceModel compositeService = composite.getServices().get(0);
        BindingModel binding = (BindingModel)compositeService.getBindings().get(0);
        Assert.assertTrue(binding instanceof SOAPBindingModel);
        SOAPBindingModel soap_binding = (SOAPBindingModel)compositeService.getBindings().get(0);
        Assert.assertEquals("soap", binding.getType());
        PortModel port = soap_binding.getPort();
        Assert.assertEquals("MyWebService/SOAPPort", port.getPort());
        Assert.assertEquals(true, port.isSecure());
        WSDLModel wsdl = soap_binding.getWSDL();
        Assert.assertEquals("service.wsdl", wsdl.getLocation());
        Assert.assertEquals("foobar", wsdl.getDescription());
    }

    @Test
    public void testReadComplete() throws Exception {
        CompositeModel composite = _puller.pull(COMPLETE_XML, getClass());
        Assert.assertEquals(CompositeModel.DEFAULT_NAMESPACE, composite.getModelConfiguration().getQName().getNamespaceURI());
        Assert.assertEquals("m1app", composite.getName());
        CompositeServiceModel compositeService = composite.getServices().get(0);
        Assert.assertEquals("M1AppService", compositeService.getName());
        Assert.assertEquals("SimpleService", compositeService.getPromote());
        SOAPBindingModel binding1 = (SOAPBindingModel)compositeService.getBindings().get(0);
        Assert.assertEquals("soap", binding1.getType());
        PortModel port = binding1.getPort();
        Assert.assertEquals("MyWebService/SOAPPort", port.getPort());
        Assert.assertEquals(true, port.isSecure());
        WSDLModel wsdl1 = binding1.getWSDL();
        Assert.assertEquals("service.wsdl", wsdl1.getLocation());
        Assert.assertEquals("foobar", wsdl1.getDescription());
        CompositeReferenceModel compositeReference = composite.getReferences().get(0);
        Assert.assertEquals("SomeOtherService", compositeReference.getName());
        Assert.assertEquals("SimpleService/AnotherService", compositeReference.getPromote());
        SOAPBindingModel binding2 = (SOAPBindingModel)compositeReference.getBindings().get(0);
        Assert.assertEquals("soap", binding2.getType());
        WSDLModel wsdl = binding2.getWSDL();
        Assert.assertEquals("http://exmample.org:8080/services/SomeOtherService?wsdl", wsdl.getLocation());
        ComponentModel component1 = composite.getComponents().get(0);
        Assert.assertEquals(component1, compositeService.getComponent());
        Assert.assertEquals("SimpleService", component1.getName());
        ComponentImplementationModel implementation1 = component1.getImplementation();
        Assert.assertEquals("bean", implementation1.getType());
        ComponentServiceModel componentService1 = component1.getServices().get(0);
        Assert.assertEquals("SimpleService", componentService1.getName());
        ComponentServiceInterfaceModel interface1 = componentService1.getInterface();
        Assert.assertEquals("java", interface1.getType());
        Assert.assertEquals("org.switchyard.example.m1app.SimpleService", interface1.getInterface());
        ComponentReferenceModel reference = component1.getReferences().get(0);
        Assert.assertEquals("anotherService", reference.getName());
        ComponentReferenceInterfaceModel interface2 = reference.getInterface();
        Assert.assertEquals("java", interface2.getType());
        Assert.assertEquals("org.switchyard.example.m1app.AnotherService", interface2.getInterface());
        ComponentModel component2 = composite.getComponents().get(1);
        Assert.assertEquals("AnotherService", component2.getName());
        ComponentImplementationModel implementation2 = component2.getImplementation();
        Assert.assertEquals("bean", implementation2.getType());
        ComponentServiceModel componentService2 = component2.getServices().get(0);
        Assert.assertEquals("AnotherService", componentService2.getName());
        ComponentServiceInterfaceModel interface3 = componentService2.getInterface();
        Assert.assertEquals("java", interface3.getType());
        Assert.assertEquals("org.switchyard.example.m1app.AnotherService", interface3.getInterface());
    }
    
    @Test
    public void testBindingModel() throws Exception {
        CompositeModel composite = _puller.pull(COMPLETE_XML, getClass());
        // Test service binding
        BindingModel serviceBinding = composite.getServices().get(0).getBindings().get(0);
        Assert.assertTrue(serviceBinding.isServiceBinding());
        Assert.assertFalse(serviceBinding.isReferenceBinding());
        Assert.assertNotNull(serviceBinding.getService());
        Assert.assertNull(serviceBinding.getReference());
        // Test reference binding
        BindingModel referenceBinding = composite.getReferences().get(0).getBindings().get(0);
        Assert.assertTrue(referenceBinding.isReferenceBinding());
        Assert.assertFalse(referenceBinding.isServiceBinding());
        Assert.assertNotNull(referenceBinding.getReference());
        Assert.assertNull(referenceBinding.getService());
    }

    @Test
    public void testWriteComplete() throws Exception {
        String old_xml = new StringPuller().pull(COMPLETE_XML, getClass());
        CompositeModel composite = _puller.pull(new StringReader(old_xml));
        String new_xml = composite.toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(old_xml, new_xml);
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testReadExtended() throws Exception {
        CompositeModel cm = _puller.pull(EXTENDED_XML, getClass());
        BogusImplementationModel bim = (BogusImplementationModel)cm.getComponents().get(0).getImplementation();
        Assert.assertEquals("bar", bim.getFoo());
    }
    
    @Test
    public void testReadExtendedWithService() throws Exception {
        CompositeModel cm = _puller.pull(EXTENDED_XML, getClass());
        ComponentModel component = cm.getComponents().get(0);
        Assert.assertEquals(1, component.getServices().size());
    }

    @Test
    public void testParenthood() throws Exception {
        CompositeModel composite_1 = _puller.pull(COMPLETE_XML, getClass());
        CompositeServiceModel service_1 = composite_1.getServices().get(0);
        SOAPBindingModel binding = (SOAPBindingModel)service_1.getBindings().get(0);
        CompositeServiceModel service_2 = binding.getService();
        CompositeModel composite_2 = service_2.getComposite();
        Assert.assertEquals(service_1, service_2);
        Assert.assertEquals(composite_1, composite_2);
    }

    @Test
    public void testValidation() throws Exception {
        CompositeModel composite = _puller.pull(COMPLETE_XML, getClass());
        composite.assertModelValid();
    }

    @Test
    public void testVerifyQNameUponCreation() throws Exception {
        final String type = "customtype";
        final V1ComponentImplementationModel model = new V1ComponentImplementationModel(type);
        Assert.assertEquals(type, model.getType());
    }

}
