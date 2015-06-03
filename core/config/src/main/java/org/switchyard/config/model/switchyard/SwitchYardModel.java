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
