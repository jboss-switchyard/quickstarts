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

package org.switchyard.component.resteasy.config.model;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.composite.BindingModel;

/**
 * A RESTEasy binding model.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public interface RESTEasyBindingModel extends BindingModel {

    /**
     *  Prefix for RESTEasy Gateway Configuration.
     */
    public static final String RESTEASY = "rest";

    /**
     * Default namespace for RESTEasy Gateway Configuration.
     */
    public static final String DEFAULT_NAMESPACE = "urn:switchyard-component-resteasy:config:1.0";

    /**
     * Returns the RESTEasy Service name.
     * 
     * @return the serviceName
     */
    public QName getServiceName();

    /**
     * Sets the RESTEasy Service name.
     * 
     * @param serviceName the serviceName to set
     */
    public void setServiceName(QName serviceName);

    /**
     * Gets the REST endpoint address invoked by the Reference binding.
     * 
     * @return the endpoint address
     */
    public String getAddress();

    /**
     * Sets the REST endpoint address invoked by the Reference binding.
     * 
     * @param address the endpoint address
     * @return RESTEasyBindingModel
     */
    public RESTEasyBindingModel setAddress(String address);

    /**
     * Gets the REST interfaces exposed by this Service binding.
     * 
     * @return the comma seperated list of RESTEasy interfaces
     */
    public String getInterfaces();

    /**
     * Gets the REST interfaces exposed by this Service binding.
     * 
     * @return the list of RESTEasy interfaces as an array
     */
    public String[] getInterfacesAsArray();

    /**
     * Sets the REST interfaces exposed by this Service binding.
     * 
     * @param interfaces a comma separated list of JAVA class names
     * @return RESTEasyBindingModel
     */
    public RESTEasyBindingModel setInterfaces(String interfaces);

    /**
     * Gets the context path of the Resource interfaces.
     * 
     * This is applicable only if getPublish is true.
     * 
     * @return the contextPath
     */
    public String getContextPath();

    /**
     * Sets the context path of the Resource interfaces.
     * 
     * This is applicable only if getPublish() is true.
     * 
     * @param contextPath the contextPath to set
     * @return RESTEasyBindingModel
     */
    public RESTEasyBindingModel setContextPath(String contextPath);

    /**
     * Gets the proxy config.
     * @return the proxy config
     */
    public ProxyModel getProxyConfig();

    /**
     * Sets the proxy config.
     * @param proxyConfig the proxy config
     * @return this RESTEasyBindingModel
     */
    public RESTEasyBindingModel setProxyConfig(ProxyModel proxyConfig);

    /**
     * Gets the global configuration.
     * 
     * @return the environment/global config
     */
    public Configuration getEnvironment();

    /**
     * Sets the global configuration.
     * 
     * @param config the environment/global config
     * @return RESTEasyBindingModel
     */
    public RESTEasyBindingModel setEnvironment(Configuration config);
}
