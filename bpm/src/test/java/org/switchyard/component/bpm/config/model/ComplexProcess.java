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

import static org.switchyard.common.io.resource.ResourceType.BPMN2;
import static org.switchyard.common.io.resource.ResourceType.DSL;
import static org.switchyard.common.io.resource.ResourceType.DSLR;

import org.switchyard.common.io.resource.SimpleResource;
import org.switchyard.component.bpm.Process;
import org.switchyard.component.bpm.config.model.ComplexProcess.My1stHandler;
import org.switchyard.component.bpm.config.model.ComplexProcess.My2ndHandler;
import org.switchyard.component.bpm.config.model.ComplexProcess.MyDsl;
import org.switchyard.component.bpm.config.model.ComplexProcess.MyDslr;
import org.switchyard.component.bpm.config.model.ComplexProcess.ProcessIface;
import org.switchyard.component.bpm.task.BaseTaskHandler;
import org.switchyard.component.bpm.task.Task;
import org.switchyard.component.bpm.task.TaskManager;

/**
 * An complex process example.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@Process(
    value=ProcessIface.class,
    definition="path/to/my.bpmn",
    definitionType=BPMN2,
    id="ComplexProcess",
    resources={MyDsl.class, MyDslr.class},
    taskHandlers={My1stHandler.class, My2ndHandler.class})
public interface ComplexProcess {

    public static interface ProcessIface {}

    public static final class MyDsl extends SimpleResource {
        public MyDsl() {
            super("path/to/my.dsl", DSL);
        }
    }

    public static final class MyDslr extends SimpleResource {
        public MyDslr() {
            super("path/to/my.dslr", DSLR);
        }
    }

    public static final class My1stHandler extends BaseTaskHandler {
        public void executeTask(Task task, TaskManager taskManager) {
            // do something
        }
    }

    public static final class My2ndHandler extends BaseTaskHandler {
        public My2ndHandler() {
            super("My2ndHandler");
        }
        public void executeTask(Task task, TaskManager taskManager) {
            // do something
        }
        public void abortTask(Task task, TaskManager taskManager) {
            // do something
        }
    }

}
