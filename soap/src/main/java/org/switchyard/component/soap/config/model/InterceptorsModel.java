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
package org.switchyard.component.soap.config.model;

import java.util.List;

import org.switchyard.config.model.Model;

/**
 * A Interceptors Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public interface InterceptorsModel extends Model {

    /** The "inInterceptors" name. */
    public static final String IN_INTERCEPTORS = "inInterceptors";

    /** The "outInterceptors" name. */
    public static final String OUT_INTERCEPTORS = "outInterceptors";

    /**
     * Gets the child interceptor models.
     * @return the child interceptor models
     */
    public List<InterceptorModel> getInterceptors();

    /**
     * Adds a child interceptor model.
     * @param interceptor the child interceptor model
     * @return this ChannelsModel (useful for chaining)
     */
    public InterceptorsModel addInterceptor(InterceptorModel interceptor);

}
