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
package org.switchyard.common.camel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;

/**
 * An extension of DataHandler which implements {@link DataSource} interface.
 * As result instances of this class may be used as {@link DataHandler} and {@link DataSource}.
 */
public class HandlerDataSource extends DataHandler implements DataSource {

    /**
     * Creates new handler.
     * 
     * @param handler Handler to wrap.
     */
    public HandlerDataSource(DataHandler handler) {
        super(handler.getDataSource());
    }

    @Override
    public String getContentType() {
        return getDataSource().getContentType();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return getDataSource().getInputStream();
    }

    @Override
    public String getName() {
        return getDataSource().getName();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return getDataSource().getOutputStream();
    }

}
