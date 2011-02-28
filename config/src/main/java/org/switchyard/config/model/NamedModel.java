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

/**
 * A Model with a name.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface NamedModel extends Model {

    /**
     * Gets the name <b>attribute</b> of this Model (<i>not</i> the name of the wrapped Configuration).
     * @return the name
     */
    public String getName();

    /**
     * Sets the name <b>attribute</b> of this Model (<i>not</i> the name of the wrapped Configuration).
     * @param name the name
     * @return this NamedModel (useful for chaining)
     */
    public NamedModel setName(String name);

    /**
     * Gets the qualified name <b>attribute</b> of this Model (<i>not</i> the qualified name of the wrapped Configuration).
     * @return the qualified name
     */
    public QName getQName();

    /**
     * Sets the qualified name <b>attribute</b> of this Model (<i>not</i> the qualified name of the wrapped Configuration).
     * @param qname the qualified name
     * @return this NamedModel (useful for chaining)
     */
    public NamedModel setQName(QName qname);

}
