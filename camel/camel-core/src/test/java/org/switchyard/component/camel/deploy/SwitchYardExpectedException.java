package org.switchyard.component.camel.deploy;

import static org.junit.matchers.JUnitMatchers.containsString;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;
import org.switchyard.HandlerException;
import org.switchyard.test.InvocationFaultException;

public class SwitchYardExpectedException implements TestRule {

    private final ExpectedException _delegate = ExpectedException.none();

    private SwitchYardExpectedException() {
    }

    public static SwitchYardExpectedException none() {
        return new SwitchYardExpectedException();
    }

    public Statement apply(Statement base, org.junit.runner.Description desc) {
        return _delegate.apply(base, desc);
    }

    public void expect(Class<? extends Throwable> type) {
        _delegate.expect(new ExceptionCauseMatcher(type));
    }

    public void expectMessage(String message) {
        expectMessage(containsString(message));
    }

    /**
     * Adds {@code matcher} to the list of requirements for the message
     * returned from any thrown exception.
     */
    public void expectMessage(Matcher<String> matcher) {
        _delegate.expect(hasMessage(matcher));
    }

    private Matcher<Throwable> hasMessage(final Matcher<String> matcher) {
        return new TypeSafeMatcher<Throwable>() {
            public void describeTo(Description description) {
                description.appendText("exception with message ");
                description.appendDescriptionOf(matcher);
            }

            @Override
            public boolean matchesSafely(Throwable item) {
                final Throwable throwable = getCauseFromHandlerException(item);
                return matcher.matches(throwable.getMessage());
            }
        };
    }

    private class ExceptionCauseMatcher extends BaseMatcher<Throwable> {

        private Class<? extends Throwable> expectedClass;

        public ExceptionCauseMatcher(final Class<? extends Throwable> expectedClass) {
            this.expectedClass = expectedClass;
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("Exception.getCause() should be [" + expectedClass.toString() + "]");
        }

        @Override
        public boolean matches(final Object obj) {
            if (!(obj instanceof Exception)) {
                return false;
            }

            final Exception e = (Exception) obj;
            final Throwable thrown = getCauseFromHandlerException(e);
            return expectedClass.equals(thrown.getClass());
        }

    }

    public static Throwable getCauseFromHandlerException(final Throwable thrown) {
        if (thrown instanceof InvocationFaultException) {
            final InvocationFaultException faultException = (InvocationFaultException) thrown;
            final HandlerException handlerException = (HandlerException) faultException.getCause();
            final Throwable cause = handlerException.getCause();
            return cause;
        }
        return thrown;
    }

}