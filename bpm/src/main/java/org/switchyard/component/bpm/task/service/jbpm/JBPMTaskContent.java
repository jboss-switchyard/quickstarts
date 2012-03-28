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
package org.switchyard.component.bpm.task.service.jbpm;

import org.jbpm.task.AccessType;
import org.jbpm.task.Content;
import org.jbpm.task.service.ContentData;
import org.switchyard.component.bpm.task.service.BaseTaskContent;

/**
 * A jBPM TaskContent implementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class JBPMTaskContent extends BaseTaskContent {

    private AccessType _accessType;

    /**
     * Creates a new JBPMTaskContent with the specified Content.
     * @param content the specified Content
     */
    public JBPMTaskContent(Content content) {
        setId(content.getId());
        setAccessType(AccessType.Inline);
        setBytes(content.getContent());
    }

    /**
     * Creates a new JBPMTaskContent with the specified ContentData.
     * @param contentData the specified ContentData.
     */
    public JBPMTaskContent(ContentData contentData) {
        setType(contentData.getType());
        setAccessType(contentData.getAccessType());
        setBytes(contentData.getContent());
    }

    /**
     * Gets the access type.
     * @return the access type
     */
    public AccessType getAccessType() {
        return _accessType;
    }

    /**
     * Sets the access type.
     * @param accessType the access type
     * @return this instance (useful for chaining)
     */
    public JBPMTaskContent setAccessType(AccessType accessType) {
        _accessType = accessType;
        return this;
    }

}
