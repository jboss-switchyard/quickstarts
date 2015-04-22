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
package org.switchyard.component.common.composer;

import org.switchyard.Exchange;
import org.switchyard.Message;

/**
 * Composes or decomposes SwitchYard Messages (via their Exchange) to and from a source or target object.
 *
 * @param <D> the type of binding data
 * 
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface MessageComposer<D extends BindingData> {

    /**
     * Gets this message composer's associated context mapper.
     * @return the context mapper
     */
    public ContextMapper<D> getContextMapper();

    /**
     * Sets this message composer's associated context mapper.
     * @param contextMapper the context mapper
     * @return this message composer (useful for chaining)
     */
    public MessageComposer<D> setContextMapper(ContextMapper<D> contextMapper);

    /**
     * Takes the data from the passed in source object and composes a SwithYardMessage based on the specified Exchange.
     * @param source the source object
     * @param exchange the exchange to use
     * @return the composed message
     * @throws Exception if a problem happens
     */
    public Message compose(D source, Exchange exchange) throws Exception;

    /**
     * Takes the data from the SwitchYardMessage in the specified Exchange and decomposes it into the target object.
     * @param exchange the exchange to use
     * @param target the target object
     * @return the new target object (could be the same or different than the original target)
     * @throws Exception if a problem happens
     */
    public D decompose(Exchange exchange, D target) throws Exception;

}
