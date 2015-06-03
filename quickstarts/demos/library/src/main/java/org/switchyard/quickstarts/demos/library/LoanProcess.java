package org.switchyard.quickstarts.demos.library;

import org.switchyard.quickstarts.demos.library.types.LoanRequest;
import org.switchyard.quickstarts.demos.library.types.LoanResponse;
import org.switchyard.quickstarts.demos.library.types.ReturnRequest;
import org.switchyard.quickstarts.demos.library.types.ReturnResponse;

public interface LoanProcess {

    public LoanResponse loanRequest(LoanRequest request);

    public ReturnResponse returnRequest(ReturnRequest request);

}
