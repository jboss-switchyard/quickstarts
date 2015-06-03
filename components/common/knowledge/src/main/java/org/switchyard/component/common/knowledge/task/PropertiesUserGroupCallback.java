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
package org.switchyard.component.common.knowledge.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

// SWITCHYARD-1755: internal api usage still required (public APIs insufficient)
import org.kie.internal.task.api.UserGroupCallback;
import org.switchyard.common.io.pull.PropertiesPuller;
import org.switchyard.common.io.pull.PropertiesPuller.PropertiesType;
import org.switchyard.common.io.pull.Puller.PathType;

/**
 * An implementation of a UserGroupCallback based on org.jbpm.services.task.identity.JBossUserGroupCallbackImpl.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class PropertiesUserGroupCallback implements UserGroupCallback {

    private Map<String, List<String>> _groupStore = new HashMap<String, List<String>>();
    private Set<String> _allGroups = new HashSet<String>();

    /**
     * Constructs a new PropertiesUserGroupCallback.
     */
    public PropertiesUserGroupCallback() {
        init(System.getProperty("jbpm.user.group.mapping", System.getProperty("jboss.server.config.dir", "target/classes") + "/roles.properties"));
    }

    /**
     * Constructs a new PropertiesUserGroupCallback.
     * @param location the location
     */
    public PropertiesUserGroupCallback(String location) {
        init(location);
    }

    /**
     * Constructs a new PropertiesUserGroupCallback.
     * @param userGroups the userGroups
     */
    public PropertiesUserGroupCallback(Properties userGroups) {
        init(userGroups);
    }

    private void init(String location) {
        if (location.startsWith("classpath:")) {
            location = location.replaceFirst("classpath:", "");
        } else if (location.startsWith("file:")) {
            location = location.replaceFirst("file:", "");
        }
        PropertiesType propertiesType = PropertiesType.PROPERTIES;
        if (location != null && location.endsWith(".xml")) {
            propertiesType = PropertiesType.XML;
        }
        Properties userGroups = new PropertiesPuller(propertiesType).pullPath(location, PathType.values());
        init(userGroups);
    }

    private void init(Properties userGroups) {
        if (userGroups == null) {
            userGroups = new Properties();
        }
        List<String> groups = null;
        Iterator<Object> it = userGroups.keySet().iterator();
        while (it.hasNext()) {
            String userId = (String) it.next();
            groups = Arrays.asList(userGroups.getProperty(userId, "").split(","));
            _groupStore.put(userId, groups);
            _allGroups.addAll(groups);
        }
        // always add Administrator if not already present
        if (!_groupStore.containsKey("Administrator")) {
            _groupStore.put("Administrator", Collections.singletonList("Administrators"));
            _allGroups.add("Administrators");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsUser(String userId) {
        return _groupStore.containsKey(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsGroup(String groupId) {
        return _allGroups.contains(groupId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getGroupsForUser(String userId, List<String> groupIds, List<String> allExistingGroupIds) {
        List<String> groups = _groupStore.get(userId);
        if (groups == null) { 
            groups = new ArrayList<String>(0);
        }
        return groups;
    }

}
