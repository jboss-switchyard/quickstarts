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
package org.switchyard.config.model.resource.v1;

import static org.switchyard.config.model.resource.ResourceDetailModel.RESOURCE_DETAIL;
import static org.switchyard.config.model.switchyard.SwitchYardModel.DEFAULT_NAMESPACE;

import java.net.URL;

import javax.xml.namespace.QName;

import org.switchyard.common.io.resource.BaseResource;
import org.switchyard.common.io.resource.ResourceDetail;
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.resource.ResourceDetailModel;
import org.switchyard.config.model.resource.ResourceModel;

/**
 * The 1st version ResourceModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2011 Red Hat Inc.
 */
public class V1ResourceModel extends BaseModel implements ResourceModel {

    private ResourceDetailModel _detail;

    /**
     * Creates a new ResourceModel in the default namespace.
     */
    public V1ResourceModel() {
        this(DEFAULT_NAMESPACE);
    }

    /**
     * Creates a new ResourceModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1ResourceModel(String namespace) {
        super(new QName(namespace, RESOURCE));
        setModelChildrenOrder(RESOURCE_DETAIL);
    }

    /**
     * Creates a new ResourceModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1ResourceModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(RESOURCE_DETAIL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocation() {
        return getModelAttribute("location");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getLocationURL() {
        return getLocationURL(getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getLocationURL(Class<?> caller) {
        return BaseResource.getURL(getLocation(), caller);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getLocationURL(ClassLoader loader) {
        return BaseResource.getURL(getLocation(), loader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceModel setLocation(String location) {
        if (location != null && getType() == null) {
            setType(ResourceType.forLocation(location));
        }
        setModelAttribute("location", location);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceType getType() {
        String rt = getModelAttribute("type");
        return rt != null ? ResourceType.valueOf(rt) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceModel setType(ResourceType type) {
        String rt = type != null ? type.getName() : null;
        setModelAttribute("type", rt);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceDetail getDetail() {
        if (_detail == null) {
            _detail = (ResourceDetailModel)getFirstChildModel(RESOURCE_DETAIL);
        }
        return _detail;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceModel setDetail(ResourceDetail detail) {
        ResourceDetailModel child = null;
        if (detail instanceof ResourceDetailModel) {
            child = (ResourceDetailModel)detail;
        } else if (detail != null) {
            child = new V1ResourceDetailModel(getNamespaceURI());
            child.setInputType(detail.getInputType());
            child.setWorksheetName(detail.getWorksheetName());
            child.setUsingExternalTypes(detail.isUsingExternalTypes());
        }
        setChildModel(child);
        _detail = child;
        return this;
    }

}
