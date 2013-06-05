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

import org.drools.core.event.DebugProcessEventListener;
import org.switchyard.component.bpm.annotation.BPM;
import org.switchyard.component.bpm.annotation.SignalEvent;
import org.switchyard.component.bpm.annotation.UserGroupCallback;
import org.switchyard.component.bpm.annotation.WorkItemHandler;
import org.switchyard.component.common.knowledge.LoggerType;
import org.switchyard.component.common.knowledge.annotation.Channel;
import org.switchyard.component.common.knowledge.annotation.Global;
import org.switchyard.component.common.knowledge.annotation.Input;
import org.switchyard.component.common.knowledge.annotation.Listener;
import org.switchyard.component.common.knowledge.annotation.Logger;
import org.switchyard.component.common.knowledge.annotation.Manifest;
import org.switchyard.component.common.knowledge.annotation.Output;
import org.switchyard.component.common.knowledge.annotation.Property;
import org.switchyard.component.common.knowledge.annotation.Resource;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@BPM(
    persistent=true,
    processId="theProcessId",
    channels=@Channel(name="theName", operation="theOperation", reference="theReference", value=BPMModelTests.TestChannel.class),
    listeners=@Listener(DebugProcessEventListener.class),
    loggers=@Logger(interval=2000, log="theLog", type=LoggerType.CONSOLE),
    manifest=@Manifest(
        //container=@Container(baseName="theBase", scan=true, scanInterval=1000, releaseId="theGroupId:theArtifactId:theVersion", sessionName="theSession"),
        resources={
            @Resource(location="foobar.bpmn", type="BPMN2")
        }),
    properties=@Property(name="foo", value="bar"),
    userGroupCallback=@UserGroupCallback(
        value=BPMModelTests.TestUserGroupCallback.class,
        properties=@Property(name="rab", value="oof")),
    workItemHandlers=@WorkItemHandler(name="MyWIH", value=BPMModelTests.TestWorkItemHandler.class)
)
public interface DoStuffProcess extends DoStuff {

    @Override
    @SignalEvent(
        eventId="theEventId",
        globals=@Global(from="context['foobar']", to="globalVar"),
        inputs=@Input(from="message.content.nested", to="inputVar"),
        outputs=@Output(from="outputVar", to="message.content")
    )
    public void process(Object stuff);

}
