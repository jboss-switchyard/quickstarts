/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.config.model.switchyard;

import org.switchyard.config.model.Model;

/**
 * The "artifact" configuration model.
 */
public interface ArtifactModel extends Model {

    /** The "artifact" name. */
    public static final String ARTIFACT = "artifact";

    /** The "name" name. */
    public static final String NAME = "name";

    /** The "url" name. */
    public static final String URL = "url";

    /**
     * Gets the name attribute.
     * @return the name attribute
     */
    public String getName();

    /**
     * Sets the name attribute.
     * @param name the name attribute
     * @return this ArtifactModel (useful for chaining)
     */
    public ArtifactModel setName(String name);

    /**
     * Gets the url attribute.
     * @return the url attribute
     */
    public String getURL();

    /**
     * Sets the url attribute.
     * @param url the url attribute
     * @return this ArtifactModel (useful for chaining)
     */
    public ArtifactModel setURL(String url);
    
    /**
     * Gets the parent artifacts model.
     * @return the parent artifacts model.
     */
    public ArtifactsModel getArtifacts();


}
