/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.switchyard.transform.xslt;

import java.io.Reader;
import java.io.StringWriter;
import javax.xml.namespace.QName;
import javax.xml.transform.Templates;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.switchyard.Message;
import org.switchyard.config.model.Scannable;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.transform.BaseTransformer;

/**
 * XSLT Transformer {@link org.switchyard.transform.Transformer}.
 * 
 * @author <a href="mailto:aamonten@gmail.com">Alejandro Montenegro</a>
 * @param <F> From Type
 * @param <T> To Type.
 */
@Scannable(false)
public class XsltTransformer<F, T> extends BaseTransformer<Message, Message> {

    private Templates _templates;

    /**
     * Public constructor.
     * 
     * @param from From type.
     * @param to To type.
     * @param templates XSL Template instance
     */
    public XsltTransformer(QName from, QName to, Templates templates) {
        super(from, to);
        this._templates = templates;
    }

    @Override
    public Message transform(Message message) {

        try {
            StreamSource source = new StreamSource(message.getContent(Reader.class));
            StringWriter resultWriter = new StringWriter();
            StreamResult result = new StreamResult(resultWriter);
            javax.xml.transform.Transformer transformer = _templates.newTransformer();
            transformer.transform(source, result);
            message.setContent(resultWriter.toString());

        } catch (Exception e) {
            throw new SwitchYardException("Error during xslt transformation", e);
        }
        return message;
    }
}

