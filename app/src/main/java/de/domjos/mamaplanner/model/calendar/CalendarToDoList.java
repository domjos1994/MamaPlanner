package de.domjos.mamaplanner.model.calendar;

import de.domjos.customwidgets.widgets.calendar.Event;
import de.domjos.mamaplanner.R;
import de.domjos.mamaplanner.model.objects.IDatabaseObject;

public final class CalendarToDoList extends Event implements IDatabaseObject {
    private long ID, timeStamp;

    public CalendarToDoList() {
        super();

        this.ID = 0L;
        this.timeStamp = 0L;
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_todo;
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
        return "todoLists";
    }
}
