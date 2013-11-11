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

package org.switchyard.transform.xslt.internal;

import javax.xml.namespace.QName;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;

import org.jboss.logging.Logger;
import org.switchyard.Message;
import org.switchyard.config.model.Scannable;
import org.switchyard.transform.BaseTransformer;
import org.switchyard.transform.TransformLogger;
import org.switchyard.transform.TransformMessages;
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
    private TransformerPool _transformerPool;
    private boolean  _failOnWarning;
    
    /**
     * Public constructor.
     * 
     * @param from From type.
     * @param to To type.
     * @param templates XSL Template instance
     * @param failOnWarning whether a warning should be reported as an SwitchYardException or just log
     * @param maxPoolSize maximum size for the transformer pool
     */
    public XsltTransformer(QName from, QName to, Templates templates, boolean failOnWarning, int maxPoolSize) {
        super(from, to);
        this._templates = templates;
        this._failOnWarning = failOnWarning;
        // max pool size < 0 indicates that pooling should be disabled
        if (maxPoolSize > 0) {
            _transformerPool = new TransformerPool(
                    templates, maxPoolSize, new XsltTransformerErrorListener(_failOnWarning));
        }
    }

    @Override
    public Message transform(Message message) {
        javax.xml.transform.Transformer transformer = null;
        
        try {
            DOMSource source = message.getContent(DOMSource.class);
            DOMResult result = new DOMResult();
            
            if (_transformerPool != null) {
                transformer = _transformerPool.take();
            } else {
                // if no pool is configured, then just create a new transformer for each request
                transformer = _templates.newTransformer();
                transformer.setErrorListener(new XsltTransformerErrorListener(_failOnWarning));
            }
            
            transformer.transform(source, result);
            message.setContent(((Document)result.getNode()).getDocumentElement());

        } catch (Exception e) {
            throw TransformMessages.MESSAGES.errorDuringXsltTransformation(e);
        } finally {
            if (_transformerPool != null && transformer != null) {
                _transformerPool.give(transformer);
            }
        }
        return message;
    }
    
    TransformerPool getTransformerPool() {
        return _transformerPool;
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
               TransformLogger.ROOT_LOGGER.warningDuringTransformation(ex);
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
