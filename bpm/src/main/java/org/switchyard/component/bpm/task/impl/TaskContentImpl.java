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
package org.switchyard.component.bpm.task.impl;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.task.Content;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.switchyard.component.bpm.task.BaseTaskContent;
import org.switchyard.component.common.knowledge.util.Environments;
import org.switchyard.exception.SwitchYardException;

/**
 * A jBPM TaskContent implementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@SuppressWarnings("serial")
public class TaskContentImpl extends BaseTaskContent {

    /**
     * Creates a new BPMTaskContent with the specified variable map.
     * @param variableMap the specified variable map
     */
    public TaskContentImpl(Map<String, Object> variableMap) {
        super(variableMap);
    }

    /**
     * Creates a new BPMTaskContent with the specified Content.
     * @param content the specified Content
     */
    public TaskContentImpl(Content content) {
        super(toVariableMap(content != null ? content.getContent() : null));
    }

    /**
     * Creates a new BPMTaskContent with the specified ContentData.
     * @param contentData the specified ContentData.
     */
    public TaskContentImpl(ContentData contentData) {
        super(toVariableMap(contentData != null ? contentData.getContent() : null));
    }

    /**
     * Converts the variable map to jBPM-friendly Content.
     * @return the Content
     */
    public Content toContent() {
        return new Content(toContentData().getContent());
    }

    /**
     * Converts the variable map to jBPM-friendly ContentData.
     * @return the ContentData
     */
    public ContentData toContentData() {
        return ContentMarshallerHelper.marshal(getVariableMap(), Environments.getEnvironment(null));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> toVariableMap(byte[] bytes) {
        Map<String, Object> variableMap = null;
        if (bytes != null) {
            Object obj = ContentMarshallerHelper.unmarshall(bytes, Environments.getEnvironment(null));
            if (obj instanceof Map) {
                variableMap = (Map<String, Object>)obj;
            } else if (obj != null) {
                throw new SwitchYardException("unmarshalled jBPM content is not a Map: " + obj);
            }
        }
        if (variableMap == null) {
            variableMap = new HashMap<String, Object>();
        }
        return variableMap;
    }

}
