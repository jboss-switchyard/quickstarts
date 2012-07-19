/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.component.common.rest;

import java.util.List;

import javax.ws.rs.core.MediaType;

/**
 * Represents the REST method's tuple {http_path, http_method, param_name, response type, ...}.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class RsMethod {
    private Class<?> _resource;
    private String _path;
    private String _method;
    private String _paramName;
    private Class<?> _requestType;
    private Class<?> _responseType;
    private Boolean _pathParam;
    private Boolean _queryParam;
    private Boolean _matrixParam;
    private List<MediaType> _consumes;
    private List<MediaType> _produces;

    /**
     * Create a REST Method tuple.
     *
     * @param resource The JAX-RS Resource Class
     */
    public RsMethod(Class<?> resource) {
        _resource = resource;
    }

    /**
     * Return the Resource interface associated with this REST method.
     *
     * @return the Resource class
     */
    public Class<?> getResource() {
        return _resource;
    }

    /**
     * Sets the Resource interface associated with this REST method.
     *
     * @param resource the Resource class
     */
    public void setResource(Class<?> resource) {
        _resource = resource;
    }

    /**
     * Return the HTTP method for the REST method.
     *
     * @return the HTTP method
     */
    public String getMethod() {
        return _method;
    }

    /**
     * Sets the HTTP method for the REST method.
     *
     * @param method the HTTP method
     */
    public void setMethod(String method) {
        _method = method;
    }

    /**
     * Return the HTTP path for the REST method.
     *
     * @return the HTTP path
     */
    public String getPath() {
        return _path;
    }

    /**
     * Sets the HTTP path for the REST method.
     *
     * @param path the HTTP path
     */
    public void setPath(String path) {
        _path = path;
    }

    /**
     * Return the method's parameter name.
     *
     * @return the method's parameter name
     */
    public String getParamName() {
        return _paramName;
    }

    /**
     * Set the method's parameter name.
     *
     * @param param the method's parameter name
     */
    public void setParamName(String param) {
        _paramName = param;
    }

    /**
     * Return the method's request type.
     *
     * @return the method's request type
     */
    public Class<?> getRequestType() {
        return _requestType;
    }

    /**
     * Set the method's request type.
     *
     * @param requestType the method's request type
     */
    public void setRequestType(Class<?> requestType) {
        _requestType = requestType;
    }

    /**
     * Return the method's response type.
     *
     * @return the method's response type
     */
    public Class<?> getResponseType() {
        return _responseType;
    }

    /**
     * Set the method's response type.
     *
     * @param responseType the method's response type
     */
    public void setResponseType(Class<?> responseType) {
        _responseType = responseType;
    }

    /**
     * Return true if this method has a path param.
     *
     * @return true if path param exists
     */
    public Boolean hasPathParam() {
        return _pathParam;
    }

    /**
     * Set if this method's has a path param.
     *
     * @param pathParam true if path param exists else false
     */
    public void setPathParam(Boolean pathParam) {
        _pathParam = pathParam;
    }

    /**
     * Return true if this method has a query param.
     *
     * @return true if query param exists
     */
    public Boolean hasQueryParam() {
        return _queryParam;
    }

    /**
     * Set if this method's has a query param.
     *
     * @param queryParam true if path query exists else false
     */
    public void setQueryParam(Boolean queryParam) {
        _queryParam = queryParam;
    }

    /**
     * Return true if this method has a matrix param.
     *
     * @return true if matrix param exists
     */
    public Boolean hasMatrixParam() {
        return _matrixParam;
    }

    /**
     * Set if this method's has a matrix param.
     *
     * @param matrixParam true if matrix param exists else false
     */
    public void setMatrixParam(Boolean matrixParam) {
        _matrixParam = matrixParam;
    }

    /**
     * Return true if this method has a *param.
     *
     * @return true if *param exists
     */
    public Boolean hasParam() {
        return _matrixParam || _queryParam || _pathParam;
    }

    /**
     * Return the @Consumes MediaTypes associated with this REST method.
     *
     * @return the List of MediaTypes
     */
    public List<MediaType> getConsumes() {
        return _consumes;
    }

    /**
     * Sets the @Consumes MediaTypes associated with this REST method.
     *
     * @param consumes a List of MediaTypes
     */
    public void setConsumes(List<MediaType> consumes) {
        _consumes = consumes;
    }

    /**
     * Return the @Produces MediaType associated with this REST method.
     *
     * @return the list of MediaTypes
     */
    public List<MediaType> getProduces() {
        return _produces;
    }

    /**
     * Sets the @Produces MediaTypes associated with this REST method.
     *
     * @param produces a List of MediaTypes
     */
    public void setProduces(List<MediaType> produces) {
        _produces = produces;
    }
}
