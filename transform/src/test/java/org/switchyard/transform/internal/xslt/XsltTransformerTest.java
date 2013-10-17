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
package org.switchyard.transform.internal.xslt;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.SwitchYardException;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.internal.DefaultMessage;
import org.switchyard.internal.transform.BaseTransformerRegistry;
import org.switchyard.transform.AbstractTransformerTestCase;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerRegistry;
import org.switchyard.transform.config.model.TransformNamespace;
import org.switchyard.transform.config.model.v1.V1XsltTransformModel;
import org.switchyard.transform.internal.TransformerRegistryLoader;
import org.switchyard.transform.xslt.internal.XsltTransformer;
import org.xml.sax.SAXException;

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
        	boolean exceptionMatch = e.getMessage().contains("SWITCHYARD016801");
        	Assert.assertTrue(exceptionMatch);
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
        	boolean exceptionMatch = e.getMessage().contains("SWITCHYARD016800");
        	Assert.assertTrue(exceptionMatch);
        }
    }

    @Test
    public void test_factoryLoad() {
        V1XsltTransformModel model = new V1XsltTransformModel(TransformNamespace.DEFAULT.uri());

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
        	boolean exceptionMatch = e.getMessage().contains("SWITCHYARD016802");
        	Assert.assertTrue(exceptionMatch);
        }
    }

    @Test
    public void test_xsl_include_with_href() throws IOException, SAXException {
        try {
            Transformer transformer = getTransformer("xslt-config-include-href.xml");

            Assert.assertTrue(transformer instanceof XsltTransformer);
        } catch (SwitchYardException e) {
            boolean exceptionMatch = e.getMessage().contains("SWITCHYARD016802");
            Assert.assertTrue(exceptionMatch);
        }
    }
    
    @Test
    public void test_xsl_include_with_invalid_href() throws IOException, SAXException {
        try {
            Transformer transformer = getTransformer("xslt-config-include-invalid-href.xml");
            
            Assert.fail("No SwitchYardException has been thrown");
        } catch (SwitchYardException e) {
	    boolean exceptionMatch = e.getMessage().contains("SWITCHYARD016802");
            Assert.assertTrue(exceptionMatch);
        }
    }
    private DefaultMessage newMessage(Object content) {
        DefaultMessage message = new DefaultMessage().setContent(content);
        message.setTransformerRegistry(xformReg);
        return message;
    }

}
