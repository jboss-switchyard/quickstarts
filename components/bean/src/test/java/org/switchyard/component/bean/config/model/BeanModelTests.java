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

package org.switchyard.component.bean.config.model;

import java.io.StringReader;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * BeanModelTests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class BeanModelTests {

    private static final String COMPLETE_XML = "/org/switchyard/component/bean/config/model/BeanModelTests-Complete.xml";

    private ModelPuller<SwitchYardModel> _puller;

    @Before
    public void before() throws Exception {
        _puller = new ModelPuller<SwitchYardModel>();
    }

    @Test
    public void testReadComplete() throws Exception {
        SwitchYardModel switchyard = _puller.pull(COMPLETE_XML, getClass());
        CompositeModel composite = switchyard.getComposite();
        ComponentModel component = composite.getComponents().get(0);
        ComponentImplementationModel implementation = component.getImplementation();
        Assert.assertTrue(implementation instanceof BeanComponentImplementationModel);
        BeanComponentImplementationModel bci = (BeanComponentImplementationModel)implementation;
        Assert.assertEquals("bean", bci.getType());
        Assert.assertEquals("org.switchyard.example.m1app.SimpleBean", bci.getClazz());
        Configuration config = bci.getModelConfiguration();
        Assert.assertEquals("implementation.bean", config.getName());
        QName qname = config.getQName();
        Assert.assertEquals("urn:switchyard-component-bean:config:1.0", qname.getNamespaceURI());
        Assert.assertEquals("implementation.bean", qname.getLocalPart());
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
        switchyard.assertModelValid();
    }

}
