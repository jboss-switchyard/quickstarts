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

/**
 * A RemoteRestModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public interface RemoteRestModel extends RemoteModel {

    /** The "remoteRest" name. */
    public static final String REMOTE_REST = "remoteRest";

    /**
     * Gets the url attribute.
     * @return the url attribute
     */
    public String getUrl();

    /**
     * Sets the url attribute.
     * @param url the url attribute
     * @return this RemoteRestModel (useful for chaining)
     */
    public RemoteRestModel setUrl(String url);

    /**
     * Gets the useFormBasedAuth attribute.
     * @return the useFormBasedAuth attribute
     * @deprecated deprecated as of kie/drools/jbpm 6.2
     */
    @Deprecated
    public boolean isUseFormBasedAuth();

    /**
     * Sets the useFormBasedAuth attribute.
     * @param useFormBasedAuth the useFormBasedAuth attribute
     * @return this RemoteRestModel (useful for chaining)
     * @deprecated deprecated as of kie/drools/jbpm 6.2
     */
    @Deprecated
    public RemoteRestModel setUseFormBasedAuth(boolean useFormBasedAuth);

}
