/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.config.model.v1;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.camel.model.Constants;
import org.apache.camel.model.RouteDefinition;
import org.switchyard.component.camel.SwitchYardRouteDefinition;
import org.switchyard.component.camel.config.model.CamelComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.v1.V1ComponentImplementationModel;
import org.switchyard.exception.SwitchYardException;

/**
 * Version 1 implementation.
 * 
 * @author Daniel Bevenius
 *
 */
public class V1CamelImplementationModel extends V1ComponentImplementationModel implements CamelComponentImplementationModel {
    
    // The class attribute for Java DSL routes
    private static final String CLASS = "class";

    private static final QName ROUTE_ELEMENT = 
            new QName("http://camel.apache.org/schema/spring", "route");
    
    private static JAXBContext jaxbContext = createJAXBInstance();
    
    /**
     * Create a new CamelImplementationModel.
     */
    public V1CamelImplementationModel() {
        super(CAMEL, DEFAULT_NAMESPACE);
    }
    
    /**
     * Create a CamelImplementationModel from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    public V1CamelImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder();
    }

    @Override
    public RouteDefinition getRoute() {
        final Configuration routeConfig = getModelConfiguration().getFirstChild(ROUTE);
        if (routeConfig == null) {
            return null;
        }
        RouteDefinition route;
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            route = (RouteDefinition) unmarshaller.unmarshal(routeConfig.getSource());
        } catch (JAXBException e) {
            throw new SwitchYardException(e);
        }
        String namespace = getComponent().getTargetNamespace();
        if (route != null && namespace != null) {
            SwitchYardRouteDefinition.addNamespaceParameter(route, namespace);
        }
        return route;
    }
    
    /**
     * Add an empty Camel route definition to the implementation.
     * @return the JAXB object model for the empty Camel route.
     */
    public RouteDefinition addRoute() {
        if (getModelConfiguration().getFirstChild(ROUTE) != null) {
            throw new IllegalStateException(ROUTE + " element already exists in implementation!");
        }
        
        Model routeModel = new BaseModel(ROUTE_ELEMENT) {};
        addChildModel(routeModel);
        return getRoute();
    }
    
    private static JAXBContext createJAXBInstance() {
        try {
            return JAXBContext.newInstance(Constants.JAXB_CONTEXT_PACKAGES);
        } catch (JAXBException e) {
            throw new SwitchYardException(e);
        }
    }

    @Override
    public String getJavaClass() {
        Configuration classConfig = getModelConfiguration().getFirstChild(JAVA);
        return classConfig != null ? classConfig.getAttribute(CLASS) : null;
    }

    @Override
    public V1CamelImplementationModel setJavaClass(String className) {
        Configuration classConfig = getModelConfiguration().getFirstChild(JAVA);
        if (classConfig == null) {
            NameValueModel model = new NameValueModel(JAVA);
            model.getModelConfiguration().setAttribute(CLASS, className);
            setChildModel(model);
        } else {
            classConfig.setAttribute(CLASS, className);
        }
        return this;
    }

}
