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
 
package org.switchyard.component.http.composer;

import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.common.composer.MessageComposerFactory;

/**
 * HttpMessageComposerFactory.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class HttpMessageComposerFactory extends MessageComposerFactory<HttpBindingData> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<HttpBindingData> getBindingDataClass() {
        return HttpBindingData.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageComposer<HttpBindingData> newMessageComposerDefault() {
        return new HttpMessageComposer();
    }

}
