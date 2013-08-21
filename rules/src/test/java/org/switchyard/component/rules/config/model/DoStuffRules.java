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
package org.switchyard.component.rules.config.model;

import org.kie.api.event.rule.DebugWorkingMemoryEventListener;
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
import org.switchyard.component.rules.annotation.FireUntilHalt;
import org.switchyard.component.rules.annotation.Rules;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@Rules(
    channels=@Channel(name="theName", operation="theOperation", reference="theReference", value=RulesModelTests.TestChannel.class),
    listeners=@Listener(DebugWorkingMemoryEventListener.class),
    loggers=@Logger(interval=2000, log="theLog", type=LoggerType.CONSOLE),
    manifest=@Manifest(
        //container=@Container(baseName="theBase", scan=true, scanInterval=1000, releaseId="theGroupId:theArtifactId:theVersion", sessionName="theSession"),
        resources={
            @Resource(location="foo.drl", type="DRL"),
            @Resource(location="bar.dsl", type="DSL"),
            @Resource(location="foobar.xls", type="DTABLE",
                detail=@ResourceDetail(inputType="XLS", worksheetName="MySheet")) // SWITCHYARD-1662, usingExternalTypes=true))
        }),
    properties=@Property(name="foo", value="bar")
)
public interface DoStuffRules extends DoStuff {

    @Override
    @FireUntilHalt(
        eventId="theEventId",
        globals=@Global(from="context['foobar']", to="globalVar"),
        inputs=@Input(from="message.content.nested", to="inputVar"),
        outputs=@Output(from="outputVar", to="message.content"),
        faults=@Fault(from="faultVar", to="message.content")
    )
    public void process(Object stuff);

}
