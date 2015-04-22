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
package org.switchyard.transform.ootb.lang;

import javax.xml.namespace.QName;

import org.switchyard.transform.BaseTransformer;

/**
 * SwitchYard transformer that takes care of transforming from a byte array
 * to a String.
 * 
 * @author Daniel Bevenius
 *
 */
public class ByteArrayToStringTransformer extends BaseTransformer<byte[],String>{
    
    /**
     * No-args constructor.
     */
    public ByteArrayToStringTransformer() {
        super(new QName("java:byte[]"), new QName("java:java.lang.String"));
    }
    
    @Override
    public String transform(final byte[] from) {
        if (from == null) {
            return null;
        }
        return new String(from);
    }

}
