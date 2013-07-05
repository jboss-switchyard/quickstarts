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

package org.switchyard.transform.json.internal;

import javax.xml.namespace.QName;

import org.codehaus.jackson.map.ObjectMapper;
import org.switchyard.SwitchYardException;
import org.switchyard.common.xml.QNameUtil;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.JSONTransformModel;
import org.switchyard.transform.internal.TransformerFactory;

/**
 * JSON Transformer factory.
 *
 * @author Alejandro Montenegro &lt;<a href="mailto:aamonten@gmail.com">aamonten@gmail.com</a>&gt;
 */
public final class JSONTransformFactory implements TransformerFactory<JSONTransformModel> {

    /**
     * Create a {@link Transformer} instance from the supplied {@link JSONTransformModel}.
     * @param model the JSON transformer model. 
     * @return the Transformer instance.
     */
    public Transformer newTransformer(JSONTransformModel model) {

        QName from = model.getFrom();
        QName to = model.getTo();

        assertValidJSONTransformSpec(from, to);

        if (QNameUtil.isJavaMessageType(from)) {
            // Java to JSON....
            Class clazz = toJavaMessageType(from);
            return new Java2JSONTransformer(from, to, new ObjectMapper(), clazz);
        } else {
            // JSON to Java....
            Class clazz = toJavaMessageType(to);
            return new JSON2JavaTransformer(from, to, new ObjectMapper(), clazz);
        }
    }

    private static Class toJavaMessageType(QName name) {
        Class clazz = QNameUtil.toJavaMessageType(name);
        if (clazz == null) {
            throw new SwitchYardException("Not able to find class definition " + name);
        }
        return clazz;
    }

    private static void assertValidJSONTransformSpec(QName from, QName to) {
        if (QNameUtil.isJavaMessageType(from)) {
            if (QNameUtil.isJavaMessageType(to)) {
                // Both of them is a Java type spec...
                throwInvalidToFromSpecException();
            }
        } else if (QNameUtil.isJavaMessageType(to)) {
            if (QNameUtil.isJavaMessageType(from)) {
                // Both of them is a Java type spec...
                throwInvalidToFromSpecException();
            }
        } else {
            // Neither of them is a Java type spec...
            throwInvalidToFromSpecException();
        }
    }

    private static void throwInvalidToFromSpecException() {
        throw new SwitchYardException("Invalid JSON Transformer configuration.  One (and only one) of the specified 'to' and 'from' transform types must be a Java type.");
    }
}
