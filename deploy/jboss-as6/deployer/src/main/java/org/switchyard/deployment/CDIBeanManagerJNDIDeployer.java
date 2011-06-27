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

package org.switchyard.deployment;

import org.apache.log4j.Logger;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractRealDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.deployers.structure.spi.DeploymentUnitVisitor;
import org.jboss.weld.integration.util.IdFactory;
import org.switchyard.deploy.internal.AbstractDeployment;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

/**
 * CDI BeanManager JNDI deployer.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class CDIBeanManagerJNDIDeployer extends AbstractRealDeployer {

    private static Logger _logger = Logger.getLogger(CDIBeanManagerJNDIDeployer.class);

    /**
     * Public default constructor.
     */
    public CDIBeanManagerJNDIDeployer() {
        setStage(DeploymentStages.PRE_REAL);
    }

    @Override
    protected void internalDeploy(DeploymentUnit unit) throws DeploymentException {
        if (isSwitchYardDeployment(unit)) {
            unit.visit(new BinderVisitor());
        }
    }

    @Override
    protected void internalUndeploy(DeploymentUnit unit) {
        if (isSwitchYardDeployment(unit)) {
            try {
                unit.visit(new UnbinderVisitor());
            } catch (DeploymentException e) {
                _logger.debug("Deployment error undeploying " + unit.getSimpleName());
            }
        }
    }

    private boolean isSwitchYardDeployment(DeploymentUnit unit) {
        return (unit.getClassLoader().getResource(AbstractDeployment.SWITCHYARD_XML) != null);
    }

    private class BinderVisitor implements DeploymentUnitVisitor {

        public void visit(DeploymentUnit unit) throws DeploymentException {
            if (unit.isTopLevel()) {
                Context javaComp = getJavaComp(unit);

                try {
                    Reference reference = new Reference("javax.enterprise.inject.spi.BeanManager", "org.jboss.weld.integration.deployer.jndi.JBossBeanManagerObjectFactory", null);
                    reference.add(new StringRefAddr("id", IdFactory.getIdFromClassLoader(unit.getClassLoader())));
                    javaComp.bind("BeanManager", reference);
                } catch (NamingException e) {
                    throw new DeploymentException("Error binding BeanManager.", e);
                }
            }
        }
            

        public void error(DeploymentUnit unit) {
            // TODO: Any info on the error please?
            _logger.debug("Deployment error deploying " + unit.getSimpleName());
        }

    }

    private class UnbinderVisitor implements DeploymentUnitVisitor {

        public void visit(DeploymentUnit unit) throws DeploymentException {
            if (unit.isTopLevel()) {
                try {
                    Context javaComp = getJavaComp(unit);
                    javaComp.unbind("BeanManager");
                } catch (NamingException e) {
                    throw new DeploymentException("Error unbinding BeanManager.", e);
                }
            }
        }

        public void error(DeploymentUnit unit) {
            // TODO: Any info on the error please?
            _logger.debug("Deployment error undeploying " + unit.getSimpleName());
        }
    }

    private Context getJavaComp(DeploymentUnit unit) throws DeploymentException {
        Context javaComp = null;
        InitialContext initialContext = null;

        ClassLoader originalTCCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(unit.getClassLoader());
            initialContext = new InitialContext();
            javaComp = (Context) initialContext.lookup("java:comp");
        } catch (Exception e) {
            throw new DeploymentException("Unexpected retrieving java:comp from JNDI namespace.", e);
        } finally {
            try {
                if (initialContext != null) {
                    try {
                        initialContext.close();
                    } catch (NamingException e) {
                        throw new DeploymentException("Unexpected error closing InitialContext.", e);
                    }
                }
            } finally {
                Thread.currentThread().setContextClassLoader(originalTCCL);
            }
        }

        return javaComp;
    }
}
