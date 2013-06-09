/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
 
package org.switchyard.component.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.http.composer.HttpComposition;
import org.switchyard.component.http.composer.HttpBindingData;
import org.switchyard.component.http.composer.HttpRequestBindingData;
import org.switchyard.component.http.composer.HttpResponseBindingData;
import org.switchyard.component.http.config.model.HttpBindingModel;
import org.switchyard.deploy.BaseServiceHandler;

/**
 * Handles invoking external HTTP services.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class OutboundHandler extends BaseServiceHandler {

    private static final Logger LOGGER = Logger.getLogger(OutboundHandler.class);

    private static final String HTTP_GET = "GET";
    private static final String HTTP_POST = "POST";
    private static final String HTTP_DELETE = "DELETE";
    private static final String HTTP_HEAD = "HEAD";
    private static final String HTTP_PUT = "PUT";
    private static final String HTTP_OPTIONS = "OPTIONS";

    private final HttpBindingModel _config;

    private MessageComposer<HttpBindingData> _messageComposer;
    private String _baseAddress = "http://localhost:8080";
    private String _httpMethod = HTTP_GET;
    private String _contentType;
    private AuthScope _authScope;
    private AuthCache _authCache;
    private Credentials _credentials;

    /**
     * Constructor.
     * @param config the configuration settings
     */
    public OutboundHandler(final HttpBindingModel config) {
        _config = config;
    }

    /**
     * Start lifecycle.
     *
     * @throws HttpConsumeException If unable to load or access a HTTP uri
     */
    public void start() throws HttpConsumeException {
        String address = _config.getAddress();
        if (address != null) {
            _baseAddress = address;
        }
        String method = _config.getMethod();
        if (method != null) {
            _httpMethod = method;
        }
        String contentType = _config.getContentType();
        if (contentType != null) {
            _contentType = contentType;
        }
        // Create and configure the HTTP message composer
        _messageComposer = HttpComposition.getMessageComposer(_config);

        if (_config.hasAuthentication()) {
            // Set authentication
            if (_config.isBasicAuth()) {
                _authScope = createAuthScope(_config.getBasicAuthConfig().getHost(), _config.getBasicAuthConfig().getPort(), _config.getBasicAuthConfig().getRealm());
                _credentials = new UsernamePasswordCredentials(_config.getBasicAuthConfig().getUser(), _config.getBasicAuthConfig().getPassword());
                // Create AuthCache instance
                _authCache = new BasicAuthCache();
                _authCache.put(new HttpHost(_authScope.getHost(), _authScope.getPort()), new BasicScheme());
            } else {
                _authScope = createAuthScope(_config.getNtlmAuthConfig().getHost(), _config.getNtlmAuthConfig().getPort(), _config.getNtlmAuthConfig().getRealm());
                _credentials = new NTCredentials(_config.getNtlmAuthConfig().getUser(),
                                    _config.getNtlmAuthConfig().getPassword(),
                                    "",
                                    _config.getNtlmAuthConfig().getDomain());
            }
        }
    }

    private AuthScope createAuthScope(String host, String portStr, String realm) throws HttpConsumeException {
        URL url = null;
        try {
            url = new URL(_baseAddress);
        } catch (MalformedURLException mue) {
            throw new HttpConsumeException(mue);
        }
        if (realm == null) {
            realm = AuthScope.ANY_REALM;
        }
        int port = url.getPort();
        if (host == null) {
            host = url.getHost();
        }
        if (portStr != null) {
            port = Integer.valueOf(portStr).intValue();
        }
        return new AuthScope(host, port, realm);
    }

    /**
     * Stop lifecycle.
     */
    public void stop() {
    }

    /**
     * The handler method that invokes the actual HTTP service when the
     * component is used as a HTTP consumer.
     * @param exchange the Exchange
     * @throws HandlerException handler exception
     */
    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            if (_credentials != null) {
                httpclient.getCredentialsProvider().setCredentials(_authScope, _credentials);
                List<String> authpref = new ArrayList<String>();
                authpref.add(AuthPolicy.NTLM);
                authpref.add(AuthPolicy.BASIC);
                httpclient.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authpref);
            }
            HttpBindingData httpRequest = _messageComposer.decompose(exchange, new HttpRequestBindingData());
            HttpRequestBase request = null;
            if (_httpMethod.equals(HTTP_GET)) {
                request = new HttpGet(_baseAddress);
            } else if (_httpMethod.equals(HTTP_POST)) {
                request = new HttpPost(_baseAddress);
                ((HttpPost) request).setEntity(new InputStreamEntity(httpRequest.getBodyBytes(), httpRequest.getBodyBytes().available()));
            } else if (_httpMethod.equals(HTTP_DELETE)) {
                request = new HttpDelete(_baseAddress);
            } else if (_httpMethod.equals(HTTP_HEAD)) {
                request = new HttpHead(_baseAddress);
            } else if (_httpMethod.equals(HTTP_PUT)) {
                request = new HttpPut(_baseAddress);
                ((HttpPut) request).setEntity(new InputStreamEntity(httpRequest.getBodyBytes(), httpRequest.getBodyBytes().available()));
            } else if (_httpMethod.equals(HTTP_OPTIONS)) {
                request = new HttpOptions(_baseAddress);
            }
            Iterator<Map.Entry<String, List<String>>> entries = httpRequest.getHeaders().entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, List<String>> entry = entries.next();
                String name = entry.getKey();
                List<String> values = entry.getValue();
                for (String value : values) {
                    request.addHeader(name, value);
                }
            }
            if (_contentType != null) {
                request.addHeader("Content-Type", _contentType);
            }

            HttpResponse response = null;
            if ((_credentials != null) && (_credentials instanceof NTCredentials)) {
                // Send a request for the Negotiation
                response = httpclient.execute(new HttpGet(_baseAddress));
                HttpClientUtils.closeQuietly(response);
            }
            if (_authCache != null) {
                BasicHttpContext context = new BasicHttpContext();
                context.setAttribute(ClientContext.AUTH_CACHE, _authCache);
                response = httpclient.execute(request, context);
            } else {
                response = httpclient.execute(request);
            }
            int status = response.getStatusLine().getStatusCode();

            HttpEntity entity = response.getEntity();
            HttpResponseBindingData httpResponse = new HttpResponseBindingData();
            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                httpResponse.addHeader(header.getName(), header.getValue());
            }
            if (entity != null) {
                if (entity.getContentType() != null) {
                    httpResponse.setContentType(new ContentType(entity.getContentType().getValue()));
                } else {
                    httpResponse.setContentType(new ContentType());
                }
                httpResponse.setBodyFromStream(entity.getContent());
            }
            httpResponse.setStatus(status);
            Message out = _messageComposer.compose(httpResponse, exchange);
            if (httpResponse.getStatus() < 400) {
                exchange.send(out);
            } else {
                exchange.sendFault(out);
            }
        } catch (Exception e) {
            throw new HandlerException("Unexpected exception handling HTTP Message", e);
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }
}
