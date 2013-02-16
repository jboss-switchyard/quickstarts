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

package org.switchyard.validate.config.model;

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
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.v1.V1SwitchYardModel;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.config.model.validate.ValidatesModel;
import org.switchyard.config.model.validate.v1.V1ValidatesModel;
import org.switchyard.validate.config.model.v1.V1FileEntryModel;
import org.switchyard.validate.config.model.v1.V1JavaValidateModel;
import org.switchyard.validate.config.model.v1.V1SchemaFilesModel;
import org.switchyard.validate.config.model.v1.V1XmlValidateModel;

/**
 * ValidateModelTests.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class ValidateModelTests {

    private static final String XML = "/org/switchyard/validate/config/model/ValidateModelTests.xml";

    private ModelPuller<SwitchYardModel> _puller;

    @Before
    public void before() throws Exception {
        _puller = new ModelPuller<SwitchYardModel>();
    }

    @Test
    public void testCreateEmptyModel() throws Exception {
        String namespace = ValidateModel.DEFAULT_NAMESPACE;
        String name = ValidateModel.VALIDATE + '.' + JavaValidateModel.JAVA;
        Model model = new ModelPuller<Model>().pull(XMLHelper.createQName(namespace, name));
        Assert.assertTrue(model instanceof JavaValidateModel);
        Assert.assertEquals(name, model.getModelConfiguration().getName());
        Assert.assertEquals(new QName(namespace, name), model.getModelConfiguration().getQName());
    }

    @Test
    public void testCreate() throws Exception {
        SwitchYardModel switchyard = new V1SwitchYardModel();
        ValidatesModel validates = new V1ValidatesModel();
        JavaValidateModel javaValidate = new V1JavaValidateModel();
        javaValidate.setName(new QName("msgA"));
        javaValidate.setClazz("org.examples.validate.AValidate");
        validates.addValidate(javaValidate);
        XmlValidateModel xmlValidate = new V1XmlValidateModel();
        xmlValidate.setName(new QName("msgB"));
        xmlValidate.setSchemaType(XmlSchemaType.XML_SCHEMA);
        FileEntryModel entry = new V1FileEntryModel().setFile("/validates/xxx.xml");
        SchemaFilesModel schemaFiles = new V1SchemaFilesModel();
        schemaFiles.addEntry(entry);
        xmlValidate.setSchemaFiles(schemaFiles);
        xmlValidate.setFailOnWarning(true);
        validates.addValidate(xmlValidate);
        switchyard.setValidates(validates);
        String new_xml = switchyard.toString();
        String old_xml = new ModelPuller<SwitchYardModel>().pull(XML, getClass()).toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(old_xml, new_xml);
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testRead() throws Exception {
        SwitchYardModel switchyard = _puller.pull(XML, getClass());
        ValidatesModel validates = switchyard.getValidates();
        JavaValidateModel java_validate = (JavaValidateModel)validates.getValidates().get(0);
        Assert.assertEquals("msgA", java_validate.getName().getLocalPart());
        Assert.assertEquals("org.examples.validate.AValidate", java_validate.getClazz());
        XmlValidateModel xml_validate = (XmlValidateModel)validates.getValidates().get(1);
        Assert.assertEquals("msgB", xml_validate.getName().getLocalPart());
        Assert.assertEquals("/validates/xxx.xml", xml_validate.getSchemaFiles().getEntries().get(0).getFile());

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
        ValidatesModel validates_1 = switchyard_1.getValidates();
        ValidateModel validate = validates_1.getValidates().get(0);
        ValidatesModel validates_2 = validate.getValidates();
        SwitchYardModel switchyard_2 = validates_2.getSwitchYard();
        Assert.assertEquals(validates_1, validates_2);
        Assert.assertEquals(switchyard_1, switchyard_2);
    }

    @Test
    public void testValidation() throws Exception {
        SwitchYardModel switchyard = _puller.pull(XML, getClass());
        switchyard.assertModelValid();
    }

}
