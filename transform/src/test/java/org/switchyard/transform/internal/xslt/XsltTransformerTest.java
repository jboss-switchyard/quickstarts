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
package org.switchyard.transform.internal.xslt;

import java.io.IOException;
import java.io.InputStream;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.internal.DefaultMessage;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.transform.AbstractTransformerTestCase;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;
import org.switchyard.transform.TransformerRegistryLoader;
import org.switchyard.transform.TransformerUtil;
import org.switchyard.transform.config.model.XsltTransformModel;
import org.switchyard.transform.config.model.v1.V1XsltTransformModel;
import org.switchyard.transform.ootb.AbstractTransformerTest;
import org.switchyard.transform.xslt.XsltTransformFactory;
import org.switchyard.transform.xslt.XsltTransformer;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;

/**
 * @author Alejandro Montenegro <a
 *         href="mailto:aamonten@gmail.com">aamonten@gmail.com</a>
 */
public class XsltTransformerTest extends AbstractTransformerTestCase {

    private TransformerRegistry xformReg;

    private final static String INITIAL = "<?xml version=\"1.0\"?><project><topic><title>Switchyard</title><url>http://www.jboss.org/switchyard</url>"
            + "</topic><topic><title>Arquillian</title><url>http://www.jboss.org/arquillian</url></topic><topic><title>Drools</title>"
            + "<url>http://www.jboss.org/drools</url></topic><topic><title>JBoss Tools</title><url>http://www.jboss.org/tools</url>"
            + "</topic></project>";

    private final static String INITIAL_FAIL = "<?xml version=\"1.0\"?><project><topic><title>Switchyard</title><url>http://www.jboss.org/switchyard</url>"
            + "</topic><topic><title>Arquillian</title><url>http://www.jboss.org/arquillian</url></topic><topic><title>Drools</title>"
            + "<url>http://www.jboss.org/drools</url></topic><topic><title>JBoss Tools</title><url>http://www.jboss.org/tools</url>";

    private final static String EXPECTED = "<?xml version=\"1.0\"?><index><head>"
            + "<title>JBoss Project's'</title></head><body><table border=\"1\"><tr><th>Title</th><th>URL</th></tr><tr>"
            + "<td>Switchyard</td><td>http://www.jboss.org/switchyard</td></tr><tr><td>Arquillian</td><td>http://www.jboss.org/arquillian</td>"
            + "</tr><tr><td>Drools</td><td>http://www.jboss.org/drools</td></tr><tr><td>JBoss Tools</td><td>http://www.jboss.org/tools</td>"
            + "</tr></table></body></index>";

    public XsltTransformerTest() {
        xformReg = new BaseTransformerRegistry();
        new TransformerRegistryLoader(xformReg).loadOOTBTransforms();
    }

    @Test
    public void test_no_validation() throws IOException {
        try {
            getTransformer("xslt-config-01.xml");
        } catch (RuntimeException e) {
            Assert.fail("failed to load configuration file xslt-config-01.xml");
        }
    }
    
    @Test
    public void test_validation() throws Exception {
        InputStream swConfigStream = Classes.getResourceAsStream("xslt-config-01.xml", getClass());
        try {
            SwitchYardModel switchyardConfig = new ModelPuller<SwitchYardModel>().pull(swConfigStream);
            switchyardConfig.assertModelValid();
        } finally {
            swConfigStream.close();
        }
    }

    @Test
    public void test_no_xslt_file() throws IOException {
        try {
            getTransformer("xslt-config-02.xml");
            Assert.fail("the configuration file should be invalid");
        } catch (RuntimeException e) {
            Assert.assertEquals("No xsl file has been defined. Check your transformer configuration.",e.getMessage());
        }
    }

    @Test
    public void test_xslt_result() throws IOException, SAXException {
        Transformer transformer = getTransformer("xslt-config-03.xml");
        DefaultMessage message = newMessage(INITIAL);
        transformer.transform(message);
        String result = message.getContent(String.class);

        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(EXPECTED, result);
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void test_local_xslt_file_fail() throws IOException {
        try {
            Transformer transformer = getTransformer("xslt-config-03.xml");
            DefaultMessage message = newMessage(INITIAL_FAIL);
            Object result = transformer.transform(message);
            Assert.fail("xml to transform should be invalid");
        } catch (SwitchYardException e) {
            Assert.assertEquals("Error during xslt transformation",e.getMessage());
        }
    }

    @Test
    public void test_factoryLoad() {
        V1XsltTransformModel model = new V1XsltTransformModel();

        model.setXsltFile("org/switchyard/transform/internal/xslt/topics.xslt");
        model.setFrom(new QName("A"));
        model.setTo(new QName("B"));

        TransformerRegistryLoader trl = new TransformerRegistryLoader(new BaseTransformerRegistry());
        Transformer<?,?> transformer = trl.newTransformer(model);

        Assert.assertTrue(transformer instanceof XsltTransformer);
    }

    @Test
    public void test_failonwarn_false_warn() throws IOException, SAXException {
        Transformer transformer = getTransformer("xslt-config-failonwarn-false-warn.xml");
        DefaultMessage message = newMessage(INITIAL);
        transformer.transform(message);
        String result = message.getContent(String.class);

        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(EXPECTED, result);
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void test_failonwarn_true_warn() throws IOException, SAXException {
        try {
            Transformer transformer = getTransformer("xslt-config-failonwarn-true-warn.xml");

            Assert.fail("No SwitchYardException has been thrown");
        } catch (SwitchYardException e) {
            Assert.assertEquals("An unexpected error ocurred while creating the xslt transformer", e.getMessage());
        }
    }

    private DefaultMessage newMessage(Object content) {
        DefaultMessage message = new DefaultMessage().setContent(content);
        message.setTransformerRegistry(xformReg);
        return message;
    }

}
