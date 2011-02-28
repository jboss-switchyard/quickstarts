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
package org.switchyard.config.model.transform.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.TransformsModel;

/**
 * A version 1 TransformsModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1TransformsModel extends BaseModel implements TransformsModel {

    private List<TransformModel> _transforms = new ArrayList<TransformModel>();

    /**
     * Constructs a new V1TransformsModel.
     */
    public V1TransformsModel() {
        super(new QName(SwitchYardModel.DEFAULT_NAMESPACE, TransformsModel.TRANSFORMS));
        setModelChildrenOrder(TransformModel.TRANSFORM);
    }

    /**
     * Constructs a new V1TransformsModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1TransformsModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration transform_config : config.getChildrenStartsWith(TransformModel.TRANSFORM)) {
            TransformModel transform = (TransformModel)readModel(transform_config);
            if (transform != null) {
                _transforms.add(transform);
            }
        }
        setModelChildrenOrder(TransformModel.TRANSFORM);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SwitchYardModel getSwitchYard() {
        return (SwitchYardModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<TransformModel> getTransforms() {
        return Collections.unmodifiableList(_transforms);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized TransformsModel addTransform(TransformModel transform) {
        addChildModel(transform);
        _transforms.add(transform);
        return this;
    }

}
