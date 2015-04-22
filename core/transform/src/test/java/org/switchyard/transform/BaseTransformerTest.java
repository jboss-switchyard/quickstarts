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

package org.switchyard.transform;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Test;

class StringToIntegerTransformer extends BaseTransformer<String, Integer> {
    public Integer transform(String num) {
        return 5;
    }
}

class UntypedTransformer extends BaseTransformer {
    public Object transform(Object obj) {
        return null;
    }
}

class ImplementsTransfomer implements Transformer {

    @Override
    public QName getFrom() {
        return null;
    }

    @Override
    public Class<?> getFromType() {
        return String.class;
    }

    @Override
    public QName getTo() {
        return null;
    }

    @Override
    public Class<?> getToType() {
        return Boolean.class;
    }

    @Override
    public Transformer setFrom(QName fromType) {
        return null;
    }

    @Override
    public Transformer setTo(QName toType) {
        return null;
    }

    @Override
    public Object transform(Object from) {
        return null;
    }
    
}
