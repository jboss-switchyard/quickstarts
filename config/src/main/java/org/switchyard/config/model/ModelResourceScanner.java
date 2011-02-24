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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * ModelResourceScanner.
 *
 * @param <M> the Model type to scan for
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ModelResourceScanner<M extends Model> implements Scanner<M> {

    private Type _type;
    private String _resource;
    private URI _uri;
    private URL _url;
    private File _file;
    private InputStream _inputStream;
    private Reader _reader;
    private InputSource _inputSource;
    private Document _document;
    private Element _element;
    private QName _qname;

    public ModelResourceScanner() {
        this("/META-INF/switchyard.xml");
    }

    public ModelResourceScanner(String resource) {
        _type = Type.RESOURCE;
        _resource = resource;
    }

    public ModelResourceScanner(URI uri) {
        _type = Type.URI;
        _uri = uri;
    }

    public ModelResourceScanner(URL url) {
        _type = Type.URL;
        _url = url;
    }

    public ModelResourceScanner(File file) {
        _type = Type.FILE;
        _file = file;
    }

    public ModelResourceScanner(InputStream inputStream) {
        _type = Type.INPUT_STREAM;
        _inputStream = inputStream;
    }

    public ModelResourceScanner(Reader reader) {
        _type = Type.READER;
        _reader = reader;
    }

    public ModelResourceScanner(InputSource inputSource) {
        _type = Type.INPUT_SOURCE;
        _inputSource = inputSource;
    }

    public ModelResourceScanner(Document document) {
        _type = Type.DOCUMENT;
        _document = document;
    }

    public ModelResourceScanner(Element element) {
        _type = Type.ELEMENT;
        _element = element;
    }

    public ModelResourceScanner(QName qname) {
        _type = Type.QNAME;
        _qname = qname;
    }

    @Override
    public ScannerOutput<M> scan(ScannerInput<M> input) throws IOException {
        M model;
        switch (_type) {
            case RESOURCE:
                model = new ModelResource<M>().pull(_resource);
                break;
            case URI:
                model = new ModelResource<M>().pull(_uri);
                break;
            case URL:
                model = new ModelResource<M>().pull(_url);
                break;
            case FILE:
                model = new ModelResource<M>().pull(_file);
                break;
            case INPUT_STREAM:
                model = new ModelResource<M>().pull(_inputStream);
                break;
            case READER:
                model = new ModelResource<M>().pull(_reader);
                break;
            case INPUT_SOURCE:
                model = new ModelResource<M>().pull(_inputSource);
                break;
            case DOCUMENT:
                model = new ModelResource<M>().pull(_document);
                break;
            case ELEMENT:
                model = new ModelResource<M>().pull(_element);
                break;
            case QNAME:
                model = new ModelResource<M>().pull(_qname);
                break;
            default:
                model = null;
        }
        return new ScannerOutput<M>().setModel(model);
    }

    private static enum Type {
        RESOURCE, URI, URL, FILE, INPUT_STREAM, READER, INPUT_SOURCE, DOCUMENT, ELEMENT, QNAME;
    }

}
