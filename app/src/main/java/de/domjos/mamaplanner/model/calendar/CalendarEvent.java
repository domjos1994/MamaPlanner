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

package de.domjos.mamaplanner.model.calendar;

import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.widgets.calendar.Event;
import de.domjos.mamaplanner.R;
import de.domjos.mamaplanner.model.family.Family;
import de.domjos.mamaplanner.model.objects.IDatabaseObject;

public final class CalendarEvent extends Event implements IDatabaseObject {
    private long ID, timeStamp;
    private Family family;
    private List<Notification> notifications;
    private boolean system;

    public CalendarEvent() {
        super();

        this.ID = 0L;
        this.timeStamp = 0L;
        this.notifications = new LinkedList<>();
        this.system = false;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_event;
    }

    @Override
    public void setID(long ID) {
        this.ID = ID;
    }

    @Override
    public long getID() {
        return this.ID;
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
        return "events";
    }

    public Family getFamily() {
        return this.family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public List<Notification> getNotifications() {
        return this.notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public boolean isSystem() {
        return this.system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }
}
