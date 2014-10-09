/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.camel.rss.binding;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

import java.util.Iterator;
import java.util.List;

import org.switchyard.component.bean.Service;

/**
 * Simple service which parses RSS and prints it.
 */
@Service(RSSParseService.class)
public class RSSParseServiceImpl implements RSSParseService {

    @Override
    public void parse(SyndFeed feed) throws Exception {
        @SuppressWarnings("unchecked")
        List<SyndEntry> entries = feed.getEntries();
        Iterator<SyndEntry> itEntries = entries.iterator();

        while (itEntries.hasNext()) {
            SyndEntry entry = (SyndEntry) itEntries.next();
            System.out.println("Title :  " + entry.getTitle());
        }
    }

}
