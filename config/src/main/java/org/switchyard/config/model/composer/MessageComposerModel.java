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

package org.switchyard.config.model.composer;

import org.switchyard.config.model.Model;

/**
 * The "messageComposer" configuration model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface MessageComposerModel extends Model {

    /** messageComposer variable. */
    public static final String MESSAGE_COMPOSER = "messageComposer";

    /**
     * Gets the type of ContextMapper.
     * @return the type of ContextMapper
     */
    public Class<?> getClazz();

    /**
     * Sets the type of ContextMapper.
     * @param clazz type of ContextMapper
     * @return this instance (useful for chaining)
     */
    public MessageComposerModel setClazz(Class<?> clazz);

}
