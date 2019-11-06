package de.domjos.mamaplanner.model.calendar;

import de.domjos.mamaplanner.model.objects.IDatabaseObject;

public final class Notification implements IDatabaseObject {
    private long id, timeStamp;
    private int months;
    private int days;
    private int hours;

    public Notification() {
        super();

        this.id = 0;
        this.timeStamp = 0;
        this.months = 0;
        this.days = 0;
        this.hours = 0;
    }

    @Override
    public void setID(long ID) {
        this.id = ID;
    }

    @Override
    public long getID() {
        return this.id;
    }

    @Override
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }

    @Override
    public String getTable() {
        return "notifications";
    }

    public int getMonths() {
        return this.months;
    }

    public void setMonths(int months) {
        this.months = months;
    }

    public int getDays() {
        return this.days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getHours() {
        return this.hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
}
