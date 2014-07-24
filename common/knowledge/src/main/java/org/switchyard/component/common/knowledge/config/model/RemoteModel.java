/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.model;

import org.switchyard.config.model.Model;

/**
 * A RemoteModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public interface RemoteModel extends Model {

    /**
     * Gets the deploymentId attribute.
     * @return the deploymentId attribute
     */
    public String getDeploymentId();

    /**
     * Sets the deploymentId attribute.
     * @param deploymentId the deploymentId attribute
     * @return this RemoteModel (useful for chaining)
     */
    public RemoteModel setDeploymentId(String deploymentId);

    /**
     * Gets the userName attribute.
     * @return the userName attribute
     */
    public String getUserName();

    /**
     * Sets the userName attribute.
     * @param userName the userName attribute
     * @return this RemoteModel (useful for chaining)
     */
    public RemoteModel setUserName(String userName);

    /**
     * Gets the password attribute.
     * @return the password attribute
     */
    public String getPassword();

    /**
     * Sets the password attribute.
     * @param password the password attribute
     * @return this RemoteModel (useful for chaining)
     */
    public RemoteModel setPassword(String password);

    /**
     * Gets the timeout attribute.
     * @return the timeout attribute
     */
    public Integer getTimeout();

    /**
     * Sets the timeout attribute.
     * @param timeout the timeout attribute
     * @return this RemoteModel (useful for chaining)
     */
    public RemoteModel setTimeout(Integer timeout);

    /**
     * Gets the child extraJaxbClasses model.
     * @return the child extraJaxbClasses model
     */
    public ExtraJaxbClassesModel getExtraJaxbClasses();

    /**
     * Sets the child extraJaxbClasses model.
     * @param extraJaxbClasses the child extraJaxbClasses model
     * @return this RemoteModel (useful for chaining)
     */
    public RemoteModel setExtraJaxbClasses(ExtraJaxbClassesModel extraJaxbClasses);

}
