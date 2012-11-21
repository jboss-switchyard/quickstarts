/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.quickstarts.http.binding;

import java.io.IOException;

import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 * Client for HTTP binding.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class HttpBindingClient {

    private static final String BASE_URL = "http://localhost:8080/http-binding";

    public static void main(String[] args) throws Exception {
        String command =  null;
        if (args.length == 0) {
            System.out.println("Usage: HttpBindingClient stock-quote-symbol");
            return;
        } else {
            HTTPMixIn http = new HTTPMixIn();
            http.initialize();
            System.out.println(http.sendString(BASE_URL + "/quote", args[0], HTTPMixIn.HTTP_POST));
        }
    }
}
