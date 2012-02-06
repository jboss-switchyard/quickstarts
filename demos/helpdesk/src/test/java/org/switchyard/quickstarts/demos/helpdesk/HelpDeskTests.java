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
package org.switchyard.quickstarts.demos.helpdesk;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.component.bpm.config.model.BPMSwitchYardScanner;
import org.switchyard.component.bpm.task.service.Task;
import org.switchyard.component.bpm.task.service.TaskClient;
import org.switchyard.component.bpm.task.service.TaskService;
import org.switchyard.component.bpm.task.service.TaskStatus;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.BPMMixIn;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.test.mixins.HTTPMixIn;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    scanners = {BeanSwitchYardScanner.class, BPMSwitchYardScanner.class, TransformSwitchYardScanner.class},
    mixins = {BPMMixIn.class, CDIMixIn.class, HTTPMixIn.class}
)
public class HelpDeskTests {

    private BPMMixIn bpm;
    private HTTPMixIn http;

    @Test
    public void testWithAutomaticHumanTaskCompletion() throws Exception {
        http.postResourceAndTestXML("http://localhost:18001/HelpDeskService", "/xml/soap-request.xml", "/xml/soap-response.xml");
        bpm.connectTaskClient();
        try {
            bpm.completeHumanTasks();
        } finally {
            bpm.disconnectTaskClient();
        }
    }

    @Test
    public void testWithManualHumanTaskCompletion() throws Exception {
        http.postResourceAndTestXML("http://localhost:18001/HelpDeskService", "/xml/soap-request.xml", "/xml/soap-response.xml");
        TaskClient client = TaskService.instance().newTaskClient();
        try {
            client.connect();
            Map<String,List<String>> usersGroups = bpm.getUsersGroups();
            boolean keepWorking;
            do {
                keepWorking = completeHumanTasks(client, usersGroups);
            } while (keepWorking);
        } finally {
            client.disconnect();
        }
    }

    private boolean completeHumanTasks(TaskClient client, Map<String,List<String>> usersGroups) throws Exception {
        boolean keepWorking = false;
        for (String userId : usersGroups.keySet()) {
            List<String> groupIds = usersGroups.get(userId);
            List<Task> tasks = client.getTasksAssignedAsPotentialOwner(userId, groupIds);
            for (Task task : tasks) {
                if (TaskStatus.COMPLETED.equals(task.getStatus())) {
                    continue;
                }
                Long taskId = task.getId();
                client.claim(taskId, userId, groupIds);
                client.start(taskId, userId);
                client.complete(taskId, userId);
                keepWorking = true;
            }
        }
        Thread.sleep(1000);
        return keepWorking;
    }

}
