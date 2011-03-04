/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2010-2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License, v.2.1 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA.
 */

package org.switchyard.quickstarts.m1app;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.test.SwitchYardCDITestCase;
import org.switchyard.test.SwitchYardDeploymentConfig;

@SwitchYardDeploymentConfig(SwitchYardDeploymentConfig.SWITCHYARD_XML)
public class WebServiceTest extends SwitchYardCDITestCase {

    // ------------------------------------------------------------------------------------
    //
    //  NOTE: This test cannot be run from inside your IDE because the switchyard.xml that
    //        it relies upon is only generated at built time.
    //
    //        See https://issues.jboss.org/browse/SWITCHYARD-163
    //
    // ------------------------------------------------------------------------------------

    private InputStream _requestStream;
    private Reader _expectedResponseReader;

    @Before
    public void setUp() throws Exception {
        _requestStream = getClass().getClassLoader().getResourceAsStream("xml/soap-request.xml");
        InputStream responseStream = 
            getClass().getClassLoader().getResourceAsStream("xml/soap-response.xml");
        _expectedResponseReader = new InputStreamReader(responseStream);
    }
    
    @After
    public void tearDown() throws Exception {
        if (_expectedResponseReader != null) {
            _expectedResponseReader.close();
        }
        if (_requestStream != null) {
            _requestStream.close();
        }
    }
    
    @Test
    public void invokeOrderWebService() throws Exception {

        HttpClient client = new HttpClient();
        PostMethod postMethod = new PostMethod("http://localhost:18001/OrderService");
        String output;
        
        postMethod.setRequestEntity(new InputStreamRequestEntity(_requestStream, "text/xml; charset=utf-8"));
        client.executeMethod(postMethod);
        output = postMethod.getResponseBodyAsString();
        
        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(_expectedResponseReader, new StringReader(output));
    }
}
