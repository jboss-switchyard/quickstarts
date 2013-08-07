/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.component.bpm.config.model;

import org.drools.core.event.DebugProcessEventListener;
import org.switchyard.component.bpm.annotation.BPM;
import org.switchyard.component.bpm.annotation.SignalEvent;
import org.switchyard.component.bpm.annotation.UserGroupCallback;
import org.switchyard.component.bpm.annotation.WorkItemHandler;
import org.switchyard.component.common.knowledge.LoggerType;
import org.switchyard.component.common.knowledge.annotation.Channel;
import org.switchyard.component.common.knowledge.annotation.Fault;
import org.switchyard.component.common.knowledge.annotation.Global;
import org.switchyard.component.common.knowledge.annotation.Input;
import org.switchyard.component.common.knowledge.annotation.Listener;
import org.switchyard.component.common.knowledge.annotation.Logger;
import org.switchyard.component.common.knowledge.annotation.Manifest;
import org.switchyard.component.common.knowledge.annotation.Output;
import org.switchyard.component.common.knowledge.annotation.Property;
import org.switchyard.component.common.knowledge.annotation.Resource;
import org.switchyard.component.common.knowledge.annotation.ResourceDetail;

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
            @Resource(location="foobar.bpmn", type="BPMN2"),
            @Resource(location="foobar.xls", type="DTABLE",
                detail=@ResourceDetail(inputType="XLS", worksheetName="MySheet", usingExternalTypes=true))
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
        outputs=@Output(from="outputVar", to="message.content"),
        faults=@Fault(from="faultVar", to="message.content")
    )
    public void process(Object stuff);

}
