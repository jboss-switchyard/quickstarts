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
package org.switchyard.component.jca.composer;

import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.common.composer.MessageComposerFactory;

/**
 * MessageComposerFactory for CCI IndexedRecord.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class IndexedRecordMessageComposerFactory extends MessageComposerFactory<IndexedRecordBindingData> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<IndexedRecordBindingData> getBindingDataClass() {
        return IndexedRecordBindingData.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageComposer<IndexedRecordBindingData> newMessageComposerDefault() {
        return new IndexedRecordMessageComposer();
    }

}
