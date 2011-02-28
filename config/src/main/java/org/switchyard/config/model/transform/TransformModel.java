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
package org.switchyard.config.model.transform;

import javax.xml.namespace.QName;

import org.switchyard.config.model.Model;

/**
 * The "transform" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface TransformModel extends Model {

    /** The default "transform" namespace. */
    public static final String DEFAULT_NAMESPACE = "urn:switchyard-config:transform:1.0";

    /** The "transform" name. */
    public static final String TRANSFORM = "transform";

    /** The "from" name. */
    public static final String FROM = "from";

    /** The "to" name. */
    public static final String TO = "to";

    /**
     * Gets the parent transforms model.
     * @return the parent transforms model.
     */
    public TransformsModel getTransforms();

    /**
     * Gets the from attribute.
     * @return the from attribute
     */
    public QName getFrom();

    /**
     * Sets the from attribute.
     * @param from the from attribute
     * @return this TransformModel (useful for chaining)
     */
    public TransformModel setFrom(QName from);

    /**
     * Gets the to attribute.
     * @return the to attribute
     */
    public QName getTo();

    /**
     * Sets the to attribute.
     * @param to the to attribute
     * @return this TransformModel (useful for chaining)
     */
    public TransformModel setTo(QName to);

}
