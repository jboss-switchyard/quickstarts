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
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.component.common.selector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.switchyard.component.common.selector.config.model.OperationSelectorModel;
import org.switchyard.component.common.selector.config.model.RegexOperationSelectorModel;
import org.switchyard.component.common.selector.config.model.StaticOperationSelectorModel;
import org.switchyard.component.common.selector.config.model.XPathOperationSelectorModel;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * A base class of OperationSelector which determine the operation to be mapped to the binding.
 * 
 * @param <T> the type of source object
 */
public abstract class OperationSelector<T> {

    private String _defaultNamespace;
    
    private OperationSelectorModel _model;
    
    /**
     * Constructor.
     * @param model OperationSelectorModel
     */
    public OperationSelector(OperationSelectorModel model) {
        _model = model;
    }
    
    /**
     * Select a operation.
     * 
     * @param content message content to search for operation
     * @return operation QName
     * @throws Exception if failed to determine the operation
     */
    public QName selectOperation(T content) throws Exception {
        QName operationQName = null;
        
        if (_model instanceof StaticOperationSelectorModel) {
            StaticOperationSelectorModel staticModel = StaticOperationSelectorModel.class.cast(_model);
            operationQName = QName.valueOf(staticModel.getOperationName());

        } else if (_model instanceof XPathOperationSelectorModel) {
            XPathOperationSelectorModel xpathModel = XPathOperationSelectorModel.class.cast(_model);
            operationQName = xpathMatch(xpathModel.getExpression(), extractDomDocument(content));
        } else if (_model instanceof RegexOperationSelectorModel) {
            RegexOperationSelectorModel regexModel = RegexOperationSelectorModel.class.cast(_model);
            operationQName = regexMatch(regexModel.getExpression(), extractString(content));
        } else {
            throw new Exception("Unsupported OperationSelector configuration: " + _model);
        }
        
        if (_defaultNamespace != null && operationQName.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) {
            operationQName = new QName(_defaultNamespace, operationQName.getLocalPart(), operationQName.getPrefix());
        }
        return operationQName;
    }
    
    /**
     * Sets default namespace.
     * @param namespace namespace
     * @return this instance for chaining
     */
    public OperationSelector<T> setDefaultNamespace(String namespace) {
        _defaultNamespace = namespace;
        return this;
    }
    
    /**
     * Extract a DOM Document from content.
     * 
     * @param content content
     * @return extracted DOM Document
     */
    protected abstract Document extractDomDocument(T content) throws Exception;
    
    /**
     * Extract a String from content.
     * 
     * @param content content
     * @return extracted String
     */
    protected abstract String extractString(T content) throws Exception;
    
    private QName xpathMatch(String expression, Document content) throws Exception {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        NodeList result = null;
        try {
            XPathExpression expr = xpath.compile(expression);
            result = NodeList.class.cast(expr.evaluate(content, XPathConstants.NODESET));
        } catch (Exception e) {
            throw new Exception("Couldn't evaluate XPath expression '" + expression + "'", e);
        }

        if (result.getLength() == 1) {
            return QName.valueOf(result.item(0).getTextContent());
        } else if (result.getLength() == 0) {
            throw new Exception("No node has been matched with the XPath expression '"
                    + expression + "' in the payload. It couldn't determine the operation.");
        } else {
            throw new Exception("Multiple nodes have been matched with the XPath expression '"
                    + expression + "' in the payload. It couldn't determine the operation.");
        }
    }
    
    private QName regexMatch(String expression, String content) throws Exception {
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find()) {
            throw new Exception("No node has been matched with the Regex expression '"
                    + expression + "' in the payload. It couldn't determine the operation.");
        } else {
            String operation = matcher.group();

            if (matcher.find()) {
                throw new Exception("Multiple nodes have been matched with the Regex expression '"
                        + expression + "' in the payload. It couldn't determine the operation.");
            }
            return QName.valueOf(operation);
        }
    }

}
