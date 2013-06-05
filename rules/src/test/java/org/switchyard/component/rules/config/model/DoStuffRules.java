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
package org.switchyard.component.rules.config.model;

import org.kie.api.event.rule.DebugWorkingMemoryEventListener;
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
            @Resource(location="bar.dsl", type="DSL")
        }),
    properties=@Property(name="foo", value="bar")
)
public interface DoStuffRules extends DoStuff {

    @Override
    @FireUntilHalt(
        eventId="theEventId",
        globals=@Global(from="context['foobar']", to="globalVar"),
        inputs=@Input(from="message.content.nested", to="inputVar"),
        outputs=@Output(from="outputVar", to="message.content")
    )
    public void process(Object stuff);

}
