/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.config.model.composite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.common.property.PropertyResolver;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.Models;
import org.switchyard.config.model.composite.test.bogus.BogusImplementationModel;
import org.switchyard.config.model.composite.test.extension.ExtensionBindingModel;
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
    private static final String EXTENSION_XML = "/org/switchyard/config/model/composite/CompositeModelTests-Extension.xml";
    private static final String SCA_BINDING_XML = "/org/switchyard/config/model/composite/CompositeModelTests-SCABinding.xml";
    private static final String PROMOTE_XML = "/org/switchyard/config/model/composite/CompositeModelTests-Promote.xml";

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
        assertTrue(model instanceof CompositeModel);
        assertEquals(name, model.getModelConfiguration().getName());
        assertEquals(new QName(namespace, name), model.getModelConfiguration().getQName());
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
        assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testReadCustomViaConfig() throws Exception {
        CompositeModel composite = _puller.pull(COMPLETE_XML, getClass());
        CompositeServiceModel compositeService = composite.getServices().get(0);
        BindingModel binding = compositeService.getBindings().get(0);
        assertEquals("soap", binding.getType());
        Configuration port_config = binding.getModelConfiguration().getFirstChild(PortModel.PORT);
        assertEquals("MyWebService/SOAPPort", port_config.getValue());
        assertEquals("true", port_config.getAttribute(PortModel.SECURE));
        Configuration wsdl_config = binding.getModelConfiguration().getFirstChild(WSDLModel.WSDL);
        assertEquals("service.wsdl", wsdl_config.getValue());
        assertEquals("foobar", wsdl_config.getAttribute(WSDLModel.DESCRIPTION));
    }

    @Test
    public void testReadCustomViaModel() throws Exception {
        CompositeModel composite = _puller.pull(COMPLETE_XML, getClass());
        CompositeServiceModel compositeService = composite.getServices().get(0);
        BindingModel binding = (BindingModel)compositeService.getBindings().get(0);
        assertTrue(binding instanceof SOAPBindingModel);
        SOAPBindingModel soap_binding = (SOAPBindingModel)compositeService.getBindings().get(0);
        assertEquals("soap", binding.getType());
        PortModel port = soap_binding.getPort();
        assertEquals("MyWebService/SOAPPort", port.getPort());
        assertEquals(true, port.isSecure());
        WSDLModel wsdl = soap_binding.getWSDL();
        assertEquals("service.wsdl", wsdl.getLocation());
        assertEquals("foobar", wsdl.getDescription());
    }

    @Test
    public void testReadComplete() throws Exception {
        CompositeModel composite = _puller.pull(COMPLETE_XML, getClass());
        assertEquals(CompositeModel.DEFAULT_NAMESPACE, composite.getModelConfiguration().getQName().getNamespaceURI());
        assertEquals("m1app", composite.getName());
        CompositeServiceModel compositeService = composite.getServices().get(0);
        assertEquals("M1AppService", compositeService.getName());
        assertEquals("SimpleService", compositeService.getPromote());
        SOAPBindingModel binding1 = (SOAPBindingModel)compositeService.getBindings().get(0);
        assertEquals("soap", binding1.getType());
        PortModel port = binding1.getPort();
        assertEquals("MyWebService/SOAPPort", port.getPort());
        assertEquals(true, port.isSecure());
        WSDLModel wsdl1 = binding1.getWSDL();
        assertEquals("service.wsdl", wsdl1.getLocation());
        assertEquals("foobar", wsdl1.getDescription());
        CompositeReferenceModel compositeReference = composite.getReferences().get(0);
        assertEquals("SomeOtherService", compositeReference.getName());
        assertEquals("SimpleService/AnotherService", compositeReference.getPromote());
        SOAPBindingModel binding2 = (SOAPBindingModel)compositeReference.getBindings().get(0);
        assertEquals("soap", binding2.getType());
        WSDLModel wsdl = binding2.getWSDL();
        assertEquals("http://exmample.org:8080/services/SomeOtherService?wsdl", wsdl.getLocation());
        ComponentModel component1 = composite.getComponents().get(0);
        assertEquals("SimpleService", component1.getName());
        ComponentImplementationModel implementation1 = component1.getImplementation();
        assertEquals("bean", implementation1.getType());
        ComponentServiceModel componentService1 = component1.getServices().get(0);
        assertEquals("SimpleService", componentService1.getName());
        InterfaceModel interface1 = componentService1.getInterface();
        assertEquals("java", interface1.getType());
        assertEquals("org.switchyard.example.m1app.SimpleService", interface1.getInterface());
        ComponentReferenceModel reference = component1.getReferences().get(0);
        assertEquals("anotherService", reference.getName());
        InterfaceModel interface2 = reference.getInterface();
        assertEquals("java", interface2.getType());
        assertEquals("org.switchyard.example.m1app.AnotherService", interface2.getInterface());
        ComponentModel component2 = composite.getComponents().get(1);
        assertEquals("AnotherService", component2.getName());
        ComponentImplementationModel implementation2 = component2.getImplementation();
        assertEquals("bean", implementation2.getType());
        ComponentServiceModel componentService2 = component2.getServices().get(0);
        assertEquals("AnotherService", componentService2.getName());
        InterfaceModel interface3 = componentService2.getInterface();
        assertEquals("java", interface3.getType());
        assertEquals("org.switchyard.example.m1app.AnotherService", interface3.getInterface());
    }
    
    @Test
    public void testBindingModel() throws Exception {
        CompositeModel composite = _puller.pull(COMPLETE_XML, getClass());
        // Test service binding
        BindingModel serviceBinding = composite.getServices().get(0).getBindings().get(0);
        assertTrue(serviceBinding.isServiceBinding());
        assertFalse(serviceBinding.isReferenceBinding());
        assertNotNull(serviceBinding.getService());
        assertNull(serviceBinding.getReference());
        // Test reference binding
        BindingModel referenceBinding = composite.getReferences().get(0).getBindings().get(0);
        assertTrue(referenceBinding.isReferenceBinding());
        assertFalse(referenceBinding.isServiceBinding());
        assertNotNull(referenceBinding.getReference());
        assertNull(referenceBinding.getService());
    }

    @Test
    public void testPropertyModel() throws Exception {
        CompositeModel composite = _puller.pull(COMPLETE_XML, getClass());
        // Test composite property
        assertEquals(2, composite.getProperties().size());
        PropertyResolver compositePr = composite.getModelConfiguration().getPropertyResolver();
        assertEquals("composite.bar", compositePr.resolveProperty("composite.foo"));
        assertEquals("composite." + System.getProperty("user.name"), composite.resolveProperty("composite.userName"));
        // Test component property
        ComponentModel component = composite.getComponents().get(0);
        assertEquals(3, component.getProperties().size());
        PropertyResolver componentPr = component.getModelConfiguration().getPropertyResolver();
        assertEquals("composite.bar", componentPr.resolveProperty("composite.foo"));
        assertEquals("component.bar", componentPr.resolveProperty("component.foo"));
        assertEquals("component." + System.getProperty("user.name"), componentPr.resolveProperty("component.userName"));
    }
    
    @Test
    public void testWriteComplete() throws Exception {
        String old_xml = new StringPuller().pull(COMPLETE_XML, getClass());
        CompositeModel composite = _puller.pull(new StringReader(old_xml));
        String new_xml = composite.toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(old_xml, new_xml);
        assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testReadExtended() throws Exception {
        CompositeModel cm = _puller.pull(EXTENDED_XML, getClass());
        BogusImplementationModel bim = (BogusImplementationModel)cm.getComponents().get(0).getImplementation();
        assertEquals("bar", bim.getFoo());
    }

    @Test
    public void testReadExtendedWithService() throws Exception {
        CompositeModel cm = _puller.pull(EXTENDED_XML, getClass());
        ComponentModel component = cm.getComponents().get(0);
        assertEquals(1, component.getServices().size());
    }

    @Test
    public void testParenthood() throws Exception {
        CompositeModel composite_1 = _puller.pull(COMPLETE_XML, getClass());
        CompositeServiceModel service_1 = composite_1.getServices().get(0);
        SOAPBindingModel binding = (SOAPBindingModel)service_1.getBindings().get(0);
        CompositeServiceModel service_2 = binding.getService();
        CompositeModel composite_2 = service_2.getComposite();
        assertEquals(service_1, service_2);
        assertEquals(composite_1, composite_2);
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
        assertEquals(type, model.getType());
    }

    @Test
    public void testReadExtensionRefferingNoNamespacedSchema() throws Exception {
        CompositeModel composite = _puller.pull(EXTENSION_XML, getClass());
        CompositeServiceModel compositeService = composite.getServices().get(0);
        BindingModel binding = compositeService.getBindings().get(0);

        assertTrue(binding instanceof ExtensionBindingModel);
        ExtensionBindingModel extension = (ExtensionBindingModel) binding;
        assertEquals("extension", extension.getType());
        assertEquals("Bar", extension.getGroup());
        assertEquals("Foo", extension.getName());
    }
    
    @Test
    public void testSCABinding() throws Exception {
        CompositeModel composite = _puller.pull(SCA_BINDING_XML, getClass());
        
        // test binding.sca on composite service
        CompositeServiceModel compositeService = composite.getServices().get(0);
        BindingModel sb = compositeService.getBindings().get(0);
        assertTrue(sb instanceof SCABindingModel);
        
        // test binding.sca on composite reference
        CompositeReferenceModel compositeReference = composite.getReferences().get(0);
        BindingModel rb = compositeReference.getBindings().get(0);
        assertTrue(rb instanceof SCABindingModel);
    }
    
    @Test
    public void testPromote() throws Exception {
        CompositeModel composite = _puller.pull(PROMOTE_XML, getClass());
        CompositeServiceModel singlePromoteCompositeSrv = null;
        for (CompositeServiceModel compositeSrv : composite.getServices()) {
            if ("SinglePromoteExampleService".equals(compositeSrv.getName())) {
                singlePromoteCompositeSrv = compositeSrv;
            }
        }
        Assert.assertNotNull("null SinglePromoteExampleService", singlePromoteCompositeSrv);
        ComponentServiceModel componentSrv = singlePromoteCompositeSrv.getComponentService();
        Assert.assertNotNull("null FooExampleService", componentSrv);
        Assert.assertEquals("FooExampleService", componentSrv.getName());
        CompositeReferenceModel singlePromoteCompositeRef = null;
        CompositeReferenceModel multiPromoteCompositeRef = null;
        for (CompositeReferenceModel compositeRef : composite.getReferences()) {
            if ("SinglePromoteExampleReference".equals(compositeRef.getName())) {
                singlePromoteCompositeRef = compositeRef;
            } else if ("MultiPromoteExampleReference".equals(compositeRef.getName())) {
                multiPromoteCompositeRef = compositeRef;
            }
        }
        Assert.assertNotNull("null SinglePromoteExampleReference", singlePromoteCompositeRef);
        Assert.assertEquals(1, singlePromoteCompositeRef.getComponentReferences().size());
        Assert.assertNotNull("null MultiPromoteExampleReference", multiPromoteCompositeRef);
        Assert.assertEquals(2, multiPromoteCompositeRef.getComponentReferences().size());
    }

    @Test
    public void testValidationOfExtensionRefferingNoNamespacedSchema() throws Exception {
        _puller.pull(EXTENSION_XML, getClass()).assertModelValid();
    }

}
