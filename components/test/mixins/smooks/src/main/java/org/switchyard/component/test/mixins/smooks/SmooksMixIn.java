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

package org.switchyard.component.test.mixins.smooks;

import javax.xml.namespace.QName;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.test.mixins.AbstractTestMixIn;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.SmooksTransformType;
import org.switchyard.transform.config.model.TransformNamespace;
import org.switchyard.transform.config.model.v1.V1SmooksTransformModel;

/**
 * Smooks test {@link org.switchyard.test.TestMixIn mix-in}.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SmooksMixIn extends AbstractTestMixIn {

    private boolean _dumpResultsToSysout = false;

    /**
     * Dump transform results to system out.
     * <p/>
     * Used for test debugging purposes.
     *
     * @param dumpResultsToSysout True to dump result, otherwise false.
     */
    public void dumpResultsToSysout(boolean dumpResultsToSysout) {
        this._dumpResultsToSysout = dumpResultsToSysout;
    }

    /**
     * Test the Smooks Java/XML binding.
     * <p/>
     * This test method provides a round-trip Java/XML binding test.  It uses the Smooks SwitchYard transformer
     * (created from the specified {@code bindingConfigResPath}) to read the specified {@code xmlResPath} from the
     * test classpath, bind its XML content to an instance of the specified {@code javaType}
     * ({@link SmooksTransformType#XML2JAVA}) and then serializes that {@code javaType} instance back to XML
     * ({@link SmooksTransformType#JAVA2XML}), comparing the serialized XML to the XML specified by the
     * {@code xmlResPath}, failing if they don't match.
     *
     * @param javaType The Java type.
     * @param bindingConfigResPath The Smooks java binding configuration.  This path can be relative to
     * the java package containing the test class using this {@link org.switchyard.test.TestMixIn}.
     * @param xmlResPath The XML classpath resource used to bind to the {@code javaType}.  This path can be relative to
     * the java package containing the test class using this {@link org.switchyard.test.TestMixIn}.
     */
    public void testJavaXMLReadWrite(Class<?> javaType, String bindingConfigResPath, String xmlResPath) {
        Transformer xml2JavaTransformer = newTransformer(bindingConfigResPath, SmooksTransformType.XML2JAVA);
        Transformer java2xmlTransformer = newTransformer(bindingConfigResPath, SmooksTransformType.JAVA2XML);
        String xml = getTestKit().readResourceString(xmlResPath);

        // XML to Java...
        Object javaObject = xml2JavaTransformer.transform(xml);
        Assert.assertNotNull("XML to Java transformation failed.  null object returned.", javaObject);
        Assert.assertTrue("XML to Java transformation failed.  Wrong type returned.  Expected '" + javaType.getName() + "' but got '" + javaObject.getClass().getName() + "'.", javaType.isInstance(javaObject));
        if (_dumpResultsToSysout) {
            System.out.println("------------XML to Java Result:\n" + javaObject);
            System.out.println("-------------------------------");
        }

        // Java to XML...
        String xmlResult = (String) java2xmlTransformer.transform(javaObject);
        Assert.assertNotNull("Java to XML transformation failed.  null object returned.", javaObject);
        if (_dumpResultsToSysout) {
            System.out.println("------------Java to XML Result:\n" + xmlResult);
            System.out.println("-------------------------------");
        }

        // Compare the serialized Java Object XML to the XML used to create Java Object... should be the same...
        XMLUnit.setIgnoreWhitespace(true);
        try {
            XMLAssert.assertXMLEqual(xml, xmlResult);
        } catch (Exception e) {
            Assert.fail("Unexpected error performing XML comparison.");
        }
    }

    /**
     * Create a new Smooks {@link Transformer} instance.
     * @param smooksConfigResPath The Smooks configuration classpath resource path.
     * @param transformerType The Smooks transformer type.
     * @return The Transformer instance.
     */
    public Transformer newTransformer(String smooksConfigResPath, SmooksTransformType transformerType) {
        TransformModel transformModel = new V1SmooksTransformModel(TransformNamespace.DEFAULT.uri())
                                            .setConfig(smooksConfigResPath)
                                            .setTransformType(transformerType)
                                            .setFrom(new QName("from")).setTo(new QName("to"));

        return getTestKit().newTransformer(transformModel);
    }
}
