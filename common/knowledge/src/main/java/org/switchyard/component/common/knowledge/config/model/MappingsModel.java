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
package org.switchyard.component.common.knowledge.config.model;

import java.util.List;

import org.switchyard.config.model.Model;

/**
 * A Mappings Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface MappingsModel extends Model {

    /** The "mappings" name. */
    public static final String MAPPINGS = "mappings";

    /** The "globals" name. */
    public static final String GLOBALS = "globals";

    /** The "inputs" name. */
    public static final String INPUTS = "inputs";

    /** The "outputs" name. */
    public static final String OUTPUTS = "outputs";

    /**
     * Gets the child mapping models.
     * @return the child mapping models
     */
    public List<MappingModel> getMappings();

    /**
     * Adds a child mapping model.
     * @param mapping the child mapping model
     * @return this MappingsModel (useful for chaining)
     */
    public MappingsModel addMapping(MappingModel mapping);

}
