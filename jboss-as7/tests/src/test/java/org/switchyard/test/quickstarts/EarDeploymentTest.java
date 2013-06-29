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
package org.switchyard.test.quickstarts;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;

/**
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
@RunWith(Arquillian.class)
public class EarDeploymentTest {

    private static String VERSION = System.getenv("SWITCHYARD_VERSION");

    private static String APPLICATION_XML = "<application xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                                            + " xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:application=\"http://java.sun.com/xml/ns/javaee/application_6.xsd\""
                                            + " xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/application_6.xsd\""
                                            + " id=\"Application_ID\" version=\"6\">\n"
                                            + "    <module>\n"
                                            + "        <java>switchyard-quickstart-bean-service-" + VERSION + ".jar</java>\n"
                                            + "    </module>\n"
                                            + "    <module>\n"
                                            + "        <java>switchyard-quickstart-camel-binding-" + VERSION + ".jar</java>\n"
                                            + "    </module>\n"
                                            + "    <module>\n"
                                            + "        <java>switchyard-quickstart-camel-soap-proxy-" + VERSION + ".jar</java>\n"
                                            + "    </module>\n"
                                            + "    <module>\n"
                                            + "        <java>switchyard-quickstart-transform-jaxb-" + VERSION + ".jar</java>\n"
                                            + "    </module>\n"
                                            + "    <module>\n"
                                            + "        <java>switchyard-quickstart-transform-json-" + VERSION + ".jar</java>\n"
                                            + "    </module>\n"
                                            + "    <module>\n"
                                            + "        <java>switchyard-quickstart-transform-smooks-" + VERSION + ".jar</java>\n"
                                            + "    </module>\n"
                                            + "    <module>\n"
                                            + "        <java>switchyard-quickstart-transform-xslt-" + VERSION + ".jar</java>\n"
                                            + "    </module>\n"
                                            + "</application>";

    @Deployment(testable = false)
    public static EnterpriseArchive createDeployment() {
        return ShrinkWrap
                .create(EnterpriseArchive.class)
                .setApplicationXML(new StringAsset(APPLICATION_XML))
                .addAsModule(ArquillianUtil.createJarQSDeployment("switchyard-quickstart-bean-service"))
                .addAsModule(ArquillianUtil.createJarQSDeployment("switchyard-quickstart-camel-binding"))
                .addAsModule(ArquillianUtil.createJarQSDeployment("switchyard-quickstart-camel-soap-proxy"))
                .addAsModule(ArquillianUtil.createJarQSDeployment("switchyard-quickstart-transform-jaxb"))
                .addAsModule(ArquillianUtil.createJarQSDeployment("switchyard-quickstart-transform-json"))
                .addAsModule(ArquillianUtil.createJarQSDeployment("switchyard-quickstart-transform-smooks"))
                .addAsModule(ArquillianUtil.createJarQSDeployment("switchyard-quickstart-transform-xslt"));
    }

    @Test
    public void testDeployment() {
        Assert.assertNotNull("Dummy not null", "");
    }

}
