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

package org.switchyard.test.mixins;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * HTTP Test Mix In.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class HTTPMixIn extends AbstractTestMixIn {

    private HttpClient _httpClient;
    private String _contentType = "text/xml";

    /**
     * Set the content type.
     * <p/>
     * Default content type is "text/xml".
     *
     * @param contentType The content type.
     * @return This HTTPMixIn instance.
     */
    public HTTPMixIn setContentType(String contentType) {
        this._contentType = contentType;
        return this;
    }

    @Override
    public void setUp() {
        _httpClient = new HttpClient();
    }

    /**
     * POST the specified request payload to the specified HTTP endpoint.
     * @param endpointURL The HTTP endpoint URL.
     * @param request The request payload.
     * @return The HTTP response payload.
     */
    public String postString(String endpointURL, String request) {
        PostMethod postMethod = new PostMethod(endpointURL);
        try {
            postMethod.setRequestEntity(new StringRequestEntity(request, _contentType, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            return execute(postMethod);
        } finally {
            postMethod.releaseConnection();
        }
    }

    /**
     * POST the specified String request to the specified HTTP endpoint and perform an XML compare
     * between the HTTP response and the specified expected response String.
     * @param endpointURL The HTTP endpoint URL.
     * @param request The classpath resource to be posted to the endpoint.
     * @param expectedResponse The String to use to perform the XML test on the response.
     * @return The HTTP response payload.
     */
    public String postStringAndTestXML(String endpointURL, String request, String expectedResponse) {
        String response = postString(endpointURL, request);
        getTestCase().compareXMLToString(response, expectedResponse);
        return response;
    }

    /**
     * POST the specified classpath resource to the specified HTTP endpoint.
     * @param endpointURL The HTTP endpoint URL.
     * @param requestResource The classpath resource to be posted to the endpoint.
     * @return The HTTP response payload.
     */
    public String postResource(String endpointURL, String requestResource) {
        PostMethod postMethod = new PostMethod(endpointURL);
        InputStream requestStream = getTestCase().getResourceAsStream(requestResource);

        try {
            postMethod.setRequestEntity(new InputStreamRequestEntity(requestStream, _contentType + "; charset=utf-8"));
            return execute(postMethod);
        } finally {
            try {
                requestStream.close();
            } catch (IOException e) {
                Assert.fail("Unexpected exception closing HTTP request resource stream.");
            } finally {
                postMethod.releaseConnection();
            }
        }
    }

    /**
     * POST the specified classpath resource to the specified HTTP endpoint and perform an XML compare
     * between the HTTP response and the specified expected classpath response resource.
     * @param endpointURL The HTTP endpoint URL.
     * @param requestResource The classpath resource to be posted to the endpoint.
     * @param expectedResponseResource The classpath resource to use to perform the XML test on the response.
     * @return The HTTP response payload.
     */
    public String postResourceAndTestXML(String endpointURL, String requestResource, String expectedResponseResource) {
        String response = postResource(endpointURL, requestResource);
        getTestCase().compareXMLToResource(response, expectedResponseResource);
        return response;
    }

    /**
     * Execute the supplied HTTP Method.
     * <p/>
     * Does not release the {@link org.apache.commons.httpclient.HttpMethod#releaseConnection() HttpMethod connection}.
     *
     * @param method The HTTP Method.
     * @return The HTTP Response.
     */
    public String execute(HttpMethod method) {
        try {
            _httpClient.executeMethod(method);
            return method.getResponseBodyAsString();
        } catch (Exception e) {
            try {
                Assert.fail("Exception invoking HTTP endpoint '" + method.getURI() + "': " + e.getMessage());
            } catch (URIException e1) {
                e1.printStackTrace();
            }
        }

        return null;
    }
}
