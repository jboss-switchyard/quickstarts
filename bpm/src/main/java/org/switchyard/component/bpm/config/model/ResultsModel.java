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
package org.switchyard.component.bpm.config.model;

import java.util.List;

import org.switchyard.component.common.rules.config.model.MappingModel;
import org.switchyard.config.model.Model;

/**
 * ResultsModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public interface ResultsModel extends Model {

    /**
     * The resultss XML element.
     */
    public static final String RESULTS = "results";

    /**
     * Gets the child mapping models.
     * @return the child mapping models
     */
    public List<MappingModel> getMappings();

    /**
     * Adds a child mapping model.
     * @param mapping the child mapping model
     * @return this ResultsModel (useful for chaining)
     */
    public ResultsModel addMapping(MappingModel mapping);

}
