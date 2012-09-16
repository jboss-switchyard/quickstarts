/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.HTTPMixIn;

@RunWith(Arquillian.class)
public class HttpBindingQuickstartTest {

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-quickstart-http-binding");
    }

    @Test
    public void quoteService() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();

        httpMixIn.initialize();
        try {
            String response = httpMixIn.sendString(BASE_URL + "/quote", "vineyard", HTTPMixIn.HTTP_POST);
            Assert.assertEquals("136.5", response);
        } finally {
            httpMixIn.uninitialize();
        }
    }

    @Test
    public void headers() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();
        httpMixIn.initialize();
        try {
            String response = httpMixIn.sendString(BASE_URL + "/symbol", "headers", HTTPMixIn.HTTP_POST);
            Assert.assertEquals("content-type=text/xml;charset=UTF-8", response);
        } finally {
            httpMixIn.uninitialize();
        }
    }

    @Test
    public void requestInfo() throws Exception {
        HTTPMixIn httpMixIn = new HTTPMixIn();
        httpMixIn.initialize();
        try {
            String response = httpMixIn.sendString(BASE_URL + "/symbol", "requestInfo", HTTPMixIn.HTTP_POST);
            Assert.assertTrue(response.indexOf("HttpRequestInfo [authType=null, characterEncoding=UTF-8, contentType=text/xml;charset=UTF-8, contextPath=/http-binding/symbol") == 0);
        } finally {
            httpMixIn.uninitialize();
        }
    }
    private static final String BASE_URL = "http://localhost:8080/http-binding";
}
