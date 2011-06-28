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
package org.switchyard.transform.xslt;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.namespace.QName;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;
import org.switchyard.common.type.Classes;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.transform.Transformer;
import org.switchyard.transform.TransformerFactory;
import org.switchyard.transform.config.model.XsltTransformModel;

/**
 * @author Alejandro Montenegro <a href="mailto:aamonten@gmail.com">aamonten@gmail.com</a>
 */
public final class XsltTransformFactory implements TransformerFactory<XsltTransformModel>{

    /**
     * Create a {@link Transformer} instance from the supplied {@link XsltTransformModel}.
     * @param model the JSON transformer model. 
     * @return the Transformer instance.
     */
    public Transformer newTransformer(XsltTransformModel model) {

        String xsltFileUri = model.getXsltFile();
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
            Templates templates = tFactory.newTemplates(new StreamSource(stylesheetStream));
            
            return new XsltTransformer(from, to, templates);
        } catch (TransformerConfigurationException e) {
            throw new SwitchYardException(
                    "An unexpected error ocurred while creating the xslt transformer",
                    e);
        } catch (IOException e) {
            throw new SwitchYardException("Unable to locate the xslt file "
                    + model.getXsltFile(), e);
        }
    }
}
