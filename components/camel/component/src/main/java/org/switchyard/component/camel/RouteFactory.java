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
package org.switchyard.component.camel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.model.Constants;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.spi.NamespaceAware;
import org.switchyard.common.type.Classes;
import org.switchyard.component.camel.model.CamelComponentImplementationModel;
import org.switchyard.SwitchYardException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Creates RouteDefinition instances based off of a class containing @Route
 * methods and Java DSL route definitions.
 */
public final class RouteFactory {

    /**
     * JAXB context for reading XML definitions.
     */
    private static JAXBContext JAXB_CONTEXT;

    static {
        try {
            JAXB_CONTEXT = JAXBContext.newInstance(Constants.JAXB_CONTEXT_PACKAGES, CamelContext.class.getClassLoader());
        } catch (JAXBException e) {
            throw new SwitchYardException(e);
        }
    }

    /** 
     * Utility class - so no need to directly instantiate.
     */
    private RouteFactory() {
        
    }

    /**
     * Returns a list of route definitions referenced by a camel implementation.
     * @param model implementation config model
     * @return list of route definitions
     */
    public static List<RouteDefinition> getRoutes(CamelComponentImplementationModel model) {
        if (model.getJavaClass() != null) {
            return createRoute(model.getJavaClass(), model.getComponent().getTargetNamespace());
        }
        return loadRoute(model.getXMLPath());
    }

    /**
     * Loads a set of route definitions from an XML file.
     * @param xmlPath path to the file containing one or more route definitions
     * @return list of route definitions
     */
    public static List<RouteDefinition> loadRoute(String xmlPath) {
        List<RouteDefinition> routes = null;
        
        try {
            InputSource source =  new InputSource(Classes.getResourceAsStream(xmlPath));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(source);
            Element element = document.getDocumentElement();
            Binder<Node> binder = JAXB_CONTEXT.createBinder();
            Object obj = binder.unmarshal(element);
            injectNamespaces(element, binder);
            
            // Look for <routes> or <route> as top-level element
            if (obj instanceof RoutesDefinition) {
                routes = ((RoutesDefinition)obj).getRoutes();
            } else if (obj instanceof RouteDefinition) {
                routes = new ArrayList<RouteDefinition>(1);
                routes.add((RouteDefinition)obj);
            }
            
            // If we couldn't find a <route> or <routes> definition, throw an error
            if (routes == null) {
                CamelComponentMessages.MESSAGES.noRoutesFoundInXMLFile(xmlPath);
            }
            return routes;
        } catch (SAXException e) {
            throw new SwitchYardException(e);
        } catch (ParserConfigurationException e) {
            throw new SwitchYardException(e);
        } catch (JAXBException e) {
            throw new SwitchYardException(e);
        } catch (IOException e) {
            throw new SwitchYardException(e);
        }
    }

    private static void injectNamespaces(Element element, Binder<Node> binder) {
        NodeList list = element.getChildNodes();
        Namespaces namespaces = null;
        int size = list.getLength();
        for (int i = 0; i < size; i++) {
            Node child = list.item(i);
            if (child instanceof Element) {
                Element childElement = (Element) child;
                Object object = binder.getJAXBNode(child);
                if (object instanceof NamespaceAware) {
                    NamespaceAware namespaceAware = (NamespaceAware) object;
                    if (namespaces == null) {
                        namespaces = new Namespaces(element);
                    }
                    namespaces.configure(namespaceAware);
                }
                injectNamespaces(childElement, binder);
            }
        }
    }

    /**
     * Create a new route from the given class name and service name.
     * @param className name of the class containing an @Route definition
     * @return the route definition
     */
    public static List<RouteDefinition> createRoute(String className) {
        return createRoute(className, null);
    }

    /**
     * Create a new route from the given class name and service name.
     * @param className name of the class containing an @Route definition
     * @param namespace the namespace to append to switchyard:// service URIs
     * @return the route definition
     */
    public static List<RouteDefinition> createRoute(String className, String namespace) {
        return createRoute(Classes.forName(className), namespace);
    }

    /**
     * Create a new route from the given class and service name.
     * @param routeClass class containing an @Route definition
     * @return the route definition
     */
    public static List<RouteDefinition> createRoute(Class<?> routeClass) {
        return createRoute(routeClass, null);
    }

    /**
     * Create a new route from the given class and service name.
     * @param routeClass class containing an @Route definition
     * @param namespace the namespace to append to switchyard:// service URIs
     * @return the route definition
     */
    public static List<RouteDefinition> createRoute(Class<?> routeClass, String namespace) {
        if (!RouteBuilder.class.isAssignableFrom(routeClass)) {
            throw CamelComponentMessages.MESSAGES.javaDSLClassMustExtend(routeClass.getName(),
                    RouteBuilder.class.getName());
        }

        // Create the route and tell it to create a route
        RouteBuilder builder;
        try {
            builder = (RouteBuilder) routeClass.newInstance();
            builder.configure();
            List<RouteDefinition> routes = builder.getRouteCollection().getRoutes();
            if (routes.isEmpty()) {
                throw CamelComponentMessages.MESSAGES.noRoutesFoundinJavaDSLClass(routeClass.getName());
            }
            return routes;
        } catch (Exception ex) {
            throw CamelComponentMessages.MESSAGES.failedToInitializeDSLClass(routeClass.getName(), ex);
        }
    }

}
