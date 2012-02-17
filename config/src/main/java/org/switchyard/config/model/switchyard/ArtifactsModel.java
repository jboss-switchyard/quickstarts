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

import java.util.List;

import org.switchyard.config.model.Model;
/**
 * The "artifacts" configuration model.
 */
public interface ArtifactsModel extends Model {

    /** The "artifacts" name. */
    public static final String ARTIFACTS = "artifacts";

    /**
     * Gets the parent switchyard model.
     * @return the parent switchyard model
     */
    public SwitchYardModel getSwitchYard();

    /**
     * Gets the child artifact models.
     * @return the child artifact models
     */
    public List<ArtifactModel> getArtifacts();

    /**
     * Adds a child artifact model.
     * @param artifact the child artifact model to add
     * @return this ArtifactsModel (useful for chaining)
     */
    public ArtifactsModel addArtifact(ArtifactModel artifact);
    
    /**
     * Fetch a artifact model by name.
     * @param name name of the artifact
     * @return artifact with the specified name, or null if no such artifact exists
     */
    public ArtifactModel getArtifact(String name);
    
    /**
     * Removes a child artifact model.
     * @param artifactName the name of the artifact
     * @return the removed artifact or null if the named artifact was not present
     */
    public ArtifactModel removeArtifact(String artifactName);
}
