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
package org.switchyard.deploy.osgi.internal.resteasy;

/**
 * Defines a list of RESTEasy providers to be registered.
 * This is a workaround for https://issues.jboss.org/browse/RESTEASY-640
 */
public class RESTEasyProviders {

    /** A list of RESTEasy providers to be registered. */
    public static Class<?>[] PROVIDERS = {
        // from resteasy-jaxrs
        org.jboss.resteasy.plugins.providers.DataSourceProvider.class,
        org.jboss.resteasy.plugins.providers.DocumentProvider.class,
        org.jboss.resteasy.plugins.providers.DefaultTextPlain.class,
        org.jboss.resteasy.plugins.providers.StringTextStar.class,
        org.jboss.resteasy.plugins.providers.InputStreamProvider.class,
        org.jboss.resteasy.plugins.providers.ByteArrayProvider.class,
        org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider.class,
        org.jboss.resteasy.plugins.providers.FileProvider.class,
        org.jboss.resteasy.plugins.providers.StreamingOutputProvider.class,
        org.jboss.resteasy.plugins.providers.IIOImageProvider.class,
        org.jboss.resteasy.plugins.interceptors.CacheControlInterceptor.class,
        org.jboss.resteasy.plugins.interceptors.encoding.AcceptEncodingGZIPInterceptor.class,
        org.jboss.resteasy.plugins.interceptors.encoding.ClientContentEncodingHeaderInterceptor.class,
        org.jboss.resteasy.plugins.interceptors.encoding.GZIPDecodingInterceptor.class,
        org.jboss.resteasy.plugins.interceptors.encoding.GZIPEncodingInterceptor.class,
        org.jboss.resteasy.plugins.interceptors.encoding.ServerContentEncodingHeaderInterceptor.class,
        // from resteasy-jaxb-provider
        org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlSeeAlsoProvider.class,
        org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlRootElementProvider.class,
        org.jboss.resteasy.plugins.providers.jaxb.JAXBElementProvider.class,
        org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlTypeProvider.class,
        org.jboss.resteasy.plugins.providers.jaxb.CollectionProvider.class,
        org.jboss.resteasy.plugins.providers.jaxb.MapProvider.class,
        org.jboss.resteasy.plugins.providers.jaxb.XmlJAXBContextFinder.class,
    };
}
