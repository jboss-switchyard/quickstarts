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

package org.switchyard.transform.config.model;

import java.io.StringReader;

import javax.xml.namespace.QName;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.SwitchYardNamespace;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.config.model.transform.v1.V1TransformsModel;
import org.switchyard.transform.config.model.v1.V1JavaTransformModel;
import org.switchyard.transform.config.model.v1.V1SmooksTransformModel;

/**
 * TransformModelTests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class TransformModelTests {

    private static final String XML = "/org/switchyard/transform/config/model/TransformModelTests.xml";

    private ModelPuller<SwitchYardModel> _puller;

    @Before
    public void before() throws Exception {
        _puller = new ModelPuller<SwitchYardModel>();
    }

    @Test
    public void testCreateEmptyModel() throws Exception {
        String namespace = TransformNamespace.DEFAULT.uri();
        String name = TransformModel.TRANSFORM + '.' + JavaTransformModel.JAVA;
        Model model = new ModelPuller<Model>().pull(XMLHelper.createQName(namespace, name));
        Assert.assertTrue(model instanceof JavaTransformModel);
        Assert.assertEquals(name, model.getModelConfiguration().getName());
        Assert.assertEquals(new QName(namespace, name), model.getModelConfiguration().getQName());
    }

    @Test
    public void testCreate() throws Exception {
        SwitchYardModel switchyard = new V1SwitchYardModel(SwitchYardNamespace.V_1_0.uri());
        TransformsModel transforms = new V1TransformsModel(SwitchYardNamespace.V_1_0.uri());
        JavaTransformModel javaTransform = new V1JavaTransformModel(TransformNamespace.V_1_0.uri());
        javaTransform.setFrom(new QName("msgA"));
        javaTransform.setTo(new QName("msgB"));
        javaTransform.setClazz("org.examples.transform.AtoBTransform");
        transforms.addTransform(javaTransform);
        SmooksTransformModel smooksTransform = new V1SmooksTransformModel(TransformNamespace.V_1_0.uri());
        smooksTransform.setFrom(new QName("msgC"));
        smooksTransform.setTo(new QName("msgD"));
        smooksTransform.setTransformType("XML2JAVA");
        smooksTransform.setConfig("/trasnforms/xxx.xml");
        smooksTransform.setReportPath("/tmp/smooksreport.html");
        transforms.addTransform(smooksTransform);
        switchyard.setTransforms(transforms);
        String new_xml = switchyard.toString();
        String old_xml = new ModelPuller<SwitchYardModel>().pull(XML, getClass()).toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(old_xml, new_xml);
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testRead() throws Exception {
        SwitchYardModel switchyard = _puller.pull(XML, getClass());
        TransformsModel transforms = switchyard.getTransforms();
        JavaTransformModel java_transform = (JavaTransformModel)transforms.getTransforms().get(0);
        Assert.assertEquals("msgA", java_transform.getFrom().getLocalPart());
        Assert.assertEquals("msgB", java_transform.getTo().getLocalPart());
        Assert.assertEquals("org.examples.transform.AtoBTransform", java_transform.getClazz());
        SmooksTransformModel smooks_transform = (SmooksTransformModel)transforms.getTransforms().get(1);
        Assert.assertEquals("msgC", smooks_transform.getFrom().getLocalPart());
        Assert.assertEquals("msgD", smooks_transform.getTo().getLocalPart());
        Assert.assertEquals("/trasnforms/xxx.xml", smooks_transform.getConfig());
        Assert.assertEquals("/tmp/smooksreport.html", smooks_transform.getReportPath());

    }

    @Test
    public void testWrite() throws Exception {
        String old_xml = new StringPuller().pull(XML, getClass());
        SwitchYardModel switchyard = _puller.pull(new StringReader(old_xml));
        String new_xml = switchyard.toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(old_xml, new_xml);
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testParenthood() throws Exception {
        SwitchYardModel switchyard_1 = _puller.pull(XML, getClass());
        TransformsModel transforms_1 = switchyard_1.getTransforms();
        TransformModel transform = transforms_1.getTransforms().get(0);
        TransformsModel transforms_2 = transform.getTransforms();
        SwitchYardModel switchyard_2 = transforms_2.getSwitchYard();
        Assert.assertEquals(transforms_1, transforms_2);
        Assert.assertEquals(switchyard_1, switchyard_2);
    }

    @Test
    public void testValidation() throws Exception {
        SwitchYardModel switchyard = _puller.pull(XML, getClass());
        switchyard.assertModelValid();
    }

}
