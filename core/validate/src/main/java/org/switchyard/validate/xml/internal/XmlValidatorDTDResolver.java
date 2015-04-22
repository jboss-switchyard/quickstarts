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
package org.switchyard.validate.xml.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.switchyard.common.type.Classes;

/**
 * DTDResolver is an entity resolver that helps locate the DTD.
 * 
 * @author <a href="mailto:tcunning@redhat.com">Tom Cunningham</a>
 */
public class XmlValidatorDTDResolver implements EntityResolver {
    private List<String> _schemaFileNames = null;

    /**
     * DTDResolver constructor.
     */
    public XmlValidatorDTDResolver() {
        _schemaFileNames = new ArrayList<String>();
    }

    /**
     * DTDResolver constructor.
     * @param schemaFileNames schemaFileNames
     */
    public XmlValidatorDTDResolver(List<String> schemaFileNames) {
        this();

        if (schemaFileNames != null) {
            for (Iterator<String> iter = schemaFileNames.iterator(); iter.hasNext();) {
                String fem = iter.next();
                _schemaFileNames.add(fem);
            }
        }
    }

    /**
     * Locate the file or throw an exception.
     * @param path
     * @return
     */
    protected URL locateFile(String path) throws IOException {
        if (path == null) {
            return null;
        }
        
        if (new File(path).exists()) {
            try {
                return new File(path).toURI().toURL();
            } catch (Exception e) {
                return null;
            }
        } else {
            URL res = Classes.getResource(path);
            if (res != null) {
                return res;
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String)
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId)
        throws SAXException, IOException {
        if (_schemaFileNames != null) {
            for (Iterator<String> iter = _schemaFileNames.iterator(); iter.hasNext();) {
                String validatorFileName = iter.next();
                
                URL url = null;
                url = locateFile(systemId);
                
                if (url == null) {
                    // Test to see if we're dealing with a local file.   If so,
                    // see if the directory was not specified in the file entry and 
                    // if so try to find the file with Classes.getResource()
                    URL temp = new URL(systemId);
                    if (temp.getProtocol().equals("file")) {
                        File systemFile = new File(systemId);
                        String systemFileName = systemFile.getName();

                        File localFile = new File(validatorFileName);
                        if (localFile.getParentFile() == null) {
                            if (systemFileName.equals(validatorFileName)) {
                                URL res = Classes.getResource(validatorFileName);
                                if (res != null) {
                                    return new InputSource(res.openStream());
                                }
                            }
                        }
                    }
                }
                if (url != null) {
                    return new InputSource(url.openStream());
                }
            }
        }
        return null;
    }
}
