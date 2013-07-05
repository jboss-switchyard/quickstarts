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
import java.io.Reader;

import javax.xml.namespace.QName;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.switchyard.Message;
import org.switchyard.SwitchYardException;
import org.switchyard.transform.BaseTransformer;

/**
 * JSON to Java Transformer.
 *
 * @author Alejandro Montenegro &lt;<a href="mailto:aamonten@gmail.com">aamonten@gmail.com</a>&gt;
 *
 * @param <F> From Type
 * @param <T> To Type.
 */
public class JSON2JavaTransformer<F, T> extends BaseTransformer<Message, Message> {

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
    public JSON2JavaTransformer(QName from, QName to, ObjectMapper mapper, Class clazz) {
        super(from, to);
        this._mapper = mapper;
        this._clazz = clazz;
    }

    @Override
    public Message transform(Message message) {

        try {
            Object result = _mapper.readValue(message.getContent(Reader.class), _clazz);

            if (_clazz.isInstance(result)) {
                message.setContent(result);
                return message;
            } else {
                throw new SwitchYardException("Result of transformation has wrong instance type " + result.getClass());
            }
        } catch (JsonParseException e) {
            throw new SwitchYardException("Unexpected JSON parse exception, check your transformer configuration", e);
        } catch (JsonMappingException e) {
            throw new SwitchYardException("Unexpected JSON mapping exception, check your transformer configuration", e);
        } catch (IOException e) {
            throw new SwitchYardException("Unexpected I/O exception, check your transformer configuration", e);
        }
    }
}
