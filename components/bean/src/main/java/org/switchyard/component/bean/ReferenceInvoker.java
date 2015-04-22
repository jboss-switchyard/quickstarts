package org.switchyard.component.bean;

import org.switchyard.metadata.ServiceInterface;

/**
 * Used to create invocation instances for @Reference injection points in 
 * a bean service.  A new ReferenceInvocation should be created for each
 * invocation of the target reference. 
 * <br><br>
 * If the contract associated with the reference has 
 * multiple operations, the overloaded version of newInvocation() must be 
 * used to specify the operation name.
 */
public interface ReferenceInvoker {
    /**
     * Create a new instance of an invocation when the reference contract
     * only has one operation.
     * @return new instance of ReferenceInvocation
     */
    ReferenceInvocation newInvocation();
    
    /**
     * Create a new instance of an invocation for the specified operation.
     * @param operation name of the operation to invoke
     * @return new instance of ReferenceInvocation
     */
    ReferenceInvocation newInvocation(String operation);
    
    /**
     * Get the service contract for the reference.
     * @return service contract used by the reference
     */
    ServiceInterface getContract();
}
