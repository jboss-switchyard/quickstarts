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

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.switchyard.test.SwitchYardTestKit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * HTTP Test Mix In.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class HTTPMixIn extends AbstractTestMixIn {
    private static Logger _logger = Logger.getLogger(HTTPMixIn.class);

    /**
     * Constant representing HTTP DELETE method.
     */
    public static final String HTTP_DELETE = "DELETE";

    /**
     * Constant representing HTTP GET method.
     */
    public static final String HTTP_GET = "GET";

    /**
     * Constant representing HTTP HEAD method.
     */
    public static final String HTTP_HEAD = "HEAD";

    /**
     * Constant representing HTTP POST method.
     */
    public static final String HTTP_POST = "POST";

    /**
     * Constant representing HTTP PUT method.
     */
    public static final String HTTP_PUT = "PUT";

    /**
     * Constant representing HTTP OPTIONS method.
     */
    public static final String HTTP_OPTIONS = "OPTIONS";

    private HttpClient _httpClient;
    private String _contentType = "text/xml";
    private HashMap<String,String> _requestHeaders = new HashMap<String,String>();
    private HashMap<String,String> _expectedHeaders = new HashMap<String,String>();
    private boolean _dumpMessages = false;
    
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
    public void initialize() {
        _httpClient = new HttpClient();
    }

    /**
     * Send the specified request payload to the specified HTTP endpoint using the method specified.
     * @param endpointURL The HTTP endpoint URL.
     * @param request The request payload.
     * @param method The request method.
     * @return The HTTP response payload.
     */
    public String sendString(String endpointURL, String request, String method) {
        if (_dumpMessages) {
            _logger.info("Sending a " + method + " request to [" + endpointURL + "]");
            _logger.info("Request body:[" + request + "]");
        }
        try {
            if (method.equals(HTTP_PUT)) {
                PutMethod httpMethod = new PutMethod(endpointURL);
                httpMethod.setRequestEntity(new StringRequestEntity(request, _contentType, "UTF-8"));
                return execute(httpMethod);
            } else if (method.equals(HTTP_POST)) {
                PostMethod httpMethod = new PostMethod(endpointURL);
                httpMethod.setRequestEntity(new StringRequestEntity(request, _contentType, "UTF-8"));
                return execute(httpMethod);
            } else if (method.equals(HTTP_DELETE)) {
                DeleteMethod httpMethod = new DeleteMethod(endpointURL);
                return execute(httpMethod);
            } else if (method.equals(HTTP_OPTIONS)) {
                OptionsMethod httpMethod = new OptionsMethod(endpointURL);
                return execute(httpMethod);
            } else if (method.equals(HTTP_HEAD)) {
                HeadMethod httpMethod = new HeadMethod(endpointURL);
                return execute(httpMethod);
            } else {
                GetMethod httpMethod = new GetMethod(endpointURL);
                return execute(httpMethod);
            }
        } catch (UnsupportedEncodingException e) {
            _logger.error("Unable to set request entity", e);
        }
        return null;
    }

    /**
     * POST the specified request payload to the specified HTTP endpoint.
     * @param endpointURL The HTTP endpoint URL.
     * @param request The request payload.
     * @return The HTTP response payload.
     */
    public String postString(String endpointURL, String request) {
        if (_dumpMessages) {
            _logger.info("Sending a POST request to [" + endpointURL + "]");
            _logger.info("Request body:[" + request + "]");
        }
        
        PostMethod postMethod = new PostMethod(endpointURL);
        try {
            postMethod.setRequestEntity(new StringRequestEntity(request, _contentType, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            _logger.error("Unable to set request entity", e);
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
        SwitchYardTestKit.compareXMLToString(response, expectedResponse);
        return response;
    }

    /**
     * POST the specified classpath resource to the specified HTTP endpoint.
     * @param endpointURL The HTTP endpoint URL.
     * @param requestResource The classpath resource to be posted to the endpoint.
     * @return The HTTP response payload.
     */
    public String postResource(String endpointURL, String requestResource) {
        if (_dumpMessages) {
            _logger.info("Sending a POST request to [" + endpointURL + "]");
            InputStream input = getTestKit().getResourceAsStream(requestResource);
            ByteArrayOutputStream tmp = new ByteArrayOutputStream();
            byte[] data = new byte[4096];
            int l = 0;
            try {
                while ((l = input.read(data)) >= 0) {
                    tmp.write(data, 0, l);
                }
                _logger.info("Request body:[" + new String(tmp.toByteArray()) + "]");
            } catch (IOException e) {
                _logger.error("Unexpected Exception while reading request resource", e);
            }
        }
        
        PostMethod postMethod = new PostMethod(endpointURL);
        InputStream requestStream = getTestKit().getResourceAsStream(requestResource);

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
        getTestKit().compareXMLToResource(response, expectedResponseResource);
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
        if (_httpClient == null) {
            Assert.fail("HTTPMixIn not initialized.  You must call the initialize() method before using this MixIn");
        }

        for (String key : _requestHeaders.keySet()) {
            method.setRequestHeader(key, _requestHeaders.get(key));
        }

        if (_dumpMessages) {
            for (Header header : method.getRequestHeaders()) {
                _logger.info("Request header:[" + header.getName() + "=" + header.getValue() + "]");
            }
        }

        String response = null;
        try {
            _httpClient.executeMethod(method);
            response = method.getResponseBodyAsString();
        } catch (Exception e) {
            try {
                Assert.fail("Exception invoking HTTP endpoint '" + method.getURI() + "': " + e.getMessage());
            } catch (URIException e1) {
                _logger.error("Unexpected error", e1);
                return null;
            }
        }
            
        if (_dumpMessages) {
            for (Header header : method.getResponseHeaders()) {
                _logger.info("Received response header:[" + header.getName() + "=" + header.getValue() + "]");
            }
            _logger.info("Received response body:[" + response + "]");
        }

        for (String key : _expectedHeaders.keySet()) {
            Header actual = method.getResponseHeader(key);
            Assert.assertNotNull("Checking response header:[" + key + "]", actual);
            Assert.assertEquals("Checking response header:[" + key + "]", _expectedHeaders.get(key), actual.getValue());
        }
            
        return response;
    }

    /**
     * Set a request header.
     * @param name header name
     * @param value header value
     * @return this instance
     */
    public HTTPMixIn setRequestHeader(String name, String value) {
        _requestHeaders.put(name, value);
        return this;
    }
    
    /**
     * Set the list of request headers.
     * @param headers request headers in HashMap
     * @return this instance
     */
    public HTTPMixIn setRequestHeaders(HashMap<String,String> headers) {
        _requestHeaders = headers;
        return this;
    }
    
    /**
     * Set a expected response header.
     * @param name header name
     * @param value header value
     * @return this instance
     */
    public HTTPMixIn setExpectedHeader(String name, String value) {
        _expectedHeaders.put(name, value);
        return this;
    }
    
    /**
     * Set the list of expected response headers.
     * @param headers expected response headers in HashMap
     * @return this instance
     */
    public HTTPMixIn setExpectedHeaders(HashMap<String,String> headers) {
        _expectedHeaders = headers;
        return this;
    }
    
    /**
     * Whether to dump the request/response message into log or not.
     * @param dumpMessages Whether to dump the request/response message into log or not
     * @return this instance
     */
    public HTTPMixIn setDumpMessages(boolean dumpMessages) {
        _dumpMessages = dumpMessages;
        return this;
    }
    
    @Override
    public void uninitialize() {
        if (_httpClient != null) {
            final HttpConnectionManager connectionManager = _httpClient.getHttpConnectionManager();
            if (connectionManager instanceof MultiThreadedHttpConnectionManager) {
                final MultiThreadedHttpConnectionManager multiThreadedHttpConnectionManager = (MultiThreadedHttpConnectionManager)connectionManager;
                multiThreadedHttpConnectionManager.shutdown();
            }
            connectionManager.closeIdleConnections(0);
        }
    }
}
