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
package org.switchyard.config.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;

import org.switchyard.common.io.resource.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * A {@link Scanner} that uses {@link org.switchyard.config.model.ModelPuller ModelPuller} to pull {@link org.switchyard.config.model.Model Model}s.
 *
 * @param <M> the Model type to scan for
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ModelPullerScanner<M extends Model> implements Scanner<M> {

    private Type _type;
    private String _classpath;
    private Resource _resource;
    private Class<?> _caller;
    private ClassLoader _loader;
    private URI _uri;
    private URL _url;
    private File _file;
    private InputStream _inputStream;
    private Reader _reader;
    private InputSource _inputSource;
    private Document _document;
    private Element _element;
    private QName _qname;

    /**
     * Constructs a default ModelPullerScanner that scans for "/META-INF/switchyard.xml" resource(s).
     */
    public ModelPullerScanner() {
        this("/META-INF/switchyard.xml");
    }

    /**
     * Constructs a ModelPullerScanner that scans for the specified classpath resource.
     * @param classpath the classpath resource
     */
    public ModelPullerScanner(String classpath) {
        this(classpath, ModelPullerScanner.class);
    }

    /**
     * Constructs a ModelPullerScanner that scans for the specified classpath resource.
     * @param classpath the classpath resource
     * @param caller class calling this method, so we can also try it's classloader
     */
    public ModelPullerScanner(String classpath, Class<?> caller) {
        _type = Type.CLASSPATH;
        _classpath = classpath;
        _caller = caller;
    }

    /**
     * Constructs a ModelPullerScanner that scans for the specified classpath resource.
     * @param classpath the classpath resource
     * @param loader the classloader we can also try to find the resource
     */
    public ModelPullerScanner(String classpath, ClassLoader loader) {
        _type = Type.CLASSPATH;
        _classpath = classpath;
        _loader = loader;
    }

    /**
     * Constructs a ModelPullerScanner that scans for the specified Resource.
     * @param resource the Resource
     */
    public ModelPullerScanner(Resource resource) {
        _type = Type.RESOURCE;
        _resource = resource;
    }

    /**
     * Constructs a ModelPullerScanner that scans for the specified Resource using the specified caller class.
     * @param resource the Resource
     * @param caller the class calling this method
     */
    public ModelPullerScanner(Resource resource, Class<?> caller) {
        _type = Type.RESOURCE;
        _resource = resource;
        _caller = caller;
    }

    /**
     * Constructs a ModelPullerScanner that scans for the specified Resource using the specified classloader.
     * @param resource the Resource
     * @param loader the classloader to try
     */
    public ModelPullerScanner(Resource resource, ClassLoader loader) {
        _type = Type.RESOURCE;
        _resource = resource;
        _loader = loader;
    }

    /**
     * Constructs a ModelPullerScanner that scans for the specified URI.
     * @param uri the URI
     */
    public ModelPullerScanner(URI uri) {
        _type = Type.URI;
        _uri = uri;
    }

    /**
     * Constructs a ModelPullerScanner that scans for the specified URL.
     * @param url the URL
     */
    public ModelPullerScanner(URL url) {
        _type = Type.URL;
        _url = url;
    }

    /**
     * Constructs a ModelPullerScanner that scans for the specified File.
     * @param file the File
     */
    public ModelPullerScanner(File file) {
        _type = Type.FILE;
        _file = file;
    }

    /**
     * Constructs a ModelPullerScanner that scans for the specified InputStream.
     * @param inputStream the InputStream
     */
    public ModelPullerScanner(InputStream inputStream) {
        _type = Type.INPUT_STREAM;
        _inputStream = inputStream;
    }

    /**
     * Constructs a ModelPullerScanner that scans for the specified Reader.
     * @param reader the Reader
     */
    public ModelPullerScanner(Reader reader) {
        _type = Type.READER;
        _reader = reader;
    }

    /**
     * Constructs a ModelPullerScanner that scans for the specified InputSource.
     * @param inputSource the InputSource
     */
    public ModelPullerScanner(InputSource inputSource) {
        _type = Type.INPUT_SOURCE;
        _inputSource = inputSource;
    }

    /**
     * Constructs a ModelPullerScanner that scans for the specified Document.
     * @param document the Document
     */
    public ModelPullerScanner(Document document) {
        _type = Type.DOCUMENT;
        _document = document;
    }

    /**
     * Constructs a ModelPullerScanner that scans for the specified Element.
     * @param element the Element
     */
    public ModelPullerScanner(Element element) {
        _type = Type.ELEMENT;
        _element = element;
    }

    /**
     * Constructs a ModelPullerScanner that scans for the specified QName.
     * @param qname the QName
     */
    public ModelPullerScanner(QName qname) {
        _type = Type.QNAME;
        _qname = qname;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScannerOutput<M> scan(ScannerInput<M> input) throws IOException {
        M model;
        switch (_type) {
            case CLASSPATH:
                if (_caller != null) {
                    model = new ModelPuller<M>().pull(_classpath, _caller);
                } else if (_loader != null) {
                    model = new ModelPuller<M>().pull(_classpath, _loader);
                } else {
                    model = new ModelPuller<M>().pull(_classpath, getClass());
                }
                break;
            case RESOURCE:
                if (_caller != null) {
                    model = new ModelPuller<M>().pull(_resource, _caller);
                } else if (_loader != null) {
                    model = new ModelPuller<M>().pull(_resource, _loader);
                } else {
                    model = new ModelPuller<M>().pull(_resource, getClass());
                }
                break;
            case URI:
                model = new ModelPuller<M>().pull(_uri);
                break;
            case URL:
                model = new ModelPuller<M>().pull(_url);
                break;
            case FILE:
                model = new ModelPuller<M>().pull(_file);
                break;
            case INPUT_STREAM:
                model = new ModelPuller<M>().pull(_inputStream);
                break;
            case READER:
                model = new ModelPuller<M>().pull(_reader);
                break;
            case INPUT_SOURCE:
                model = new ModelPuller<M>().pull(_inputSource);
                break;
            case DOCUMENT:
                model = new ModelPuller<M>().pull(_document);
                break;
            case ELEMENT:
                model = new ModelPuller<M>().pull(_element);
                break;
            case QNAME:
                model = new ModelPuller<M>().pull(_qname);
                break;
            default:
                model = null;
        }
        return new ScannerOutput<M>().setModel(model);
    }

    private static enum Type {
        CLASSPATH, RESOURCE, URI, URL, FILE, INPUT_STREAM, READER, INPUT_SOURCE, DOCUMENT, ELEMENT, QNAME;
    }

}
