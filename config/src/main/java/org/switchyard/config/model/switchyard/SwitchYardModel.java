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

import org.switchyard.config.model.NamedModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.domain.DomainModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.config.model.validate.ValidatesModel;

/**
 * The root "switchyard" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface SwitchYardModel extends NamedModel {

    /** The default switchyard namespace. */
    public static final String DEFAULT_NAMESPACE = "urn:switchyard-config:switchyard:1.0";

    /** The "switchyard" name. */
    public static final String SWITCHYARD = "switchyard";

    /**
     * Gets the child composite model.
     * @return the child composite model
     */
    public CompositeModel getComposite();

    /**
     * Sets the child composite model.
     * @param composite the child composite model
     * @return this SwitchYardModel (useful for chaining)
     */
    public SwitchYardModel setComposite(CompositeModel composite);

    /**
     * Gets the child transforms model.
     * @return the child transforms model
     */
    public TransformsModel getTransforms();

    /**
     * Sets the child transforms model.
     * @param transforms the child transforms model.
     * @return this SwitchYardModel (useful for chaining)
     */
    public SwitchYardModel setTransforms(TransformsModel transforms);

    /**
     * Gets the child validates model.
     * @return the child validates model
     */
    public ValidatesModel getValidates();
    
    /**
     * Sets the child artifacts model.
     * @param artifacts the child artifacts model.
     * @return this SwitchYardModel (useful for chaining)
     */
    public SwitchYardModel setArtifacts(ArtifactsModel artifacts);

    /**
     * Gets the child artifacts model.
     * @return the child artifacts model
     */
    public ArtifactsModel getArtifacts();
    
    /**
     * Sets the child validates model.
     * @param validatesModel the child validates model.
     * @return this SwitchYardModel (useful for chaining)
     */
    public SwitchYardModel setValidates(ValidatesModel validatesModel);

    /**
     * Gets the child domain model.
     * @return the child domain model
     */
    public DomainModel getDomain();
    
    /**
     * Sets the child domain model.
     * @param domain the child domain model.
     * @return this SwitchyardModel (useful for chaining)
     */
    public SwitchYardModel setDomain(DomainModel domain);

    /**
     * Sets the domain property resolver based on the current state of the model.
     */
    public void setDomainPropertyResolver();

}
