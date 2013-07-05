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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.switchyard.SwitchYardException;
import org.switchyard.common.type.Classes;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.config.model.XsltTransformModel;
import org.switchyard.transform.internal.TransformerFactory;

/**
 * @author Alejandro Montenegro <a href="mailto:aamonten@gmail.com">aamonten@gmail.com</a>
 */
public final class XsltTransformFactory implements TransformerFactory<XsltTransformModel>{

    private static final Logger LOGGER = Logger.getLogger(XsltTransformFactory.class);
    
    /**
     * Create a {@link Transformer} instance from the supplied {@link XsltTransformModel}.
     * @param model the JSON transformer model. 
     * @return the Transformer instance.
     */
    public Transformer newTransformer(XsltTransformModel model) {

        String xsltFileUri = model.getXsltFile();
        boolean failOnWarning = model.failOnWarning();
        QName to = model.getTo();
        QName from = model.getFrom();

        if (xsltFileUri == null || xsltFileUri.equals("")) {
            throw new SwitchYardException("No xsl file has been defined. Check your transformer configuration.");
        }

        try {
            InputStream stylesheetStream = Classes.getResourceAsStream(xsltFileUri);

            if (stylesheetStream == null) {
                throw new SwitchYardException("Failed to load xsl file '" + xsltFileUri + "' from classpath.");
            }
            javax.xml.transform.TransformerFactory tFactory = javax.xml.transform.TransformerFactory.newInstance();
            tFactory.setErrorListener(new XsltTransformFactoryErrorListener(failOnWarning));
            tFactory.setURIResolver(new XsltUriResolver());
            Templates templates = tFactory.newTemplates(new StreamSource(stylesheetStream));
            
            return new XsltTransformer(from, to, templates, failOnWarning);
        } catch (TransformerConfigurationException e) {
            throw new SwitchYardException(
                    "An unexpected error ocurred while creating the xslt transformer",
                    e);
        } catch (IOException e) {
            throw new SwitchYardException("Unable to locate the xslt file "
                    + model.getXsltFile(), e);
        }
    }
    
    private class XsltTransformFactoryErrorListener implements ErrorListener {
        private boolean _failOnWarning;

        public XsltTransformFactoryErrorListener(boolean failOnWarning) {
             this._failOnWarning = failOnWarning;
        }
        
        @Override
        public void warning(TransformerException ex) throws TransformerException {
            if (_failOnWarning) {
                throw ex;
           } else {
                LOGGER.warn("Warning during xslt compilation", ex);
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
