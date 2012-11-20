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
package org.switchyard.component.common.knowledge.util;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;
import org.drools.xml.ChangeSetSemanticModule;
import org.drools.xml.SemanticModules;
import org.drools.xml.XmlChangeSetReader;
import org.kie.ChangeSet;
import org.switchyard.common.io.pull.ElementPuller;
import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.exception.SwitchYardException;
import org.xml.sax.SAXException;

/**
 * ChangeSet functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class ChangeSets {

    private static final Logger LOGGER = Logger.getLogger(ChangeSets.class);

    /**
     * Creates a new ChangeSet given the specified list of resources.
     * @param resources the resources
     * @return the ChangeSet
     */
    public static ChangeSet newChangeSet(List<? extends Resource> resources) {
        return newChangeSet(resources, null);
    }

    /**
     * Creates a new ChangeSet given the specified list of resources.
     * @param resources the resources
     * @param loader the ClassLoader to use
     * @return the ChangeSet
     */
    public static ChangeSet newChangeSet(List<? extends Resource> resources, ClassLoader loader) {
        StringBuilder xml = new StringBuilder();
        xml.append("<change-set xmlns=\"http://drools.org/drools-5.0/change-set\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema-instance\" xs:schemaLocation=\"http://drools.org/drools-5.0/change-set https://raw.github.com/droolsjbpm/droolsjbpm-knowledge/master/knowledge-api/src/main/resources/change-set-1.0.0.xsd\">");
        xml.append("<add>");
        if (resources != null) {
            for (Resource resource : resources) {
                // TODO: decision table configuration requires a bit more...
                if (resource != null) {
                    URL url = resource.getLocationURL(loader);
                    org.kie.io.ResourceType type = Resources.convertResourceType(resource.getType());
                    if (url != null && type != null) {
                        xml.append(String.format("<resource source=\"%s\" type=\"%s\"/>", url, type.getName()));
                    }
                }
            }
        }
        xml.append("</add>");
        xml.append("</change-set>");
        return newChangeSet(xml.toString(), loader);
    }

    /**
     * Creates a new ChangeSet given the specified XML.
     * @param xml the XML
     * @return the ChangeSet
     */
    public static ChangeSet newChangeSet(String xml) {
        return newChangeSet(xml, null);
    }

    /**
     * Creates a new ChangeSet given the specified XML.
     * @param xml the XML
     * @param loader the ClassLoader to use
     * @return the ChangeSet
     */
    public static ChangeSet newChangeSet(String xml, ClassLoader loader) {
        if (LOGGER.isDebugEnabled()) {
            String xcs;
            try {
                // try to make the xml "pretty"
                xcs = XMLHelper.toString(new ElementPuller().pull(new StringReader(xml)));
            } catch (IOException ioe) {
                xcs = xml;
            }
            LOGGER.debug("newChangeSet:\n" + xcs);
        }
        SemanticModules semanticModules = new SemanticModules();
        semanticModules.addSemanticModule(new ChangeSetSemanticModule());
        XmlChangeSetReader reader = new XmlChangeSetReader(semanticModules);
        reader.setClassLoader(loader, null);
        try {
            return reader.read(new StringReader(xml));
        } catch (SAXException saxe) {
            throw new SwitchYardException(saxe);
        } catch (IOException ioe) {
            throw new SwitchYardException(ioe);
        }
    }

    private ChangeSets() {}

}
