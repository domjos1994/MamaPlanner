/*
 * MamaPlanner
 * Copyright (C) 2019 Domjos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.customwidgets.widgets.calendar;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Calendar;

public abstract class Event {
    private long id;
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

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
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

    public abstract int getIcon();

    @Override
    @NonNull
    public String toString() {
        return this.getName();
    }
}
