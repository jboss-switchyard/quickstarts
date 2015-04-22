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

import org.jboss.ballroom.client.widgets.forms.FormItem;

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

/**
 * ClickableTextItem
 * 
 * Presents the text for the form field as a read-only hyperlink.
 * 
 * @param <T> the value type managed by this object.
 * 
 * @author Rob Cernich
 */
public class ClickableTextItem<T> extends FormItem<T> {

    /**
     * ValueAdapter
     * 
     * Provides services for retrieving the display text and link token for the
     * current field value.
     * 
     * @author Rob Cernich
     */
    public interface ValueAdapter<T> {
        /**
         * @param value the field's current value.
         * @return the text to display for the value.
         */
        public String getText(T value);

        /**
         * @param value the field's current value.
         * @return the history token to use for the value.
         */
        public String getTargetHistoryToken(T value);
    }

    private T _value;
    private ValueAdapter<T> _valueAdapter;
    private Hyperlink _link;

    /**
     * Create a new ClickableTextItem.
     * 
     * @param name the name of the bean member.
     * @param title the label text.
     * @param valueAdapter the adapter for the field value.
     */
    public ClickableTextItem(String name, String title, ValueAdapter<T> valueAdapter) {
        super(name, title);
        _link = new Hyperlink();
        _valueAdapter = valueAdapter;

        resetMetaData();
    }

    @Override
    public T getValue() {
        return _value;
    }

    @Override
    public void setValue(T value) {
        _link.setText(_valueAdapter.getText(value));
        _link.setTargetHistoryToken(_valueAdapter.getTargetHistoryToken(value));
    }

    @Override
    public Widget asWidget() {
        return _link;
    }

    @Override
    public void setEnabled(boolean b) {
        // it's not editable anyway
    }

    @Override
    public boolean validate(T value) {
        return true;
    }

    @Override
    public void clearValue() {
        setValue(null);
    }
}
