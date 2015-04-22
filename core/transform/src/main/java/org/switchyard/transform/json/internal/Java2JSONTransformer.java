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

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.switchyard.transform.BaseTransformer;
import org.switchyard.transform.internal.TransformMessages;

/**
 * Java to JSON Transformer.
 *
 * @author Alejandro Montenegro &lt;<a href="mailto:aamonten@gmail.com">aamonten@gmail.com</a>&gt;
 *
 * @param <F> From Type
 * @param <T> To Type.
 */
public class Java2JSONTransformer<F, T> extends BaseTransformer<Object, String> {

    private ObjectMapper _mapper;
    private Class _clazz;

    /**
     * Public constructor.
     *
     * @param from   From type.
     * @param to     To type.
     * @param mapper JSON Object Mapper instance.
     * @param clazz  The Java type being mapped.
     */
    public Java2JSONTransformer(QName from, QName to, ObjectMapper mapper, Class clazz) {
        super(from, to);
        this._mapper = mapper;
        this._clazz = clazz;
    }

    @Override
    public String transform(Object from) {

        try {
            StringWriter writer = new StringWriter();

            if (_clazz.isInstance(from)) {
                _mapper.writeValue(writer, from);
                return writer.toString();
            } else {
                throw TransformMessages.MESSAGES.objectToTransformWrongType(from.getClass().toString());
            }
        } catch (JsonProcessingException e) {
            throw TransformMessages.MESSAGES.unexpectedJSONProcessingException(e);
        } catch (IOException e) {
            throw TransformMessages.MESSAGES.unexpectedIOException(e);
        }
    }
}
