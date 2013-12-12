package org.switchyard.quickstarts.bpel.xts.wsat.ws;

/**
 * 
 * Airport manager checks by flight identifier that the flight has available
 * tickets.
 * 
 */
public final class AirportManager {

    /**
     * Cities which can be used to have flight.
     */
    public static final String[] CITIES = {"Brno", "Prague", "London" };

    private AirportManager() {

    }

    /**
     * Check flight identifier. 
     * @param fltid flight identifier
     * @return true if flight is valid and available, otherwise returns false 
     */
    public static boolean checkFLTID(String fltid) {
        boolean state = false;
        String[] parts = fltid.split("/");
        if (parts.length != 4) {
            return state;
        }
        for (int i = 0; i < 2; i++) {
            state = false;
            for (String city : CITIES) {
                if (parts[i].compareTo(city) == 0) {
                    state = true;
                    break;
                }
            }
            if (!state) {
                return state;
            }
        }
        return state;
    }

}
