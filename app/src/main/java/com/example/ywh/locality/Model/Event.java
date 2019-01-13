package com.example.ywh.locality.Model;

import java.util.Date;
import java.util.List;

public class Event {

    private String eventID;
    private String eventTitle;
    private String eventType;
    private String eventDesc;
    private String participantType;
    private long eventDate;
    private long startTime;
    private long endTime;
    private int participantAmt;
    private List<String> participant;
    private Address address;
    private String ownerEmail;

    public Event() {
    }

    public Event (String eventTitle, String eventType, String eventDesc, String participantType, int participantAmt){
        this.eventTitle = eventTitle;
        this.eventType = eventType;
        this.eventDesc = eventDesc;
        this.participantType = participantType;
        this.participantAmt = participantAmt;
    }
    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getParticipantType() {
        return participantType;
    }

    public void setParticipantType(String participantType) {
        this.participantType = participantType;
    }

    public long getEventDate() {
        return eventDate;
    }

    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    public void setStartTime(int startHrs, int startMin) {
        Date startDate = new Date();
        startDate.setHours(startHrs);
        startDate.setHours(startMin);
        this.startTime = startDate.getTime();
    }

    public void setEndTime(int endHrs, int endMin) {
        Date endDate = new Date();
        endDate.setHours(endHrs);
        endDate.setHours(endMin);
        this.endTime = endDate.getTime();
    }

    public int getParticipantAmt() {
        return participantAmt;
    }

    public void setParticipantAmt(int participantAmt) {
        this.participantAmt = participantAmt;
    }

    public Address getEventAddress() {
        return address;
    }

    public void setEventAddress(Address eventAddress) {
        this.address = eventAddress;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public List<String> getParticipant() {
        return participant;
    }

    public void setParticipant(List<String> participant) {
        this.participant = participant;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }
}
