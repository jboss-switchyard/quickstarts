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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;

public class WebServiceTest {
    
    @Test
    public void invokeOrderWebService() {

        HttpClient client = new HttpClient();
        PostMethod postMethod = new PostMethod("http://localhost:18001/OrderService");
        InputStream requestStream = null;
        String output;
        
        try{
            requestStream = getClass().getClassLoader().getResourceAsStream("xml/soap-request.xml");
            postMethod.setRequestEntity(new InputStreamRequestEntity(requestStream, "text/xml; charset=utf-8"));
            client.executeMethod(postMethod);
            output = postMethod.getResponseBodyAsString();
        } catch (Exception ex) {
           output = "Exception: " + ex.toString();
        } finally {
            try { 
                if (requestStream != null) {
                    requestStream.close();
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        }
        
        System.out.println("Response from OrderService: " + output);
    }
}
