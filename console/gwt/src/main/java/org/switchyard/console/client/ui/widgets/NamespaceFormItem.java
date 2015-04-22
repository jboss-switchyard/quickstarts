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
package org.switchyard.console.client.ui.widgets;

import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.switchyard.console.client.NameTokens;

/**
 * NamespaceFormItem
 * 
 * Unwraps the namespace value of a qname string.
 * 
 * @author Rob Cernich
 */
public class NamespaceFormItem extends TextItem {

    private String _value;

    /**
     * Create a new NamespaceFormItem.
     * 
     * @param name the property name.
     * @param title the display text.
     */
    public NamespaceFormItem(String name, String title) {
        super(name, title);
    }

    @Override
    public String getValue() {
        return _value;
    }

    @Override
    public void clearValue() {
        _value = null;
        super.clearValue();
    }

    @Override
    public void setValue(String value) {
        _value = value;
        super.setValue(NameTokens.parseQName(value)[0]);
    }

    @Override
    public void setEnabled(boolean b) {
        // not editable
    }

}
