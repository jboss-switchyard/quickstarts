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
package org.switchyard.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.switchyard.common.version.Queries.Projects.SWITCHYARD;
import static org.switchyard.common.version.Queries.Projects.SWITCHYARD_API;
import static org.switchyard.common.version.Queries.Projects.SWITCHYARD_COMMON;
import static org.switchyard.common.version.QueryType.PROJECT_ARTIFACT_ID;
import static org.switchyard.common.version.QueryType.SPECIFICATION_TITLE;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.switchyard.common.version.Queries.Projects;
import org.switchyard.common.version.Query;
import org.switchyard.common.version.Version;
import org.switchyard.common.version.VersionFactory;
import org.switchyard.common.version.Versions;

/**
 * VersionTest.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public class VersionTest {

    @Test
    public void testPrint() {
        Versions.printSwitchYardNotification();
        Versions.printSwitchYardVersions();
    }

    @Test
    public void testEquals() {
        VersionFactory factory = VersionFactory.instance();
        final Version expected = factory.getVersion(SWITCHYARD_COMMON);
        Version actual = factory.getVersion(Projects.create("org.switchyard", "switchyard-common"));
        assertEquals(expected, actual);
        actual = factory.getVersion(SWITCHYARD, new Query(PROJECT_ARTIFACT_ID, "switchyard-common"));
        assertEquals(expected, actual);
        actual = factory.getVersion(new Query(SPECIFICATION_TITLE, "SwitchYard: Common"));
        assertEquals(expected, actual);
    }

    @Test
    public void testCompare() {
        VersionFactory factory = VersionFactory.instance();
        Version api = factory.getVersion(SWITCHYARD_API);
        Version common = factory.getVersion(SWITCHYARD_COMMON);
        Set<Version> set = new TreeSet<Version>();
        // add out of order
        set.add(common);
        set.add(api);
        Iterator<Version> iter = set.iterator();
        // test compare did ordering
        assertTrue("1st version should be api", iter.next() == api);
        assertTrue("2nd version should be common", iter.next() == common);
    }

}
