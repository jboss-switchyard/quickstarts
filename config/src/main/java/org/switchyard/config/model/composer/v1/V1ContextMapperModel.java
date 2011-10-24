/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.config.model.composer.v1;

import javax.xml.namespace.QName;

import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.Classes;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * A version 1 ContextMapperModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1ContextMapperModel extends BaseModel implements ContextMapperModel {

    /**
     * Constructs a new V1ContextMapperModel.
     */
    public V1ContextMapperModel() {
        super(new QName(SwitchYardModel.DEFAULT_NAMESPACE, CONTEXT_MAPPER));
    }

    /**
     * Constructs a new V1ContextMapperModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ContextMapperModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getClazz() {
        String name = Strings.trimToNull(getModelAttribute("class"));
        if (name != null) {
            return Classes.forName(name, getClass());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIncludes() {
        return Strings.trimToNull(getModelAttribute("includes"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExcludes() {
        return Strings.trimToNull(getModelAttribute("excludes"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIncludeNamespaces() {
        return Strings.trimToNull(getModelAttribute("includeNamespaces"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExcludeNamespaces() {
        return Strings.trimToNull(getModelAttribute("excludeNamespaces"));
    }

}
