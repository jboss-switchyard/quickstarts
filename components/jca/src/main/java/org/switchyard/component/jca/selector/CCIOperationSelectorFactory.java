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
package org.switchyard.component.jca.selector;

import org.switchyard.component.common.selector.OperationSelectorFactory;
import org.switchyard.component.jca.composer.MappedRecordBindingData;
import org.switchyard.selector.OperationSelector;

/**
 * An example of OperationSelectorFactory implementation for CCI MappedRecord.
 */
public class CCIOperationSelectorFactory extends OperationSelectorFactory<MappedRecordBindingData> {

    @Override
    public Class<MappedRecordBindingData> getTargetClass() {
        return MappedRecordBindingData.class;
    }

    @Override
    public Class<? extends OperationSelector<MappedRecordBindingData>> getDefaultOperationSelectorClass() {
        return CCIOperationSelector.class;
    }

}
