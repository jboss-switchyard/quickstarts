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
package org.switchyard.component.camel.ftp.deploy;

import org.switchyard.component.camel.common.deploy.BaseBindingComponent;
import org.switchyard.component.camel.ftp.model.v1.V1CamelFtpBindingModel;
import org.switchyard.component.camel.ftps.model.v1.V1CamelFtpsBindingModel;
import org.switchyard.component.camel.sftp.model.v1.V1CamelSftpBindingModel;

/**
 * Ftp/ftps/sftp binding component.
 */
public class CamelFtpComponent extends BaseBindingComponent {

    /**
     * Creates new component.
     */
    public CamelFtpComponent() {
        super("CamelFtpComponent",
            V1CamelFtpBindingModel.FTP,
            V1CamelFtpsBindingModel.FTPS,
            V1CamelSftpBindingModel.SFTP
        );
    }

}
