package com.example.sample.providerapp.Model;

/**
 * Created by Hassan Javaid on 9/10/2018.
 */

public class Booking {
    String uid;
    int acceptedRequest;
    int rejectedRequest;
    int cancelBooking;
    String jobStatus;

    public Booking() {
    }

    public Booking(String uid, int acceptedRequest, int rejectedRequest, int cancelBooking, String jobStatus) {
        this.uid = uid;
        this.acceptedRequest = acceptedRequest;
        this.rejectedRequest = rejectedRequest;
        this.cancelBooking = cancelBooking;
        this.jobStatus = jobStatus;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getAcceptedRequest() {
        return acceptedRequest;
    }

    public void setAcceptedRequest(int acceptedRequest) {
        this.acceptedRequest = acceptedRequest;
    }

    public int getRejectedRequest() {
        return rejectedRequest;
    }

    public void setRejectedRequest(int rejectedRequest) {
        this.rejectedRequest = rejectedRequest;
    }

    public int getCancelBooking() {
        return cancelBooking;
    }

    public void setCancelBooking(int cancelBooking) {
        this.cancelBooking = cancelBooking;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }
}
