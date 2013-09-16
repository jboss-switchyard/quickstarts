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
 
package org.switchyard.component.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.ChallengeState;
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
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Scope;
import org.switchyard.ServiceDomain;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.http.composer.HttpComposition;
import org.switchyard.component.http.composer.HttpBindingData;
import org.switchyard.component.http.composer.HttpRequestBindingData;
import org.switchyard.component.http.composer.HttpResponseBindingData;
import org.switchyard.component.http.config.model.HttpBindingModel;
import org.switchyard.deploy.BaseServiceHandler;
import org.switchyard.label.BehaviorLabel;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

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
    private final String _bindingName;
    private final String _referenceName;
    private MessageComposer<HttpBindingData> _messageComposer;
    private String _baseAddress = "http://localhost:8080";
    private String _httpMethod = HTTP_GET;
    private String _contentType;
    private AuthScope _authScope;
    private AuthCache _authCache;
    private Credentials _credentials;
    private HttpHost _proxyHost;
    private Integer _timeout;

    /**
     * Constructor.
     * @param config the configuration settings
     * @param domain the service domain
     */
    public OutboundHandler(final HttpBindingModel config, final ServiceDomain domain) {
        super(domain);
        _config = config;
        _bindingName = config.getName();
        _referenceName = config.getReference().getName();
    }

    /**
     * Start lifecycle.
     *
     * @throws HttpConsumeException If unable to load or access a HTTP uri
     */
    @Override
    protected void doStart() throws HttpConsumeException {
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
                _authCache.put(new HttpHost(_authScope.getHost(), _authScope.getPort()), new BasicScheme(ChallengeState.TARGET));
            } else {
                _authScope = createAuthScope(_config.getNtlmAuthConfig().getHost(), _config.getNtlmAuthConfig().getPort(), _config.getNtlmAuthConfig().getRealm());
                _credentials = new NTCredentials(_config.getNtlmAuthConfig().getUser(),
                                    _config.getNtlmAuthConfig().getPassword(),
                                    "",
                                    _config.getNtlmAuthConfig().getDomain());
            }
        }
        if (_config.getProxyConfig() != null) {
            if (_config.getProxyConfig().getPort() != null) {
                _proxyHost = new HttpHost(_config.getProxyConfig().getHost(), Integer.valueOf(_config.getProxyConfig().getPort()).intValue());
            } else {
                _proxyHost = new HttpHost(_config.getProxyConfig().getHost(), -1);
            }
            if (_config.getProxyConfig().getUser() != null) {
                _authScope = createAuthScope(_config.getProxyConfig().getHost(), _config.getProxyConfig().getPort(), null);
                _credentials = new UsernamePasswordCredentials(_config.getProxyConfig().getUser(), _config.getProxyConfig().getPassword());
                if (_authCache == null) {
                    _authCache = new BasicAuthCache();
                }
                _authCache.put(_proxyHost, new BasicScheme(ChallengeState.PROXY));
            }
        }
        _timeout = _config.getTimeout();
    }

    private AuthScope createAuthScope(String host, String portStr, String realm) throws HttpConsumeException {
        URL url = null;
        try {
            url = new URL(_baseAddress);
        } catch (MalformedURLException mue) {
            throw HttpMessages.MESSAGES.invalidHttpURL(mue);
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
     * The handler method that invokes the actual HTTP service when the
     * component is used as a HTTP consumer.
     * @param exchange the Exchange
     * @throws HandlerException handler exception
     */
    @Override
    public void handleMessage(final Exchange exchange) throws HandlerException {
        // identify ourselves
        exchange.getContext().setProperty(ExchangeCompletionEvent.GATEWAY_NAME, _bindingName, Scope.EXCHANGE)
                .addLabels(BehaviorLabel.TRANSIENT.label());
        if (getState() != State.STARTED) {
            throw HttpMessages.MESSAGES.bindingNotStarted(_referenceName, _bindingName);
        }

        DefaultHttpClient httpclient = new DefaultHttpClient();
        if (_timeout != null) {
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, _timeout);
            HttpConnectionParams.setSoTimeout(httpParams, _timeout);
        }
        try {
            if (_credentials != null) {
                httpclient.getCredentialsProvider().setCredentials(_authScope, _credentials);
                List<String> authpref = new ArrayList<String>();
                authpref.add(AuthPolicy.NTLM);
                authpref.add(AuthPolicy.BASIC);
                httpclient.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authpref);
            }
            if (_proxyHost != null) {
                httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, _proxyHost);
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
            throw HttpMessages.MESSAGES.unexpectedExceptionHandlingHTTPMessage(e);
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }
}
