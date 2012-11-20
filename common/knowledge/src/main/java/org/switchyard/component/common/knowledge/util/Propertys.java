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

import java.util.Properties;

import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.config.model.property.PropertiesModel;

/**
 * Property functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Propertys {

    /**
     * Gets properties.
     * @param model the model
     * @param overrides any overrides
     * @return the properties
     */
    public static Properties getProperties(KnowledgeComponentImplementationModel model, Properties overrides) {
        Properties properties = new Properties();
        // If this isn't false, then all rules' LHS object conditions will not match on redeploys!
        // (since objects are only equal if their classloaders are also equal - and they're not on redeploys)
        // NOTE: not necessary any more, per mproctor
        //properties.setProperty(ClassLoaderCacheOption.PROPERTY_NAME, Boolean.FALSE.toString());
        if (overrides != null) {
            overrideProperties(properties, overrides);
        }
        PropertiesModel propertiesModel  = model.getProperties();
        if (propertiesModel != null) {
            overrideProperties(properties, propertiesModel.toProperties());
        }
        return properties;
    }

    private static void overrideProperties(Properties target, Properties overrides) {
        for (Object key : overrides.keySet()) {
            String name = (String)key;
            String value = overrides.getProperty(name);
            target.setProperty(name, value);
        }
    }

    private Propertys() {}

}
