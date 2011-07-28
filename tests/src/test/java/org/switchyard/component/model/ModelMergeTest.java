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

package org.switchyard.component.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.config.model.Models;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestKit;

import java.io.IOException;
import java.io.StringWriter;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@RunWith(SwitchYardRunner.class)
public class ModelMergeTest {

    private SwitchYardTestKit _testKit;

    @Test
    public void test() throws IOException {
        SwitchYardModel model1 = _testKit.loadSwitchYardModel(getClass().getResourceAsStream("switchyard_1.xml"));
        SwitchYardModel model2 = _testKit.loadSwitchYardModel(getClass().getResourceAsStream("switchyard_2.xml"));

        // Merge and compare...
        SwitchYardModel model3 = Models.merge(model1, model2, false);
        compareToExpected(model3);

        // Testing that merging the same model multiple times has no effect...
        model3 = Models.merge(model3, model2, false);
        model2 = _testKit.loadSwitchYardModel(getClass().getResourceAsStream("switchyard_2.xml")); // re-read in case only works for instance equality
        model3 = Models.merge(model3, model2, false);
        compareToExpected(model3);
    }

    private void compareToExpected(SwitchYardModel mergedModel) throws IOException {
        StringWriter stringWriter = new StringWriter();
        mergedModel.write(stringWriter);
        _testKit.compareXMLToResource(stringWriter.toString(), "expected_merged_switchyard.xml");
    }
}
