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
package org.switchyard.component.common.knowledge.config.model.v2;

import static org.switchyard.component.common.knowledge.config.model.ExtraJaxbClassesModel.EXTRA_JAXB_CLASSES;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.ExtraJaxbClassesModel;
import org.switchyard.component.common.knowledge.config.model.RemoteModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 2nd version RemoteModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public abstract class V2RemoteModel extends BaseModel implements RemoteModel {

    private ExtraJaxbClassesModel _extraJaxbClasses = null;

    /**
     * Constructs a new V2RemoteModel of the specified namespace and name.
     * @param namespace the namespace
     * @param name the name
     */
    public V2RemoteModel(String namespace, String name) {
        super(XMLHelper.createQName(namespace, name));
    }

    /**
     * Constructs a new V2RemoteJmsModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V2RemoteModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeploymentId() {
        return getModelAttribute("deploymentId");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteModel setDeploymentId(String deploymentId) {
        setModelAttribute("deploymentId", deploymentId);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserName() {
        return getModelAttribute("userName");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteModel setUserName(String userName) {
        setModelAttribute("userName", userName);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() {
        return getModelAttribute("password");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteModel setPassword(String password) {
        setModelAttribute("password", password);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getTimeout() {
        String timeout = getModelAttribute("timeout");
        return timeout != null ? Integer.valueOf(timeout) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteModel setTimeout(Integer timeout) {
        String t = timeout != null ? timeout.toString() : null;
        setModelAttribute("timeout", t);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtraJaxbClassesModel getExtraJaxbClasses() {
        if (_extraJaxbClasses == null) {
            _extraJaxbClasses = (ExtraJaxbClassesModel)getFirstChildModel(EXTRA_JAXB_CLASSES);
        }
        return _extraJaxbClasses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteModel setExtraJaxbClasses(ExtraJaxbClassesModel extraJaxbClasses) {
        setChildModel(extraJaxbClasses);
        _extraJaxbClasses = extraJaxbClasses;
        return this;
    }

}
