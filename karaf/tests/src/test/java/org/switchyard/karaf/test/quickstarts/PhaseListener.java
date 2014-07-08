package org.switchyard.karaf.test.quickstarts;

public interface PhaseListener {

    public static enum Phase {
        CREATE_SYSTEM,
        CREATE_CONTAINER,
        START_CONTAINER,
        CREATE_PROBE,
        ADD_TESTS,
        BUILD_PROBE,
        INSTALL_PROBE,
        EXECUTE_PROBE;
    }

    public void post(Phase phase) throws Exception;

}
