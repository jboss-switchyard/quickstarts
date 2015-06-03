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

package org.switchyard.transform.config.model.transformers;

import javax.inject.Named;
import javax.xml.namespace.QName;

import org.switchyard.transform.BaseTransformer;

@SuppressWarnings("rawtypes")
@Named("BeanTransformer")
public class BeanTransformer extends BaseTransformer {

    @Override
    public Object transform(Object from) {
        return from;
    }

    @Override
    public QName getFrom() {
        return new QName("urn:switchyard-transform:test-transformers:1.0", "c");
    }

    @Override
    public QName getTo() {
        return new QName("urn:switchyard-transform:test-transformers:1.0", "a");
    }
}
