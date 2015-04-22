/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.runtime.remote;

import org.kie.api.runtime.manager.Context;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.remote.jaxb.gen.InsertObjectCommand;
import org.kie.services.client.api.command.KieSessionClientCommandObject;
import org.kie.services.client.api.command.RemoteConfiguration;

/**
 * ExtendedKieSessionClientCommandObject.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class ExtendedKieSessionClientCommandObject extends KieSessionClientCommandObject {

    //private final Context<?> _context;
    private final Long _sessionIdentifier;

    ExtendedKieSessionClientCommandObject(RemoteConfiguration config, Context<?> context, Long sessionIdentifier) {
        super(config);
        //_context = context;
        _sessionIdentifier = sessionIdentifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FactHandle insert(Object object) {
        InsertObjectCommand cmd = new InsertObjectCommand();
        cmd.setObject(object);
        executeCommand(cmd);
        return null; // TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getId() {
        return _sessionIdentifier.intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getIdentifier() {
        return _sessionIdentifier.longValue();
    }

}
