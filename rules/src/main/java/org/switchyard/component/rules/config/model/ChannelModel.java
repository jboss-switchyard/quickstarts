/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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
package org.switchyard.component.rules.config.model;

import javax.xml.namespace.QName;

import org.switchyard.config.model.NamedModel;

/**
 * A Channel Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface ChannelModel extends NamedModel {

    /**
     * The channel XML element.
     */
    public static final String CHANNEL = "channel";

    /**
     * Gets the Channel class.
     * @param loader the ClassLoader to use
     * @return the Channel class
     */
    public Class<?> getClazz(ClassLoader loader);

    /**
     * Sets the Channel class.
     * @param clazz the Channel class
     * @return this ChannelModel (useful for chaining)
     */
    public ChannelModel setClazz(Class<?> clazz);

    /**
     * Gets the input attribute.
     * @return the input attribute
     */
    public QName getInput();

    /**
     * Sets the input attribute.
     * @param input the input attribute
     * @return this ChannelModel (useful for chaining)
     */
    public ChannelModel setInput(QName input);

    /**
     * Gets the operation attribute.
     * @return the operation attribute
     */
    public String getOperation();

    /**
     * Sets the operation attribute.
     * @param operation the operation attribute
     * @return this ChannelModel (useful for chaining)
     */
    public ChannelModel setOperation(String operation);

    /**
     * Gets the reference attribute.
     * @return the reference attribute
     */
    public String getReference();

    /**
     * Sets the reference attribute.
     * @param reference the reference attribute
     * @return this ChannelModel (useful for chaining)
     */
    public ChannelModel setReference(String reference);

}
