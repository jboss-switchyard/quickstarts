/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.switchyard.component.soap.greeting.transform;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;

import org.milyn.javabean.binding.xml.XMLBinding;
import org.switchyard.component.soap.greeting.Greeting;
import org.switchyard.transform.BaseTransformer;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * @param <F> From type.
 * @param <T> To type.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class SOAPGreetingToJava<F extends Node, T extends Greeting>  extends BaseTransformer<F, T> {

    private XMLBinding _binder;

    /**
     * Public constructor.
     * @throws IOException Error reading binding config.
     * @throws SAXException Error parsing binding config.
     */
    public SOAPGreetingToJava() throws IOException, SAXException {
        _binder = new XMLBinding().add("/transforms/GreetingSOAPBinding.xml").intiailize();
    }

    @Override
    public Greeting transform(Node from) {
        try {
            return _binder.fromXML(new DOMSource(from), Greeting.class);
        } catch (IOException e) {
            throw new IllegalStateException("Transformation from '" + getFrom() + "' to '" + getTo() + "' failed.", e);
        }
    }

    @Override
    public QName getFrom() {
        return QName.valueOf("{urn:switchyard-component-soap:test-greeting:1.0}greet"); // SOAP body element QName
    }

    @Override
    public QName getTo() {
        return new QName("java:org.switchyard.component.soap.greeting.Greeting");
    }
}
