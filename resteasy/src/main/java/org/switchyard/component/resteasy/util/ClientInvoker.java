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

package org.switchyard.component.resteasy.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Providers;

import org.jboss.logging.Logger;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.ClientInterceptorRepositoryImpl;
import org.jboss.resteasy.client.core.ClientInvokerInterceptorFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.client.core.extractors.ClientErrorHandler;
import org.jboss.resteasy.client.core.extractors.ClientRequestContext;
import org.jboss.resteasy.client.core.extractors.DefaultEntityExtractorFactory;
import org.jboss.resteasy.client.core.extractors.EntityExtractor;
import org.jboss.resteasy.client.core.extractors.EntityExtractorFactory;
import org.jboss.resteasy.client.core.marshallers.ClientMarshallerFactory;
import org.jboss.resteasy.client.core.marshallers.Marshaller;
import org.jboss.resteasy.client.exception.mapper.ApacheHttpClient4ExceptionMapper;
import org.jboss.resteasy.client.exception.mapper.ClientExceptionMapper;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.MediaTypeHelper;
import org.jboss.resteasy.util.IsHttpMethod;
import org.jboss.resteasy.util.Types;
import org.switchyard.component.resteasy.RestEasyLogger;
import org.switchyard.component.resteasy.RestEasyMessages;
import org.switchyard.component.resteasy.composer.RESTEasyBindingData;
import org.switchyard.component.resteasy.config.model.ProxyModel;
import org.switchyard.component.resteasy.config.model.RESTEasyBindingModel;

/**
 * Client Invoker for RESTEasy gateway. Code lifted from RESTEasy.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class ClientInvoker extends ClientInterceptorRepositoryImpl implements MethodInvoker {

    private static final Logger LOGGER = Logger.getLogger(ClientInvoker.class);

    private static final String AS7_URIBUILDER = "org.jboss.resteasy.specimpl.UriBuilderImpl";
    private static final String WFLY_URIBUILDER = "org.jboss.resteasy.specimpl.ResteasyUriBuilder";
    private static Class<?> URIBUILDER_CLASS = null;

    private String _subResourcePath;
    private Class<?> _resourceClass;
    private Method _method;
    private String _httpMethod;
    private UriBuilder _uri;
    private MediaType _accepts;
    private Marshaller[] _marshallers;
    private ClientExecutor _executor;
    private boolean _followRedirects;
    private EntityExtractor _extractor;
    private EntityExtractorFactory _extractorFactory;
    private ResteasyProviderFactory _providerFactory;
    private URI _baseUri;
    private Map<String, Object> _attributes = new HashMap<String, Object>();

    static {
        try {
            URIBUILDER_CLASS = Class.forName(AS7_URIBUILDER);
        } catch (ClassNotFoundException cnfe) {
            try {
                URIBUILDER_CLASS = Class.forName(WFLY_URIBUILDER);
            } catch (ClassNotFoundException e) {
                RestEasyLogger.ROOT_LOGGER.unableToFindURIBuilder(e);
            }
        }
    }

    private static URI createUri(String base) {
        try {
            return new URI(base);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a RESTEasy invoker client.
     *
     * @param basePath The base path for the class
     * @param resourceClass The JAX-RS Resource Class
     * @param method The JAX-RS Resource Class's method
     */
    public ClientInvoker(String basePath, Class<?> resourceClass, Method method) {

        this(basePath, resourceClass, method, null);
    }

    /**
     * Create a RESTEasy invoker client.
     *
     * @param basePath The base path for the class
     * @param resourceClass The JAX-RS Resource Class
     * @param method The JAX-RS Resource Class's method
     * @param model Configuration model
     */
    public ClientInvoker(String basePath, Class<?> resourceClass, Method method, RESTEasyBindingModel model) {
        Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
        _baseUri = createUri(basePath);
        if ((httpMethods == null || httpMethods.size() == 0) && method.isAnnotationPresent(Path.class) && method.getReturnType().isInterface()) {
            _subResourcePath = createSubResourcePath(basePath, method);
        } else if (httpMethods == null || httpMethods.size() != 1) {
            throw RestEasyMessages.MESSAGES.youMustUseAtLeastOneButNoMoreThanOneHttpMethodAnnotationOn(method.toString());
        }
        _httpMethod = httpMethods.iterator().next();
        _resourceClass = resourceClass;
        _method = method;
        try {
            _uri = (UriBuilder)URIBUILDER_CLASS.newInstance();
        } catch (InstantiationException ie) {
            throw new RuntimeException(ie);
        } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
        }
        _uri.uri(_baseUri);
        if (_resourceClass.isAnnotationPresent(Path.class)) {
            _uri.path(_resourceClass);
        }
        if (_method.isAnnotationPresent(Path.class)) {
            _uri.path(_method);
        }

        _providerFactory = new ResteasyProviderFactory();

        boolean useBuiltins = true; // use builtin @Provider classes by default
        if (model.getContextParamsConfig() != null) {
            Map<String, String> contextParams = model.getContextParamsConfig().toMap();

            // Set use builtin @Provider classes
            String registerBuiltins = contextParams.get(ResteasyContextParameters.RESTEASY_USE_BUILTIN_PROVIDERS);
            if (registerBuiltins != null) {
                useBuiltins = Boolean.parseBoolean(registerBuiltins);
            }

            // Register @Provider classes
            List<Class<?>> providerClasses = RESTEasyProviderUtil.getProviderClasses(contextParams);
            if (providerClasses != null) {
                for (Class<?> pc : providerClasses) {
                    _providerFactory.registerProvider(pc);
                }
            }
        }
        if (useBuiltins) {
            _providerFactory.setRegisterBuiltins(true);
            RegisterBuiltin.register(_providerFactory);
        }

        _extractorFactory = new DefaultEntityExtractorFactory();
        _extractor = _extractorFactory.createExtractor(_method);
        _marshallers = ClientMarshallerFactory.createMarshallers(_resourceClass, _method, _providerFactory, null);
        _accepts = MediaTypeHelper.getProduces(_resourceClass, method, null);
        ClientInvokerInterceptorFactory.applyDefaultInterceptors(this, _providerFactory, _resourceClass, _method);

        // Client executor
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        int port = _baseUri.getPort();
        if (_baseUri.getScheme().startsWith("https")) {
            if (port == -1) {
                port = 443;
            }
            schemeRegistry.register(new Scheme(_baseUri.getScheme(), port, SSLSocketFactory.getSocketFactory()));
        } else {
            if (port == -1) {
                port = 80;
            }
            schemeRegistry.register(new Scheme(_baseUri.getScheme(), port, PlainSocketFactory.getSocketFactory()));
        }
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
        HttpClient httpClient = new DefaultHttpClient(cm);
        _executor = new ApacheHttpClient4Executor(httpClient);
        // register ApacheHttpClient4ExceptionMapper manually for local instance of ResteasyProviderFactory
        Type exceptionType = Types.getActualTypeArgumentsOfAnInterface(ApacheHttpClient4ExceptionMapper.class, ClientExceptionMapper.class)[0];
        _providerFactory.addClientExceptionMapper(new ApacheHttpClient4ExceptionMapper(), exceptionType);

        // Authentication settings
        if (model.hasAuthentication()) {
            // Set authentication
            AuthScope authScope = null;
            Credentials credentials = null;
            if (model.isBasicAuth()) {
                authScope = createAuthScope(model.getBasicAuthConfig().getHost(), model.getBasicAuthConfig().getPort(), model.getBasicAuthConfig().getRealm());
                credentials = new UsernamePasswordCredentials(model.getBasicAuthConfig().getUser(), model.getBasicAuthConfig().getPassword());
                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                authCache.put(new HttpHost(authScope.getHost(), authScope.getPort()), new BasicScheme(ChallengeState.TARGET));
                BasicHttpContext context = new BasicHttpContext();
                context.setAttribute(ClientContext.AUTH_CACHE, authCache);
                ((ApacheHttpClient4Executor)_executor).setHttpContext(context);
            } else {
                authScope = createAuthScope(model.getNtlmAuthConfig().getHost(), model.getNtlmAuthConfig().getPort(), model.getNtlmAuthConfig().getRealm());
                credentials = new NTCredentials(model.getNtlmAuthConfig().getUser(),
                                    model.getNtlmAuthConfig().getPassword(),
                                    "",
                                    model.getNtlmAuthConfig().getDomain());
            }
            ((DefaultHttpClient)httpClient).getCredentialsProvider().setCredentials(authScope, credentials);
        } else {
            ProxyModel proxy = model.getProxyConfig();
            if (proxy != null) {
                HttpHost proxyHost = null;
                if (proxy.getPort() != null) {
                    proxyHost = new HttpHost(proxy.getHost(), Integer.valueOf(proxy.getPort()).intValue());
                } else {
                    proxyHost = new HttpHost(proxy.getHost(), -1);
                }
                if (proxy.getUser() != null) {
                    AuthScope authScope = new AuthScope(proxy.getHost(), Integer.valueOf(proxy.getPort()).intValue(), AuthScope.ANY_REALM);
                    Credentials credentials = new UsernamePasswordCredentials(proxy.getUser(), proxy.getPassword());
                    AuthCache authCache = new BasicAuthCache();
                    authCache.put(proxyHost, new BasicScheme(ChallengeState.PROXY));
                    ((DefaultHttpClient)httpClient).getCredentialsProvider().setCredentials(authScope, credentials);
                    BasicHttpContext context = new BasicHttpContext();
                    context.setAttribute(ClientContext.AUTH_CACHE, authCache);
                    ((ApacheHttpClient4Executor)_executor).setHttpContext(context);
                }
                httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHost);
            }
        }
        Integer timeout = model.getTimeout();
        if (timeout != null) {
            HttpParams httpParams = httpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
            HttpConnectionParams.setSoTimeout(httpParams, timeout);
        }
    }

    /**
     * Gets a ResteasyProviderFactory instance.
     * @return ResteasyProviderFactory
     */
    public ResteasyProviderFactory getResteasyProviderFactory() {
        return _providerFactory;
    }

    private AuthScope createAuthScope(String host, String portStr, String realm) throws RuntimeException {
        if (realm == null) {
            realm = AuthScope.ANY_REALM;
        }
        int port = -1;
        if (portStr != null) {
            port = Integer.valueOf(portStr).intValue();
        }
        return new AuthScope(host, port, realm);
    }

    private static String createSubResourcePath(String base, Method method) {
        if (!base.endsWith("/")) {
            base = base + "/";
        }
        String path = method.getAnnotation(Path.class).value();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        Annotation[][] params = method.getParameterAnnotations();
        int index = 1;
        for (Annotation[] param : params) {
            for (Annotation a : param) {
                if (a instanceof PathParam) {
                    String name = ((PathParam) a).value();
                    path = path.replace("{" + name + "}", "%" + index + "$s");
                    break;
                }
            }
            index++;
        }
        return base + path;
    }

    /*public Map<String, Object> getAttributes() {
        return _attributes;
    }

    public MediaType getAccepts() {
        return _accepts;
    }

    public Method getMethod() {
        return _method;
    }

    public Class getDeclaring() {
        return _resourceClass;
    }

    public ResteasyProviderFactory getProviderFactory() {
        return _providerFactory;
    }*/

    @Override
    public RESTEasyBindingData invoke(Object[] args, MultivaluedMap<String, String> headers) {
        boolean isProvidersSet = ResteasyProviderFactory.getContextData(Providers.class) != null;
        if (!isProvidersSet) {
            ResteasyProviderFactory.pushContext(Providers.class, _providerFactory);
        }

        try {
            if (_uri == null) {
                throw RestEasyMessages.MESSAGES.youHaveNotSetABaseURIForTheClientProxy();
            }

            ClientRequest request = null;

            BaseClientResponse clientResponse = null;
            try {
                request = createRequest(args, headers);
                clientResponse = (BaseClientResponse) request.httpMethod(_httpMethod);
            } catch (ClientResponseFailure crf) {
                clientResponse = (BaseClientResponse) crf.getResponse();
            } catch (Exception e) {
                ClientExceptionMapper<Exception> mapper = _providerFactory.getClientExceptionMapper(Exception.class);
                if (mapper != null) {
                   throw mapper.toException(e);
                } else {
                   throw new RuntimeException(e);
                }
            }
            ClientErrorHandler errorHandler = new ClientErrorHandler(_providerFactory.getClientErrorInterceptors());
            clientResponse.setAttributeExceptionsTo(_method.toString());
            clientResponse.setAnnotations(_method.getAnnotations());
            ClientRequestContext clientRequestContext = new ClientRequestContext(request, clientResponse, errorHandler, _extractorFactory, _baseUri);
            Object response = _extractor.extractEntity(clientRequestContext);
            RESTEasyBindingData restResponse = new RESTEasyBindingData();
            if (response != null) {
                restResponse.setParameters(new Object[]{response});
            }
            // Propagate outgoing headers
            restResponse.setHeaders(clientResponse.getHeaders());
            restResponse.setStatusCode(clientResponse.getStatus());
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Incoming Headers to SwitchYard through OutboundHandler [");
                RESTEasyProxy.traceLog(LOGGER, clientResponse.getHeaders());
                LOGGER.trace("]");
            }
            return restResponse;
        } finally {
            if (!isProvidersSet) {
                ResteasyProviderFactory.popContextData(Providers.class);
            }
        }
    }

    private ClientRequest createRequest(Object[] args, MultivaluedMap<String, String> headers) {
        UriBuilder uriBuilder = _uri;
        if (_subResourcePath != null) {
            uriBuilder = UriBuilder.fromUri(String.format(_subResourcePath, args));
        }
        ClientRequest request = new ClientRequest(uriBuilder, _executor, _providerFactory);
        if (_accepts != null) {
            request.header(HttpHeaders.ACCEPT, _accepts.toString());
        }
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String name = entry.getKey();
            List<String> values = entry.getValue();
            for (String value : values) {
                request.header(name, value);
            }
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Outgoing Headers from SwitchYard through OutboundHandler [");
            RESTEasyProxy.traceLog(LOGGER, headers);
            LOGGER.trace("]");
        }
        this.copyClientInterceptorsTo(request);

        boolean isClientResponseResult = ClientResponse.class.isAssignableFrom(_method.getReturnType());
        request.followRedirects(!isClientResponseResult);

        for (int i = 0; i < _marshallers.length; i++) {
            _marshallers[i].build(request, args[i]);
        }
        return request;
    }

    /*public String getHttpMethod() {
        return _httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        _httpMethod = httpMethod;
    }

    public boolean isFollowRedirects() {
        return _followRedirects;
    }

    public void setFollowRedirects(boolean followRedirects) {
        _followRedirects = followRedirects;
    }

    public void followRedirects() {
        setFollowRedirects(true);
    }*/
}
