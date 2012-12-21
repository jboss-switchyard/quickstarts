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
package org.switchyard.quickstarts.demos.helpdesk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;
import org.switchyard.component.bpm.task.Task;
import org.switchyard.component.bpm.task.TaskClient;
import org.switchyard.component.bpm.task.TaskContent;
import org.switchyard.component.bpm.task.TaskService;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@ManagedBean(name="helpDesk")
@SessionScoped
public class HelpDesk {

    private static final Logger LOGGER = Logger.getLogger(HelpDesk.class);
    private static final String TICKET = "ticket";
    private static final Map<String,List<String>> USERS_GROUPS = new LinkedHashMap<String,List<String>>();
    static {
        USERS_GROUPS.put("krisv", Arrays.asList(new String[] {"developers"}));
        USERS_GROUPS.put("david", Arrays.asList(new String[] {"users"}));
    }

    private final TaskClient _taskClient;
    private final List<Task> _userTasks;
    private final Map<Long, Ticket> _userTickets;
    private String _userId;
    

    public HelpDesk() {
        _taskClient = TaskService.instance().newTaskClient();
        _userTasks = Collections.synchronizedList(new ArrayList<Task>());
        _userTickets = Collections.synchronizedMap(new LinkedHashMap<Long, Ticket>());
        _userId = "krisv";
    }

    public String getUserId() {
        return _userId;
    }

    public void setUserId(String userId) {
        _userId = userId;
    }

    public String getGroupId() {
        List<String> groups = USERS_GROUPS.get(_userId);
        return (groups != null && groups.size() > 0) ? groups.get(0) : null;
    }

    public Map<String,String> getUsersGroups() {
        Map<String,String> usersGroups = new LinkedHashMap<String,String>();
        for (Map.Entry<String, List<String>> entry : USERS_GROUPS.entrySet()) {
            String key = entry.getKey();
            usersGroups.put(key + " (" + entry.getValue().get(0) + ")", key);
        }
        return usersGroups;
    }

    public List<Task> getUserTasks() {
        return _userTasks;
    }

    public Map<Long, Ticket> getUserTickets() {
        return _userTickets;
    }

    public void selectUser(ValueChangeEvent vce) {
        setUserId((String)vce.getNewValue());
        fetchTasks();
    }

    private void fetchTasks() {
        synchronized (_userTasks) {
            _userTasks.clear();
            _userTickets.clear();
            try {
                _taskClient.connect();
                List<Task> tasks = _taskClient.getTasksAssignedAsPotentialOwner(_userId, USERS_GROUPS.get(_userId));
                for (Task task : tasks) {
                    _userTasks.add(task);
                    TaskContent taskContent = _taskClient.getTaskContent(task.getTaskContentId());
                    Map<String, Object> params = taskContent.getVariableMap();
                    Ticket ticket = (Ticket)params.get(TICKET);
                    _userTickets.put(task.getProcessInstanceId(), ticket);
                }
            } finally {
                _taskClient.disconnect();
            }
        }
    }

    private void completeTasks() {
        synchronized (_userTasks) {
            if (_userTasks.size() > 0) {
                try {
                    _taskClient.connect();
                    for (Task task : _userTasks) {
                        _taskClient.claim(task.getId(), _userId, USERS_GROUPS.get(_userId));
                        _taskClient.start(task.getId(), _userId);
                        TaskContent taskContent = _taskClient.getTaskContent(task.getTaskContentId());
                        Map<String, Object> results = taskContent.getVariableMap();
                        Ticket ticket = _userTickets.get(task.getProcessInstanceId());
                        results.put(TICKET, ticket);
                        _taskClient.complete(task.getId(), _userId, taskContent);
                    }
                }
                finally {
                    _taskClient.disconnect();
                }
            }
        }
    }

    public void submit() {
        try {
            completeTasks();
            fetchTasks();
        } catch (Throwable t) {
            StringBuilder sb = new StringBuilder();
            sb.append("Problem processing tasks: ");
            sb.append(t.getClass().getName());
            String m = t.getMessage();
            if (m != null) {
                sb.append("(");
                sb.append(m);
                sb.append(")");
            }
            String msg = sb.toString();
            LOGGER.error(msg, t);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(msg));
        }
    }

}
