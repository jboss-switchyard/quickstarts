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

package org.switchyard.transform.xslt.internal;

import javax.xml.namespace.QName;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import org.apache.log4j.Logger;
import org.switchyard.Message;
import org.switchyard.config.model.Scannable;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.transform.BaseTransformer;
import org.w3c.dom.Document;

/**
 * XSLT Transformer {@link org.switchyard.transform.Transformer}.
 * 
 * @author <a href="mailto:aamonten@gmail.com">Alejandro Montenegro</a>
 * @param <F> From Type
 * @param <T> To Type.
 */
@Scannable(false)
public class XsltTransformer<F, T> extends BaseTransformer<Message, Message> {

    private static final Logger LOGGER = Logger.getLogger(XsltTransformer.class);
    private Templates _templates;
    private boolean  _failOnWarning;
    
    /**
     * Public constructor.
     * 
     * @param from From type.
     * @param to To type.
     * @param templates XSL Template instance
     * @param failOnWarning whether a warning should be reported as an SwitchYardException or just log
     */
    public XsltTransformer(QName from, QName to, Templates templates, boolean failOnWarning) {
        super(from, to);
        this._templates = templates;
        this._failOnWarning = failOnWarning;
    }

    @Override
    public Message transform(Message message) {

        try {
            DOMSource source = message.getContent(DOMSource.class);
            DOMResult result = new DOMResult();
            javax.xml.transform.Transformer transformer = _templates.newTransformer();
            transformer.setErrorListener(new XsltTransformerErrorListener(_failOnWarning));
            transformer.transform(source, result);
            message.setContent(((Document)result.getNode()).getDocumentElement());

        } catch (Exception e) {
            throw new SwitchYardException("Error during xslt transformation", e);
        }
        return message;
    }

    private class XsltTransformerErrorListener implements ErrorListener {
        private boolean _failOnWarning;

        public XsltTransformerErrorListener(boolean failOnWarning) {
             this._failOnWarning = failOnWarning;
        }
        
        @Override
        public void warning(TransformerException ex) throws TransformerException {
            if (_failOnWarning) {
                throw ex;
           } else {
                LOGGER.warn("Warning during xslt transformation", ex);
             }
        }

        @Override
        public void error(TransformerException ex) throws TransformerException {
            throw ex;
        }

        @Override
        public void fatalError(TransformerException ex) throws TransformerException {
            throw ex;
        }
    }
}

