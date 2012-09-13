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
package org.switchyard.component.bpm.config.model;

import org.drools.event.DebugProcessEventListener;
import org.switchyard.component.bpm.Process;
import org.switchyard.component.bpm.config.model.ComplexProcess.My1stHandler;
import org.switchyard.component.bpm.config.model.ComplexProcess.My2ndHandler;
import org.switchyard.component.bpm.config.model.ComplexProcess.ProcessIface;
import org.switchyard.component.bpm.task.work.BaseTaskHandler;
import org.switchyard.component.bpm.task.work.Task;
import org.switchyard.component.bpm.task.work.TaskManager;

/**
 * An complex process example.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@Process(
    value=ProcessIface.class,
    definition="path/to/my.bpmn",
    definitionType="BPMN2",
    id="ComplexProcess",
    resources={"path/to/my.dsl", "path/to/my.dslr"},
    eventListeners={DebugProcessEventListener.class},
    taskHandlers={My1stHandler.class, My2ndHandler.class})
public interface ComplexProcess {

    public static interface ProcessIface {}

    public static final class My1stHandler extends BaseTaskHandler {
        public void executeTask(Task task, TaskManager manager) {
            // do something
        }
    }

    public static final class My2ndHandler extends BaseTaskHandler {
        public My2ndHandler() {
            super("My2ndHandler");
        }
        public void executeTask(Task task, TaskManager manager) {
            // do something
        }
        public void abortTask(Task task, TaskManager manager) {
            // do something
        }
    }

}
