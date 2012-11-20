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

import org.switchyard.config.model.Model;
import org.switchyard.config.model.resource.ResourcesModel;

/**
 * A Manifest Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface ManifestModel extends Model {

    /** The "manifest" name. */
    public static final String MANIFEST = "manifest";

    /**
     * Gets the scan attribute.
     * @return the scan attribute
     */
    public boolean isScan();

    /**
     * Sets the scan attribute.
     * @param scan the scan attribute
     * @return this ManifestModel (useful for chaining)
     */
    public ManifestModel setScan(boolean scan);

    /**
     * Gets the child container model.
     * @return the child container model
     */
    public ContainerModel getContainer();

    /**
     * Sets the child container model.
     * @param container the child container model
     * @return this ManifestModel (useful for chaining)
     */
    public ManifestModel setContainer(ContainerModel container);

    /**
     * Gets the child resources model.
     * @return the child resources model
     */
    public ResourcesModel getResources();

    /**
     * Sets the child resources model.
     * @param resources the child resources model
     * @return this ManifestModel (useful for chaining)
     */
    public ManifestModel setResources(ResourcesModel resources);

}
