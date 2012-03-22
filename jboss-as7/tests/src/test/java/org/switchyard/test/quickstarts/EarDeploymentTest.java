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
