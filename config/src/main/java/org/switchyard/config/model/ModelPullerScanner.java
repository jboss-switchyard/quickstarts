/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
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
    private URI _uri;
    private URL _url;
    private Resource _resource;
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
        _type = Type.CLASSPATH;
        _classpath = classpath;
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
     * Constructs a ModelPullerScanner that scans for the specified Resource.
     * @param resource the Resource
     */
    public ModelPullerScanner(Resource resource) {
        _type = Type.RESOURCE;
        _resource = resource;
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
                model = new ModelPuller<M>().pull(_classpath, getClass());
                break;
            case URI:
                model = new ModelPuller<M>().pull(_uri);
                break;
            case URL:
                model = new ModelPuller<M>().pull(_url);
                break;
            case RESOURCE:
                model = new ModelPuller<M>().pull(_resource);
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
        CLASSPATH, URI, URL, RESOURCE, FILE, INPUT_STREAM, READER, INPUT_SOURCE, DOCUMENT, ELEMENT, QNAME;
    }

}
