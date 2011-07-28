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
package org.switchyard.config.model.switchyard;

import java.io.StringReader;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.Models;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.test.soap.PortModel;
import org.switchyard.config.model.composite.test.soap.SOAPBindingModel;
import org.switchyard.config.model.domain.DomainModel;
import org.switchyard.config.model.domain.HandlerModel;
import org.switchyard.config.model.domain.PropertiesModel;
import org.switchyard.config.model.switchyard.test.java.JavaTransformModel;
import org.switchyard.config.model.switchyard.test.smooks.SmooksConfigModel;
import org.switchyard.config.model.switchyard.test.smooks.SmooksTransformModel;
import org.switchyard.config.model.transform.TransformsModel;

/**
 * SwitchYardModelTests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class SwitchYardModelTests {

    private static final String INCOMPLETE_XML = "/org/switchyard/config/model/switchyard/SwitchYardModelTests-Incomplete.xml";
    private static final String FRAGMENT_XML = "/org/switchyard/config/model/switchyard/SwitchYardModelTests-Fragment.xml";
    private static final String COMPLETE_XML = "/org/switchyard/config/model/switchyard/SwitchYardModelTests-Complete.xml";

    private ModelPuller<SwitchYardModel> _puller;

    @Before
    public void before() throws Exception {
        _puller = new ModelPuller<SwitchYardModel>();
    }

    @Test
    public void testCreateEmptyModel() throws Exception {
        String namespace = SwitchYardModel.DEFAULT_NAMESPACE;
        String name = SwitchYardModel.SWITCHYARD;
        Model model = new ModelPuller<Model>().pull(XMLHelper.createQName(namespace, name));
        Assert.assertTrue(model instanceof SwitchYardModel);
        Assert.assertEquals(name, model.getModelConfiguration().getName());
        Assert.assertEquals(new QName(namespace, name), model.getModelConfiguration().getQName());
    }

    @Test
    public void testMerge() throws Exception {
        SwitchYardModel incomplete_switchyard = _puller.pull(INCOMPLETE_XML, getClass());
        SwitchYardModel fragment_switchyard = _puller.pull(FRAGMENT_XML, getClass());
        SwitchYardModel merged_switchyard = Models.merge(fragment_switchyard, incomplete_switchyard);
        XMLUnit.setIgnoreWhitespace(true);
        SwitchYardModel complete_switchyard = _puller.pull(COMPLETE_XML, getClass());
        Diff diff = XMLUnit.compareXML(complete_switchyard.toString(), merged_switchyard.toString());
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testReadComplete() throws Exception {
        SwitchYardModel switchyard = _puller.pull(COMPLETE_XML, getClass());
        CompositeModel composite = switchyard.getComposite();
        CompositeServiceModel service = composite.getServices().get(0);
        // Verify composite service binding
        SOAPBindingModel binding = (SOAPBindingModel)service.getBindings().get(0);
        PortModel port = binding.getPort();
        Assert.assertEquals("MyWebService/SOAPPort", port.getPort());
        String name = port.getBinding().getService().getComposite().getComponents().get(0).getName();
        Assert.assertEquals("SimpleService", name);
        // Verify transform definitions
        TransformsModel transforms = switchyard.getTransforms();
        JavaTransformModel java_transform = (JavaTransformModel)transforms.getTransforms().get(0);
        Assert.assertEquals("msgA", java_transform.getFrom().getLocalPart());
        Assert.assertEquals("msgB", java_transform.getTo().getLocalPart());
        Assert.assertEquals("org.examples.transform.AtoBTransform", java_transform.getClazz());
        SmooksTransformModel smooks_transform = (SmooksTransformModel)transforms.getTransforms().get(1);
        Assert.assertEquals("msgC", smooks_transform.getFrom().getLocalPart());
        Assert.assertEquals("msgD", smooks_transform.getTo().getLocalPart());
        SmooksConfigModel smooks_config = smooks_transform.getConfig();
        Assert.assertEquals("stuff", smooks_config.getData());
        // Verify domain configuration
        DomainModel domain = switchyard.getDomain();
        Assert.assertEquals("TestDomain", domain.getName());
        PropertiesModel props = domain.getProperties();
        Assert.assertEquals(2, props.getProperties().size());
        Assert.assertEquals("bar", props.getProperty("foo").getValue());
        Assert.assertEquals("fish", props.getProperty("tuna").getValue());
        Assert.assertEquals(switchyard, domain.getSwitchYard());
        Assert.assertEquals(1, domain.getHandlers().getHandlers().size());
        HandlerModel handler = domain.getHandlers().getHandlers().get(0);
        Assert.assertEquals("handler1", handler.getName());
        Assert.assertEquals("org.switchyard.handlers.TestHandler", handler.getClassName());
    }

    @Test
    public void testWriteComplete() throws Exception {
        String old_xml = new StringPuller().pull(COMPLETE_XML, getClass());
        SwitchYardModel switchyard = _puller.pull(new StringReader(old_xml));
        String new_xml = switchyard.toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(old_xml, new_xml);
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testValidation() throws Exception {
        SwitchYardModel switchyard = _puller.pull(COMPLETE_XML, getClass());
        Assert.assertTrue(switchyard.isModelValid());
    }

    @Test
    public void testTargetNamespace() throws Exception {
        SwitchYardModel switchyard = _puller.pull(COMPLETE_XML, getClass());
        Assert.assertEquals("m1app", switchyard.getName());
        Assert.assertEquals(new QName("m1app"), switchyard.getQName());
        final String tns = "urn:m1app:example:1.0";
        CompositeModel composite = switchyard.getComposite();
        Assert.assertEquals("m1app", composite.getName());
        Assert.assertEquals(new QName(tns, "m1app"), composite.getQName());
        CompositeServiceModel compositeService = composite.getServices().iterator().next();
        Assert.assertEquals("M1AppService", compositeService.getName());
        Assert.assertEquals(new QName(tns, "M1AppService"), compositeService.getQName());
        ComponentModel component = composite.getComponents().iterator().next();
        Assert.assertEquals("SimpleService", component.getName());
        Assert.assertEquals(new QName(tns, "SimpleService"), component.getQName());
        ComponentServiceModel componentService = component.getServices().iterator().next();
        Assert.assertEquals("SimpleService", componentService.getName());
        Assert.assertEquals(new QName(tns, "SimpleService"), componentService.getQName());
        ComponentReferenceModel componentReference = component.getReferences().iterator().next();
        Assert.assertEquals("anotherService", componentReference.getName());
        Assert.assertEquals(new QName(tns, "anotherService"), componentReference.getQName());
    }

}
