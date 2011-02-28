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
package org.switchyard.config.model;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.util.QNames;

/**
 * An abstract representation of a NamedModel, useful for subclassing.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public abstract class BaseNamedModel extends BaseModel implements NamedModel {

    protected BaseNamedModel(QName qname) {
        super(qname);
    }

    protected BaseNamedModel(Configuration config) {
        super(config);
    }

    protected BaseNamedModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getModelAttribute("name");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NamedModel setName(String name) {
        setModelAttribute("name", name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getQName() {
        return QNames.create(getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NamedModel setQName(QName qname) {
        setName(qname != null ? qname.toString() : null);
        return this;
    }

}
