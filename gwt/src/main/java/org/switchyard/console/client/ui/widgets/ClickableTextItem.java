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

package org.switchyard.console.client.ui.widgets;

import org.jboss.as.console.client.widgets.forms.FormItem;

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

        isModified = false;
    }

    @Override
    public T getValue() {
        return _value;
    }

    @Override
    public void resetMetaData() {
        isUndefined = true;
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

}
