package org.switchyard.quickstarts.remoteinvoker;

public class Deal {

    private Offer offer;
    private boolean accepted;

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

}
