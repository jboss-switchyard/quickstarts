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

package org.switchyard.transform;

import javax.xml.namespace.QName;
import junit.framework.Assert;
import org.junit.Test;

public class BaseTransformerTest {

    @Test
    public void testTypedTransformer() throws Exception {
        Transformer<?,?> t = new StringToIntegerTransformer();
        Assert.assertEquals(String.class, t.getFromType());
        Assert.assertEquals(Integer.class, t.getToType());
    }
    
    @Test
    public void testUntypedTransformer() throws Exception {
        Transformer<?,?> t = new UntypedTransformer();
        Assert.assertEquals(Object.class, t.getFromType());
        Assert.assertEquals(Object.class, t.getToType());
    }
    
    @Test
    public void testImplementsTransformer() throws Exception {
        Transformer<?,?> t = new ImplementsTransfomer();
        Assert.assertEquals(String.class, t.getFromType());
        Assert.assertEquals(Boolean.class, t.getToType());
    }
}

class StringToIntegerTransformer extends BaseTransformer<String, Integer> {
    public Integer transform(String num) {
        return 5;
    }
}

class UntypedTransformer extends BaseTransformer {
    public Object transform(Object obj) {
        return null;
    }
}

class ImplementsTransfomer implements Transformer {

    @Override
    public QName getFrom() {
        return null;
    }

    @Override
    public Class<?> getFromType() {
        return String.class;
    }

    @Override
    public QName getTo() {
        return null;
    }

    @Override
    public Class<?> getToType() {
        return Boolean.class;
    }

    @Override
    public Transformer setFrom(QName fromType) {
        return null;
    }

    @Override
    public Transformer setTo(QName toType) {
        return null;
    }

    @Override
    public Object transform(Object from) {
        return null;
    }
    
}
