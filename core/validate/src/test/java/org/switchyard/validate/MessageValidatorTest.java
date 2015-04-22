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

package org.switchyard.validate;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.Message;
import org.switchyard.internal.DefaultMessage;

import javax.xml.namespace.QName;
import java.io.IOException;

/**
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class MessageValidatorTest {

    @Test
    public void test() throws IOException {
        final QName A = new QName("a");

        DefaultMessage message = new DefaultMessage().setContent(A);
        MessageValidator<Message> validator = new MessageValidator<Message>();

        ValidationResult result = validator.validate(message);
        Assert.assertTrue(result.isValid());
        Assert.assertNull(result.getDetail());
        Assert.assertEquals(message, validator.getMessage());
    }
}
