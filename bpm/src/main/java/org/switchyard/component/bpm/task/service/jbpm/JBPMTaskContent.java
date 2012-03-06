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
import org.switchyard.component.bpm.task.service.BaseTaskContent;
import org.switchyard.component.bpm.task.service.TaskContent;

/**
 * A jBPM TaskContent implementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class JBPMTaskContent extends BaseTaskContent {

    private final org.jbpm.task.service.ContentData _wrapped;

    /**
     * Creates a new JBPMTaskContent with the specified ContentData.
     * @param contentData the specified ContentData.
     */
    public JBPMTaskContent(org.jbpm.task.service.ContentData contentData) {
        if (contentData != null) {
            _wrapped = contentData;
        } else {
            _wrapped = new org.jbpm.task.service.ContentData();
            _wrapped.setAccessType(AccessType.Inline);
        }
    }

    /**
     * Creates a new JBPMTaskContent with the specified Content.
     * @param content the specified Content
     */
    public JBPMTaskContent(org.jbpm.task.Content content) {
        this((org.jbpm.task.service.ContentData)null);
        _wrapped.setContent(content.getContent());
        setId(content.getId());
    }

    org.jbpm.task.service.ContentData getWrapped() {
        return _wrapped;
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return _wrapped.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskContent setType(String type) {
        _wrapped.setType(type);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBytes() {
        return _wrapped.getContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskContent setBytes(byte[] bytes) {
        _wrapped.setContent(bytes);
        return this;
    }

}
