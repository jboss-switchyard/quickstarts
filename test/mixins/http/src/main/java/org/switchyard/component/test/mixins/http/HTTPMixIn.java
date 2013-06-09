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

package org.switchyard.component.test.mixins.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.AbstractTestMixIn;

/**
 * HTTP Test Mix In.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
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
        _httpClient = new DefaultHttpClient();
    }

    /**
     * Send the specified request payload to the specified HTTP endpoint using the method specified.
     * @param endpointURL The HTTP endpoint URL.
     * @param request The request payload.
     * @param method The request method.
     * @return The HTTP response code.
     */
    public int sendStringAndGetStatus(String endpointURL, String request, String method) {
        HttpResponse httpResponse = sendStringAndGetMethod(endpointURL, request, method);
        int status = httpResponse.getStatusLine().getStatusCode();
        EntityUtils.consumeQuietly(httpResponse.getEntity());
        return status;
    }

    /**
     * Send the specified request payload to the specified HTTP endpoint using the method specified.
     * @param endpointURL The HTTP endpoint URL.
     * @param request The request payload.
     * @param method The request method.
     * @return The HTTP response payload.
     */
    public String sendString(String endpointURL, String request, String method) {
        String response = null;
        try {
            HttpResponse httpResponse = sendStringAndGetMethod(endpointURL, request, method);
            response = EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException ioe) {
            _logger.error("Unable to get response", ioe);
        }
        return response;
    }

    /**
     * Send the specified request payload to the specified HTTP endpoint using the method specified.
     * @param endpointURL The HTTP endpoint URL.
     * @param request The request payload.
     * @param method The request method.
     * @return The HttpResponse object.
     */
    public HttpResponse sendStringAndGetMethod(String endpointURL, String request, String method) {
        if (_dumpMessages) {
            _logger.info("Sending a " + method + " request to [" + endpointURL + "]");
            _logger.info("Request body:[" + request + "]");
        }
        HttpRequestBase httpMethod = null;
        HttpResponse response = null;
        try {
            if (method.equals(HTTP_PUT)) {
                httpMethod = new HttpPut(endpointURL);
                ((HttpPut)httpMethod).setEntity(new StringEntity(request, _contentType, "UTF-8"));
            } else if (method.equals(HTTP_POST)) {
                httpMethod = new HttpPost(endpointURL);
                ((HttpPost)httpMethod).setEntity(new StringEntity(request, _contentType, "UTF-8"));
            } else if (method.equals(HTTP_DELETE)) {
                httpMethod = new HttpDelete(endpointURL);
            } else if (method.equals(HTTP_OPTIONS)) {
                httpMethod = new HttpOptions(endpointURL);
            } else if (method.equals(HTTP_HEAD)) {
                httpMethod = new HttpHead(endpointURL);
            } else {
                httpMethod = new HttpGet(endpointURL);
            }
            response = execute(httpMethod);
        } catch (UnsupportedEncodingException e) {
            _logger.error("Unable to set request entity", e);
        }
        return response;
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
        
        HttpPost postMethod = new HttpPost(endpointURL);
        try {
            postMethod.setEntity(new StringEntity(request, _contentType, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            _logger.error("Unable to set request entity", e);
        }
        HttpResponse httpResponse = execute(postMethod);
        try {
            return EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException ioe) {
            _logger.error("Unable to get response entity", ioe);
        }
        return null;
    }

    /**
     * POST the specified request payload to the specified HTTP endpoint.
     * @param endpointURL The HTTP endpoint URL.
     * @param request The file resource containing the request payload.
     * @return The HTTP response payload.
     */
    public String postFile(String endpointURL, String request) {

        FileEntity requestEntity =
            new FileEntity(new File(request), "text/xml; charset=utf-8");

        if (_dumpMessages) {
            _logger.info("Sending a POST request to [" + endpointURL + "]");
            ByteArrayOutputStream target = new ByteArrayOutputStream();
            try {
                requestEntity.writeTo(target);
                _logger.info("Request body:[" + target.toString() + "]");
            } catch (IOException e) {
                _logger.error("Unable to write FileEntity to stream", e);
            }
        }

        HttpPost postMethod = new HttpPost(endpointURL);
        postMethod.setEntity(requestEntity);
        HttpResponse httpResponse = execute(postMethod);
        try {
            return EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException ioe) {
            _logger.error("Unable to get response entity", ioe);
        }
        return null;
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
     * @return The HTTP method.
     */
    public HttpResponse postResourceAndGetMethod(String endpointURL, String requestResource) {
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
        
        HttpPost postMethod = new HttpPost(endpointURL);
        InputStream requestStream = getTestKit().getResourceAsStream(requestResource);
        HttpResponse response = null;
        try {
            if (_contentType != null) {
                ContentType contentType = null;
                if (_contentType.contains("charset")) {
                    contentType = ContentType.create(_contentType);
                } else {
                    contentType = ContentType.create(_contentType, "utf-8");
                }
                postMethod.setEntity(new InputStreamEntity(requestStream, requestStream.available(), contentType));
            } else {
                postMethod.setEntity(new InputStreamEntity(requestStream, requestStream.available()));
            }
            response = execute(postMethod);
        } catch (IOException ioe) {
            _logger.error("Unable to get response entity", ioe);
        } finally {
            try {
                requestStream.close();
            } catch (IOException e) {
                Assert.fail("Unexpected exception closing HTTP request resource stream.");
            }
        }
        return response;
    }

    /**
     * POST the specified classpath resource to the specified HTTP endpoint.
     * @param endpointURL The HTTP endpoint URL.
     * @param requestResource The classpath resource to be posted to the endpoint.
     * @return The HTTP response payload.
     */
    public String postResource(String endpointURL, String requestResource) {
        String response = null;
        try {
            HttpResponse httpResponse = postResourceAndGetMethod(endpointURL, requestResource);
            response = EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException ioe) {
            _logger.error("Unable to get response", ioe);
        }
        return response;
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
     * POST the specified classpath resource to the specified HTTP endpoint.
     * @param endpointURL The HTTP endpoint URL.
     * @param requestResource The classpath resource to be posted to the endpoint.
     * @return The HTTP status code.
     */
    public int postResourceAndGetStatus(String endpointURL, String requestResource) {
        HttpResponse httpResponse = postResourceAndGetMethod(endpointURL, requestResource);
        int status = httpResponse.getStatusLine().getStatusCode();
        EntityUtils.consumeQuietly(httpResponse.getEntity());
        return status;
    }

    /**
     * Execute the supplied HTTP Method.
     *
     * @param method The HTTP Method.
     * @return The HTTP Response.
     */
    public HttpResponse execute(HttpRequestBase method) {
        if (_httpClient == null) {
            Assert.fail("HTTPMixIn not initialized.  You must call the initialize() method before using this MixIn");
        }

        for (String key : _requestHeaders.keySet()) {
            method.setHeader(new BasicHeader(key, _requestHeaders.get(key)));
        }

        if (_dumpMessages) {
            for (Header header : method.getAllHeaders()) {
                _logger.info("Request header:[" + header.getName() + "=" + header.getValue() + "]");
            }
        }

        HttpResponse response = null;
        try {
            response = _httpClient.execute(method);
        } catch (Exception e) {
            Assert.fail("Exception invoking HTTP endpoint '" + method.getURI() + "': " + e.getMessage());
        }
            
        if (_dumpMessages) {
            for (Header header : response.getAllHeaders()) {
                _logger.info("Received response header:[" + header.getName() + "=" + header.getValue() + "]");
            }
            _logger.info("Received response body:[" + response + "]");
        }

        for (String key : _expectedHeaders.keySet()) {
            Header actual = response.getFirstHeader(key);
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
    public HTTPMixIn setRequestHeaders(Map<String,String> headers) {
        _requestHeaders.clear();
        _requestHeaders.putAll(headers);
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
    public HTTPMixIn setExpectedHeaders(Map<String,String> headers) {
        _expectedHeaders.clear();
        _expectedHeaders.putAll(headers);
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
            _httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.SECONDS);
            _httpClient.getConnectionManager().shutdown();
        }
    }
}
