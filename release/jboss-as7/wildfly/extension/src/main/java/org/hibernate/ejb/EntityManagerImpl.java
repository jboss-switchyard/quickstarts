package org.hibernate.ejb;

import java.util.Map;
import javax.persistence.PersistenceContextType;
import javax.persistence.SynchronizationType;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.hibernate.jpa.internal.EntityManagerFactoryImpl;

public class EntityManagerImpl extends org.hibernate.jpa.internal.EntityManagerImpl {

    public EntityManagerImpl(EntityManagerFactoryImpl entityManagerFactory, PersistenceContextType pcType, SynchronizationType synchronizationType, PersistenceUnitTransactionType transactionType, boolean discardOnClose, Class sessionInterceptorClass, Map properties) {
        super(entityManagerFactory, pcType, synchronizationType, transactionType, discardOnClose, sessionInterceptorClass, properties);
    }
}
