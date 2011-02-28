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

package org.switchyard.transform.config.model;

import java.io.StringReader;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.ModelResource;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.config.model.transform.v1.V1TransformsModel;
import org.switchyard.config.util.QNames;
import org.switchyard.config.util.StringResource;
import org.switchyard.transform.config.model.v1.V1JavaTransformModel;
import org.switchyard.transform.config.model.v1.V1SmooksConfigModel;
import org.switchyard.transform.config.model.v1.V1SmooksTransformModel;

/**
 * TransformModelTests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class TransformModelTests {

    private static final String XML = "/org/switchyard/transform/config/model/TransformModelTests.xml";

    private ModelResource<SwitchYardModel> _res;

    @Before
    public void before() throws Exception {
        _res = new ModelResource<SwitchYardModel>();
    }

    @Test
    public void testCreateEmptyModel() throws Exception {
        String namespace = TransformModel.DEFAULT_NAMESPACE;
        String name = TransformModel.TRANSFORM + '.' + JavaTransformModel.JAVA;
        Model model = new ModelResource<Model>().pull(QNames.create(namespace, name));
        Assert.assertTrue(model instanceof JavaTransformModel);
        Assert.assertEquals(name, model.getModelConfiguration().getName());
        Assert.assertEquals(new QName(namespace, name), model.getModelConfiguration().getQName());
    }

    @Test
    public void testCreate() throws Exception {
        SwitchYardModel switchyard = new V1SwitchYardModel();
        TransformsModel transforms = new V1TransformsModel();
        JavaTransformModel javaTransform = new V1JavaTransformModel();
        javaTransform.setFrom(new QName("msgA"));
        javaTransform.setTo(new QName("msgB"));
        javaTransform.setClazz("org.examples.transform.AtoBTransform");
        transforms.addTransform(javaTransform);
        SmooksTransformModel smooksTransform = new V1SmooksTransformModel();
        smooksTransform.setFrom(new QName("msgC"));
        smooksTransform.setTo(new QName("msgD"));
        SmooksConfigModel smooksConfig = new V1SmooksConfigModel();
        smooksConfig.setData("stuff");
        smooksTransform.setConfig(smooksConfig);
        transforms.addTransform(smooksTransform);
        switchyard.setTransforms(transforms);
        String new_xml = switchyard.toString();
        String old_xml = new ModelResource<SwitchYardModel>().pull(XML).toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(old_xml, new_xml);
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testRead() throws Exception {
        SwitchYardModel switchyard = _res.pull(XML);
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
    }

    @Test
    public void testWrite() throws Exception {
        String old_xml = new StringResource().pull(XML);
        SwitchYardModel switchyard = _res.pull(new StringReader(old_xml));
        String new_xml = switchyard.toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(old_xml, new_xml);
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testParenthood() throws Exception {
        SwitchYardModel switchyard_1 = _res.pull(XML);
        TransformsModel transforms_1 = switchyard_1.getTransforms();
        TransformModel transform = transforms_1.getTransforms().get(0);
        TransformsModel transforms_2 = transform.getTransforms();
        SwitchYardModel switchyard_2 = transforms_2.getSwitchYard();
        Assert.assertEquals(transforms_1, transforms_2);
        Assert.assertEquals(switchyard_1, switchyard_2);
    }

    @Test
    public void testValidation() throws Exception {
        SwitchYardModel switchyard = _res.pull(XML);
        Assert.assertTrue(switchyard.isModelValid());
    }

}
