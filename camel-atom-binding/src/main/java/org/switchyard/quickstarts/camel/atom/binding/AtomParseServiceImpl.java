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
package org.switchyard.quickstarts.camel.atom.binding;

import org.apache.abdera.parser.stax.FOMEntry;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import junit.framework.Assert;

import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;
import org.switchyard.quickstarts.camel.atom.binding.PrintService;

/**
 * Simple service which parses Atom and prints it.
 */
@Service(AtomParseService.class)
public class AtomParseServiceImpl implements AtomParseService {

    /**
     * Reference which allows sending reply.
     */
    @Inject
    @Reference("OutgoingAtomParseService")
    private AtomParseService _outgoing;

    @Override
    public void parse(FOMEntry entry) throws Exception {
        System.out.println(entry.getTitle());
    }

}
