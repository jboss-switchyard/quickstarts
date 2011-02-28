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

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseTypedModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.TransformsModel;
import org.switchyard.config.util.QNames;

/**
 * An abstract representation of a TransformModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class V1BaseTransformModel extends BaseTypedModel implements TransformModel {

    protected V1BaseTransformModel(String type) {
        this(new QName(SwitchYardModel.DEFAULT_NAMESPACE, TransformModel.TRANSFORM + '.' + type));
    }

    protected V1BaseTransformModel(QName qname) {
        super(qname);
    }

    protected V1BaseTransformModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransformsModel getTransforms() {
        return (TransformsModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getFrom() {
        return QNames.create(getModelAttribute(TransformModel.FROM));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransformModel setFrom(QName from) {
        setModelAttribute(TransformModel.FROM, from != null ? from.toString() : null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getTo() {
        return QNames.create(getModelAttribute(TransformModel.TO));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransformModel setTo(QName to) {
        setModelAttribute(TransformModel.TO, to != null ? to.toString() : null);
        return this;
    }

}
