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
package org.switchyard.console.server;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.CancellationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;

/**
 * DMRServlet forwards DMR requests to ModelController.
 * 
 * @author Rob Cernich
 */
public class DMRServlet extends HttpServlet {

    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** The outcome type "failed". */
    public static final String FAILED = "failed";
    /** The field name "outcome". */
    public static final String OUTCOME = "outcome";
    /** The content type supported by this servlet. */
    public static final String DMR_CONTENT_TYPE = "application/dmr-encoded";

    private ModelControllerClient _modelControllerClient;

    @Override
    public void init() throws ServletException {
        try {
            _modelControllerClient = ModelControllerClient.Factory.create("localhost",
                    Integer.valueOf(getServletConfig().getInitParameter("dmr.service.port")));
        } catch (NumberFormatException e) {
            throw new ServletException("DMR service port is not defined.", e);
        } catch (UnknownHostException e) {
            throw new ServletException("Could not initialize DMR client.", e);
        }
    }

    @Override
    public void destroy() {
        try {
            _modelControllerClient.close();
        } catch (IOException e) {
            // FIXME
            e.getCause();
        }
        _modelControllerClient = null;
        super.destroy();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String contentTypeValue = req.getContentType();
        if (contentTypeValue == null) {
            resp.sendError(500, "Unsupported content type: " + contentTypeValue);
            return;
        }
        boolean accepted = false;
        String[] contentTypes = contentTypeValue.split(";");
        for (String contentType : contentTypes) {
            if (DMR_CONTENT_TYPE.equals(contentType.trim())) {
                accepted = true;
                break;
            }
        }
        if (!accepted) {
            resp.sendError(500, "Unsupported content type: " + req.getContentType());
            return;
        }

        int status = 200;
        ModelNode respNode;
        try {
            ModelNode reqNode = ModelNode.fromBase64(req.getInputStream());
            // TODO: think about running this asynchronously so we can cancel
            // after a period of time (i.e. so we don't hang on this call)
            respNode = _modelControllerClient.execute(reqNode);
        } catch (CancellationException e) {
            resp.sendError(500, "Unexpected exception occurred: " + e.getMessage());
            return;
        }

        if (respNode.hasDefined(OUTCOME) && FAILED.equals(respNode.get(OUTCOME).asString())) {
            status = 500;
        }

        resp.setContentType(DMR_CONTENT_TYPE);
        resp.setStatus(status);
        respNode.writeBase64(resp.getOutputStream());
    }
}
