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
package org.switchyard.component.camel.sap.model.v2;

import org.switchyard.component.camel.common.QueryString;
import org.switchyard.component.camel.sap.model.IDocDestinationModel;
import org.switchyard.component.camel.sap.model.IDocServerModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * V2 camel-sap IDoc destination model.
 */
public class V2IDocDestinationModel extends V2DestinationModel implements IDocDestinationModel {

    /**
     * Constructor.
     * @param config configuration
     * @param desc descriptor
     */
    public V2IDocDestinationModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * Constructor.
     * @param namespace namespace
     * @param name name
     */
    public V2IDocDestinationModel(String namespace, String name) {
        super(namespace, name);
        setModelChildrenOrder(Constants.DESTINATION_NAME, Constants.IDOC_TYPE,
                Constants.IDOC_TYPE_EXTENSION, Constants.SYSTEM_RELEASE, Constants.APPLICATION_RELEASE);
    }

    @Override
    public String getIdocType() {
        return getConfig(Constants.IDOC_TYPE);
    }

    @Override
    public IDocDestinationModel setIdocType(String idoc) {
        setConfig(Constants.IDOC_TYPE, idoc);
        return this;
    }

    @Override
    public String getIdocTypeExtension() {
        return getConfig(Constants.IDOC_TYPE_EXTENSION);
    }

    @Override
    public IDocServerModel setIdocTypeExtension(String idocExt) {
        setConfig(Constants.IDOC_TYPE_EXTENSION, idocExt);
        return null;
    }

    @Override
    public String getSystemRelease() {
        return getConfig(Constants.SYSTEM_RELEASE);
    }

    @Override
    public IDocDestinationModel setSystemRelease(String sysRelease) {
        setConfig(Constants.SYSTEM_RELEASE, sysRelease);
        return this;
    }

    @Override
    public String getApplicationRelease() {
        return getConfig(Constants.APPLICATION_RELEASE);
    }

    @Override
    public IDocDestinationModel setApplicationRelease(String appRelease) {
        setConfig(Constants.APPLICATION_RELEASE, appRelease);
        return this;
    }

    @Override
    public StringBuilder createBaseURIString(QueryString queryString) {
        StringBuilder buf = new StringBuilder(getSchema())
                                .append(':').append(getDestinationName())
                                .append(':').append(getIdocType());
        String idocTypeExt = getIdocTypeExtension();
        if (idocTypeExt != null && !idocTypeExt.isEmpty()) {
            buf.append(':').append(idocTypeExt);
            String sysRel = getSystemRelease();
            if (sysRel != null && !sysRel.isEmpty()) {
                buf.append(':').append(sysRel);
                String appRel = getApplicationRelease();
                if (appRel != null && !appRel.isEmpty()) {
                    buf.append(':').append(appRel);
                }
            }
        }
        return buf;
    }

}
