package de.domjos.customwidgets.widgets.calendar;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Calendar;

public class Event {
    private Calendar calendar;
    private String name;
    private String description;
    private int color;

    public Event() {
        this(new Date(), "", "", -1);
    }

    public Event(Date dt, String name, String description, int color) {
        this.setCalendar(dt);
        this.name = name;
        this.description = description;
        this.color = color;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Date date) {
        this.calendar = Calendar.getInstance();
        this.calendar.setTime(date);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    @NonNull
    public String toString() {
        return this.getName();
    }
}
