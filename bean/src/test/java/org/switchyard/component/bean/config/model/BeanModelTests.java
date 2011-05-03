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

package org.switchyard.component.bean.config.model;

import java.io.StringReader;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.common.io.resource.StringResource;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.ModelResource;
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

    private ModelResource<SwitchYardModel> _res;

    @Before
    public void before() throws Exception {
        _res = new ModelResource<SwitchYardModel>();
    }

    @Test
    public void testReadComplete() throws Exception {
        SwitchYardModel switchyard = _res.pull(COMPLETE_XML, getClass());
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
        String old_xml = new StringResource().pull(COMPLETE_XML, getClass());
        SwitchYardModel switchyard = _res.pull(new StringReader(old_xml));
        String new_xml = switchyard.toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(old_xml, new_xml);
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testValidation() throws Exception {
        SwitchYardModel switchyard = _res.pull(COMPLETE_XML, getClass());
        Assert.assertTrue(switchyard.isModelValid());
    }

}
