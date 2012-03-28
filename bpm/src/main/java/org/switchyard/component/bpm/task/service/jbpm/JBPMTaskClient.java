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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.drools.SystemEventListenerFactory;
import org.jbpm.task.AccessType;
import org.jbpm.task.Content;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.switchyard.component.bpm.task.service.BaseTaskClient;
import org.switchyard.component.bpm.task.service.Task;
import org.switchyard.component.bpm.task.service.TaskContent;

/**
 * A jBPM task client.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class JBPMTaskClient extends BaseTaskClient {

    private static final Logger LOGGER = Logger.getLogger(JBPMTaskClient.class);

    private org.jbpm.task.service.TaskClient _wrapped = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() {
        if (_wrapped == null) {
            _wrapped = new TaskClient(new MinaTaskClientConnector(JBPMTaskClient.class.getSimpleName(), new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        }
        if (!isConnected()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Connecting jBPM TaskClient to %s:%s...", getHost(), getPort()));
            }
            setConnected(_wrapped.connect(getHost(), getPort()));
            if (isConnected()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("jBPM TaskClient connected to %s:%s.", getHost(), getPort()));
                }
            } else {
                LOGGER.error(String.format("jBPM TaskClient could not connect to %s:%s!", getHost(), getPort()));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        if (_wrapped != null) {
            if (isConnected()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("Disconnecting jBPM TaskClient from %s:%s...", getHost(), getPort()));
                }
                try {
                    _wrapped.disconnect();
                } catch (Throwable t) {
                    // just to keep checkstyle happy ("Must have at least one statement.")
                    t.getMessage();
                }
                setConnected(false);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("jBPM TaskClient disconnected from %s:%s.", getHost(), getPort()));
                }
            }
            _wrapped = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskContent getTaskContent(Long taskContentId) {
        BlockingGetContentResponseHandler bgcrh = new BlockingGetContentResponseHandler();
        _wrapped.getContent(taskContentId.longValue(), bgcrh);
        bgcrh.waitTillDone(10000);
        Content content = bgcrh.getContent();
        if (content != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("Found content with id %s.", content.getId()));
            }
            return new JBPMTaskContent(content);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Task> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, Locale locale) {
        String language = locale != null ? locale.toString().replace('_', '-') : null;
        BlockingTaskSummaryResponseHandler btsrh =  new BlockingTaskSummaryResponseHandler();
        _wrapped.getTasksAssignedAsPotentialOwner(userId, groupIds, language, btsrh);
        btsrh.waitTillDone(10000);
        List<TaskSummary> taskSummaries = btsrh.getResults();
        int size = taskSummaries.size();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Found %s task%s for %s.", size, (size == 1 ? "" : "s"), userId));
        }
        List<Task> tasks = new ArrayList<Task>();
        for (TaskSummary taskSummary : taskSummaries) {
            BlockingGetTaskResponseHandler bgtrh = new BlockingGetTaskResponseHandler();
             _wrapped.getTask(taskSummary.getId(), bgtrh);
            tasks.add(new JBPMTask(taskSummary, bgtrh.getTask()));
        }
        return tasks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void claim(Long taskId, String userId, List<String> groupIds) {
        BlockingTaskOperationResponseHandler btorh = new BlockingTaskOperationResponseHandler();
        _wrapped.claim(taskId.longValue(), userId, groupIds, btorh);
        btorh.waitTillDone(10000);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Task %s claimed by %s.", taskId, userId));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Long taskId, String userId) {
        BlockingTaskOperationResponseHandler btorh = new BlockingTaskOperationResponseHandler();
        _wrapped.start(taskId.longValue(), userId, btorh);
        btorh.waitTillDone(10000);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Task %s started by %s.", taskId, userId));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void complete(Long taskId, String userId, TaskContent content) {
        BlockingTaskOperationResponseHandler btorh = new BlockingTaskOperationResponseHandler();
        ContentData contentData = null;
        if (content != null) {
            contentData = new ContentData();
            contentData.setType(content.getType());
            contentData.setContent(content.getBytes());
            AccessType accessType = null;
            if (content instanceof JBPMTaskContent) {
                accessType = ((JBPMTaskContent)content).getAccessType();
            }
            contentData.setAccessType(accessType != null ? accessType : AccessType.Inline);
        }
        _wrapped.complete(taskId.longValue(), userId, contentData, btorh);
        btorh.waitTillDone(10000);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Task %s completed by %s.", taskId, userId));
        }
    }

}
