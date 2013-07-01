/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.component.resteasy.config.model;

import org.switchyard.config.model.Model;

/**
 * A RESTEasy value model.
 * 
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public interface RESTEasyNameValueModel extends Model {

    /** Known XML element names. */
    public enum RESTEasyName {
        /** Known XML element names. */
        interfaces, address, contextPath, proxy, host, port, user, password;
    }

    /**
     * Gets the name.
     * @return the name
     */
    public RESTEasyName getName();

    /**
     * Gets the value.
     * @return the value
     */
    public String getValue();

    /**
     * Sets the value.
     * @param value the value
     * @return this RESTEasyValueModel (useful for chaining)
     */
    public RESTEasyNameValueModel setValue(String value);

}
