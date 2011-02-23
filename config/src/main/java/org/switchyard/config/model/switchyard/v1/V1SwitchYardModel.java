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
package org.switchyard.config.model.switchyard.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformsModel;

/**
 * V1SwitchYardModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1SwitchYardModel extends BaseNamedModel implements SwitchYardModel {

    private CompositeModel _composite;
    private TransformsModel _transforms;

    public V1SwitchYardModel() {
        super(new QName(SwitchYardModel.DEFAULT_NAMESPACE, SwitchYardModel.SWITCHYARD));
        setModelChildrenOrder(CompositeModel.COMPOSITE, TransformsModel.TRANSFORMS);
    }

    public V1SwitchYardModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(CompositeModel.COMPOSITE, TransformsModel.TRANSFORMS);
    }

    @Override
    public CompositeModel getComposite() {
        if (_composite == null) {
            _composite = (CompositeModel)getFirstChildModelStartsWith(CompositeModel.COMPOSITE);
        }
        return _composite;
    }

    @Override
    public SwitchYardModel setComposite(CompositeModel composite) {
        setChildModel(composite);
        _composite = composite;
        return this;
    }

    @Override
    public TransformsModel getTransforms() {
        if (_transforms == null) {
            _transforms = (TransformsModel)getFirstChildModelStartsWith(TransformsModel.TRANSFORMS);
        }
        return _transforms;
    }

    @Override
    public SwitchYardModel setTransforms(TransformsModel transforms) {
        setChildModel(transforms);
        _transforms = transforms;
        return this;
    }

}
