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
package org.switchyard.component.camel.sap.model;

/**
 * camel-sap IDoc server model.
 */
public interface IDocServerModel extends ServerModel {
    /**
     * Gets IDoc type.
     * @return IDoc type.
     */
    public String getIdocType();

    /**
     * Sets IDoc type.
     * @param idoc IDoc type
     * @return this model (useful for chaining)
     */
    public IDocServerModel setIdocType(String idoc);

    /**
     * Gets IDoc type extension.
     * @return IDoc type extension
     */
    public String getIdocTypeExtension();

    /**
     * Sets IDoc type extension.
     * @param idocExt IDoc type extension
     * @return this model (useful for chaiming)
     */
    public IDocServerModel setIdocTypeExtension(String idocExt);

    /**
     * Gets system release.
     * @return system release
     */
    public String getSystemRelease();

    /**
     * Sets system release.
     * @param sysRelease system release
     * @return this model (useful for chaining)
     */
    public IDocServerModel setSystemRelease(String sysRelease);

    /**
     * Gets application release.
     * @return application release
     */
    public String getApplicationRelease();

    /**
     * Sets application release.
     * @param appRelease application release
     * @return this model (useful for chaining)
     */
    public IDocServerModel setApplicationRelease(String appRelease);
}
