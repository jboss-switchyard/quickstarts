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
import java.util.Properties;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.config.Configuration;
import org.switchyard.config.Descriptor;
import org.switchyard.config.PropertiesResource;
import org.switchyard.config.StringResource;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.Models;
import org.switchyard.config.model.composite.test.bogus.BogusImplementationModel;
import org.switchyard.config.model.composite.test.bogus.BogusModelMarshaller;
import org.switchyard.config.model.composite.test.soap.PortModel;
import org.switchyard.config.model.composite.test.soap.SOAPBindingModel;
import org.switchyard.config.model.composite.test.soap.SOAPModelMarshaller;
import org.switchyard.config.model.composite.test.soap.WSDLModel;

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

    private static ModelResource _res;

    @Before
    public void before() throws Exception {
        Properties props = new PropertiesResource().pull(Descriptor.DEFAULT_PROPERTIES);
        props.setProperty("soap.namespace", SOAPModelMarshaller.NAMESPACE);
        props.setProperty("soap.modelMarshaller", SOAPModelMarshaller.class.getName());
        _res = new ModelResource(new Descriptor(props));
    }

    @Test
    public void testCreateEmptyModel() throws Exception {
        String namespace = CompositeModel.DEFAULT_NAMESPACE;
        String name = CompositeModel.COMPOSITE;
        Model model = Models.create(namespace, name);
        Assert.assertTrue(model instanceof CompositeModel);
        Assert.assertEquals(name, model.getModelConfiguration().getName());
        Assert.assertEquals(new QName(namespace, name), model.getModelConfiguration().getQName());
    }

    @Test
    public void testMerge() throws Exception {
        CompositeModel incomplete_composite = (CompositeModel)_res.pull(INCOMPLETE_XML);
        CompositeModel fragment_composite = (CompositeModel)_res.pull(FRAGMENT_XML);
        CompositeModel merged_composite = (CompositeModel)Models.merge(fragment_composite, incomplete_composite);
        //merged_composite.write(System.out);
        XMLUnit.setIgnoreWhitespace(true);
        CompositeModel complete_composite = (CompositeModel)_res.pull(COMPLETE_XML);
        Diff diff = XMLUnit.compareXML(complete_composite.toString(), merged_composite.toString());
        Assert.assertTrue(diff.identical());
    }

    @Test
    public void testReadCustomViaConfig() throws Exception {
        CompositeModel composite = (CompositeModel)_res.pull(COMPLETE_XML);
        ExternalServiceModel externalService = composite.getServices().get(0);
        BindingModel binding = externalService.getBindings().get(0);
        Assert.assertEquals("soap", binding.getType());
        Configuration port_config = binding.getModelConfiguration().getFirstChild(PortModel.PORT);
        Assert.assertEquals("MyWebService/SOAPPort", port_config.getValue());
        Assert.assertEquals("true", port_config.getAttribute(PortModel.SECURE));
        Configuration wsdl_config = binding.getModelConfiguration().getFirstChild(WSDLModel.WSDL);
        Assert.assertEquals("service.wsdl", wsdl_config.getValue());
        Assert.assertEquals("foobar", wsdl_config.getAttribute(WSDLModel.DESCRIPTION));
        //composite.write(System.out);
    }

    @Test
    public void testReadCustomViaModel() throws Exception {
        CompositeModel composite = (CompositeModel)_res.pull(COMPLETE_XML);
        ExternalServiceModel externalService = composite.getServices().get(0);
        BindingModel binding = (BindingModel)externalService.getBindings().get(0);
        Assert.assertTrue(binding instanceof SOAPBindingModel);
        SOAPBindingModel soap_binding = (SOAPBindingModel)externalService.getBindings().get(0);
        Assert.assertEquals("soap", binding.getType());
        PortModel port = soap_binding.getPort();
        Assert.assertEquals("MyWebService/SOAPPort", port.getPort());
        Assert.assertEquals(true, port.isSecure());
        WSDLModel wsdl = soap_binding.getWSDL();
        Assert.assertEquals("service.wsdl", wsdl.getLocation());
        Assert.assertEquals("foobar", wsdl.getDescription());
        //composite.write(System.out);
    }

    @Test
    public void testReadComplete() throws Exception {
        CompositeModel composite = (CompositeModel)_res.pull(COMPLETE_XML);
        Assert.assertEquals(CompositeModel.DEFAULT_NAMESPACE, composite.getModelConfiguration().getQName().getNamespaceURI());
        Assert.assertEquals("m1app", composite.getName());
        ExternalServiceModel externalService = composite.getServices().get(0);
        Assert.assertEquals("M1AppService", externalService.getName());
        Assert.assertEquals(new QName("SimpleService"), externalService.getPromote());
        SOAPBindingModel binding = (SOAPBindingModel)externalService.getBindings().get(0);
        Assert.assertEquals("soap", binding.getType());
        PortModel port = binding.getPort();
        Assert.assertEquals("MyWebService/SOAPPort", port.getPort());
        Assert.assertEquals(true, port.isSecure());
        WSDLModel wsdl = binding.getWSDL();
        Assert.assertEquals("service.wsdl", wsdl.getLocation());
        Assert.assertEquals("foobar", wsdl.getDescription());
        ComponentModel component1 = composite.getComponents().get(0);
        Assert.assertEquals(component1, externalService.getComponent());
        Assert.assertEquals("SimpleService", component1.getName());
        ImplementationModel implementation1 = component1.getImplementation();
        Assert.assertEquals("bean", implementation1.getType());
        Assert.assertEquals("org.switchyard.example.m1app.SimpleBean", implementation1.getClazz());
        InternalServiceModel internalService1 = component1.getServices().get(0);
        Assert.assertEquals("SimpleService", internalService1.getName());
        InternalServiceInterfaceModel interface1 = internalService1.getInterface();
        Assert.assertEquals("java", interface1.getType());
        Assert.assertEquals("org.switchyard.example.m1app.SimpleService", interface1.getInterface());
        ReferenceModel reference = component1.getReferences().get(0);
        Assert.assertEquals("anotherService", reference.getName());
        ReferenceInterfaceModel interface2 = reference.getInterface();
        Assert.assertEquals("java", interface2.getType());
        Assert.assertEquals("org.switchyard.example.m1app.AnotherService", interface2.getInterface());
        ComponentModel component2 = composite.getComponents().get(1);
        Assert.assertEquals("AnotherService", component2.getName());
        ImplementationModel implementation2 = component2.getImplementation();
        Assert.assertEquals("bean", implementation2.getType());
        Assert.assertEquals("org.switchyard.example.m1app.AnotherBean", implementation2.getClazz());
        InternalServiceModel internalService2 = component2.getServices().get(0);
        Assert.assertEquals("AnotherService", internalService2.getName());
        InternalServiceInterfaceModel interface3 = internalService2.getInterface();
        Assert.assertEquals("java", interface3.getType());
        Assert.assertEquals("org.switchyard.example.m1app.AnotherService", interface3.getInterface());
    }

    @Test
    public void testWriteComplete() throws Exception {
        String old_xml = new StringResource().pull(COMPLETE_XML);
        CompositeModel composite = (CompositeModel)_res.pull(new StringReader(old_xml));
        String new_xml = composite.toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(old_xml, new_xml);
        Assert.assertTrue(diff.identical());
    }

    @Test
    public void testReadExtended() throws Exception {
        Properties props = new PropertiesResource().pull(Descriptor.DEFAULT_PROPERTIES);
        props.setProperty("bogus.namespace", "http://bogus.org/bogus/xsd/bogus.xsd");
        props.setProperty("bogus.modelMarshaller", BogusModelMarshaller.class.getName());
        ModelResource res = new ModelResource(new Descriptor(props));
        CompositeModel cm = (CompositeModel)res.pull(EXTENDED_XML);
        BogusImplementationModel bim = (BogusImplementationModel)cm.getComponents().get(0).getImplementation();
        Assert.assertEquals("bar", bim.getFoo());
    }

    @Test
    public void testParenthood() throws Exception {
        CompositeModel composite_1 = (CompositeModel)_res.pull(COMPLETE_XML);
        ExternalServiceModel service_1 = composite_1.getServices().get(0);
        SOAPBindingModel binding = (SOAPBindingModel)service_1.getBindings().get(0);
        ExternalServiceModel service_2 = binding.getService();
        CompositeModel composite_2 = service_2.getComposite();
        Assert.assertEquals(service_1, service_2);
        Assert.assertEquals(composite_1, composite_2);
    }

}
