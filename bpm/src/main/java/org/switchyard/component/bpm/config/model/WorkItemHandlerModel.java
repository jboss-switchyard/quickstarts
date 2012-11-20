/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.bpm.config.model;

import org.kie.runtime.process.WorkItemHandler;
import org.switchyard.config.model.NamedModel;

/**
 * A WorkItemHandler Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface WorkItemHandlerModel extends NamedModel {

    /**
     * The workItemHandler XML element.
     */
    public static final String WORK_ITEM_HANDLER = "workItemHandler";

    /**
     * Gets the WorkItemHandler class.
     * @param loader the ClassLoader to use
     * @return the WorkItemHandler class
     */
    public Class<? extends WorkItemHandler> getClazz(ClassLoader loader);

    /**
     * Sets the WorkItemHandler class.
     * @param clazz the WorkItemHandler class
     * @return this WorkItemHandlerModel (useful for chaining)
     */
    public WorkItemHandlerModel setClazz(Class<? extends WorkItemHandler> clazz);

}
