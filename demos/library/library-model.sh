#!/bin/sh

cd target/classes

rm -f ../library-model.jar

jar cvf ../library-model.jar \
META-INF/kmodule.xml \
org/switchyard/quickstarts/demos/library/Library.class \
org/switchyard/quickstarts/demos/library/types/Book.class \
org/switchyard/quickstarts/demos/library/types/Loan.class \
org/switchyard/quickstarts/demos/library/types/LoanRequest.class \
org/switchyard/quickstarts/demos/library/types/LoanResponse.class \
org/switchyard/quickstarts/demos/library/types/ObjectFactory.class \
org/switchyard/quickstarts/demos/library/types/ReturnRequest.class \
org/switchyard/quickstarts/demos/library/types/ReturnResponse.class \
org/switchyard/quickstarts/demos/library/types/Suggestion.class \
org/switchyard/quickstarts/demos/library/types/SuggestionRequest.class \
org/switchyard/quickstarts/demos/library/types/SuggestionResponse.class

cd ../..

