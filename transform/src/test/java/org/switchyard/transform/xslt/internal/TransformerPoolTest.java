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

import java.io.InputStream;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.transform.xslt.internal.TransformerPool;
import org.switchyard.transform.xslt.internal.XsltUriResolver;

public class TransformerPoolTest {
    
    private static final String XSLT = "org/switchyard/transform/xslt/internal/topics.xslt";
    
    private Templates templates;
    
    public TransformerPoolTest() throws Exception {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        tFactory.setURIResolver(new XsltUriResolver());
        InputStream xsltStream = this.getClass().getClassLoader().getResourceAsStream(XSLT);
        templates = tFactory.newTemplates(new StreamSource(xsltStream));
    }

    @Test
    public void takeAndGive() throws Exception {
        TransformerPool pool = new TransformerPool(templates, 1);
        Transformer t1 = pool.take();
        Assert.assertNotNull(t1);
        pool.give(t1);
        Transformer t2 = pool.take();
        Assert.assertNotNull(t2);
        Assert.assertEquals(t1, t2);
    }
    
    @Test
    public void blockOnTake() throws Exception {
        final TransformerPool pool = new TransformerPool(templates, 3);
        // draw down the pool
        pool.take();
        pool.take();
        final Transformer t = pool.take();
        
        // spin up a thread to offer back
        new Thread() {
            public void run() { 
                try {
                    Thread.sleep(2000);
                    pool.give(t);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Assert.fail();
                }
            }
        }.start();
        
        // This will block until the give from our thread above
        Transformer t2 = pool.take();
        Assert.assertEquals(t, t2);
    }
    
}
