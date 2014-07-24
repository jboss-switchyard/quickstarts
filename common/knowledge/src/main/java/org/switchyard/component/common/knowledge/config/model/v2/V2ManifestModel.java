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

import static org.switchyard.component.common.knowledge.config.model.RemoteJmsModel.REMOTE_JMS;
import static org.switchyard.component.common.knowledge.config.model.RemoteRestModel.REMOTE_REST;

import javax.xml.namespace.QName;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.ManifestModel;
import org.switchyard.component.common.knowledge.config.model.RemoteModel;
import org.switchyard.component.common.knowledge.config.model.v1.V1ManifestModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * The 2nd version ManifestModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class V2ManifestModel extends V1ManifestModel {

    private QName _jmsRemoteQName;
    private QName _restRemoteQName;
    private RemoteModel _remote;

    /**
     * Constructs a new V2ManifestModel of the specified namespace.
     * @param namespace the namespace
     */
    public V2ManifestModel(String namespace) {
        super(namespace);
        init();
    }

    /**
     * Constructs a new V2ManifestModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V2ManifestModel(Configuration config, Descriptor desc) {
        super(config, desc);
        init();
    }

    private void init() {
        setModelChildrenOrder(REMOTE_JMS, REMOTE_REST); // this appends to CONTAINER, RESOURCES
        QName qname = getModelConfiguration().getQName();
        _jmsRemoteQName = XMLHelper.createQName(qname.getNamespaceURI(), REMOTE_JMS, qname.getPrefix());
        _restRemoteQName = XMLHelper.createQName(qname.getNamespaceURI(), REMOTE_REST, qname.getPrefix());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RemoteModel getRemote() {
        if (_remote == null) {
            _remote = (RemoteModel)getFirstChildModel(REMOTE_JMS);
            if (_remote == null) {
                _remote = (RemoteModel)getFirstChildModel(REMOTE_REST);
            }
        }
        return _remote;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ManifestModel setRemote(RemoteModel remote) {
        Configuration config = getModelConfiguration();
        config.removeChildren(_jmsRemoteQName);
        config.removeChildren(_restRemoteQName);
        setChildModel(remote);
        _remote = remote;
        return this;
    }

}
