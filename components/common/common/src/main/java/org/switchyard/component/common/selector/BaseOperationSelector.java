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

package org.switchyard.component.common.selector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.switchyard.component.common.CommonCommonMessages;
import org.switchyard.config.model.selector.OperationSelectorModel;
import org.switchyard.config.model.selector.RegexOperationSelectorModel;
import org.switchyard.config.model.selector.StaticOperationSelectorModel;
import org.switchyard.config.model.selector.XPathOperationSelectorModel;
import org.switchyard.selector.OperationSelector;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * A base class of OperationSelector which determine the operation to be mapped to the binding.
 * 
 * @param <T> the type of source object
 */
public abstract class BaseOperationSelector<T> implements OperationSelector<T> {

    private String _defaultNamespace;
    
    private OperationSelectorModel _model;
    
    /**
     * Constructor.
     * @param model OperationSelectorModel
     */
    public BaseOperationSelector(OperationSelectorModel model) {
        _model = model;
    }

    @Override

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
            throw CommonCommonMessages.MESSAGES.unsupportedOperationSelectorConfiguration(_model.toString());
        }
        
        if (_defaultNamespace != null && operationQName.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) {
            operationQName = new QName(_defaultNamespace, operationQName.getLocalPart(), operationQName.getPrefix());
        }
        return operationQName;
    }

    @Override
    public String getDefaultNamespace() {
        return _defaultNamespace;
    }

    @Override
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
            throw CommonCommonMessages.MESSAGES.couldnTEvaluateXPathExpression(expression, e);
        }

        if (result.getLength() == 1) {
            return QName.valueOf(result.item(0).getTextContent());
        } else if (result.getLength() == 0) {
            throw CommonCommonMessages.MESSAGES.noNodeHasBeenMatchedWithTheXPathExpression(expression);
        } else {
            throw CommonCommonMessages.MESSAGES.multipleNodesHaveBeenMatchedWithTheXPathExpression(expression);
        }
    }
    
    private QName regexMatch(String expression, String content) throws Exception {
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(content);
        if (!matcher.find()) {
            throw CommonCommonMessages.MESSAGES.noNodeHasBeenMatchedWithTheRegexExpression(expression);
        } else {
            String operation = matcher.group();

            if (matcher.find()) {
                throw CommonCommonMessages.MESSAGES.multipleNodesHaveBeenMatchedWithTheRegexExpression(expression);
            }
            return QName.valueOf(operation);
        }
    }

}
