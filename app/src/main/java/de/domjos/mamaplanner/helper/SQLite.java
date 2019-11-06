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

package de.domjos.mamaplanner.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.WidgetUtils;
import de.domjos.customwidgets.widgets.calendar.Event;
import de.domjos.mamaplanner.R;
import de.domjos.mamaplanner.model.calendar.CalendarEvent;
import de.domjos.mamaplanner.model.calendar.Notification;
import de.domjos.mamaplanner.model.family.Family;
import de.domjos.mamaplanner.model.objects.IDatabaseObject;
import de.domjos.mamaplanner.settings.Global;

public class SQLite extends SQLiteOpenHelper {
    private Context context;

    public SQLite(Context context) {
        super(context, "mamaPlanner.db", null, Global.getVersion(context));
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            String content = WidgetUtils.getRaw(this.context, R.raw.init);
            for(String query : content.split(";")) {
                sqLiteDatabase.execSQL(query.trim());
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, this.context);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            String content = WidgetUtils.getRaw(this.context, R.raw.init);
            for(String query : content.split(";")) {
                sqLiteDatabase.execSQL(query.trim());
            }

            content = WidgetUtils.getRaw(this.context, R.raw.update);
            for(String query : content.split(";")) {
                sqLiteDatabase.execSQL(query.trim());
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, this.context);
        }
    }

    public void insertOrUpdateFamily(Family family) throws Exception {
        SQLiteStatement sqLiteStatement = this.getStatement(family, Arrays.asList("firstName", "lastName", "birthDate", "gender", "profilePicture", "color"));
        sqLiteStatement.bindString(1, family.getFirstName());
        sqLiteStatement.bindString(2, family.getLastName());
        sqLiteStatement.bindString(3, Converter.convertDateToString(family.getBirthDate(), Global.getDateFormat()));
        sqLiteStatement.bindString(4, family.getGender());
        if(family.getProfilePicture()!=null) {
            sqLiteStatement.bindBlob(5, family.getProfilePicture());
        } else {
            sqLiteStatement.bindNull(5);
        }
        sqLiteStatement.bindLong(6, family.getColor());
        family = (Family) this.execute(sqLiteStatement, family);

        this.initProgrammedEvents(family);
    }

    private void initProgrammedEvents(Family family) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(family.getBirthDate());

        Calendar tmpCalendar = calendar;
        CalendarEvent calendarEvent = new CalendarEvent();
        calendarEvent.setName(this.context.getString(R.string.events_vaccinations));
        calendarEvent.setDescription(this.context.getString(R.string.events_vaccinations_1));
        tmpCalendar.add(Calendar.WEEK_OF_YEAR, 6);
        calendarEvent.setCalendar(tmpCalendar.getTime());
        this.insertOrUpdateEvent(calendarEvent, family);

        tmpCalendar = calendar;
        calendarEvent = new CalendarEvent();
        calendarEvent.setName(this.context.getString(R.string.events_vaccinations));
        calendarEvent.setDescription(this.context.getString(R.string.events_vaccinations_2));
        tmpCalendar.add(Calendar.MONTH, 2);
        calendarEvent.setCalendar(tmpCalendar.getTime());
        this.insertOrUpdateEvent(calendarEvent, family);

        tmpCalendar = calendar;
        calendarEvent = new CalendarEvent();
        calendarEvent.setName(this.context.getString(R.string.events_vaccinations));
        calendarEvent.setDescription(this.context.getString(R.string.events_vaccinations_3));
        tmpCalendar.add(Calendar.MONTH, 3);
        calendarEvent.setCalendar(tmpCalendar.getTime());
        this.insertOrUpdateEvent(calendarEvent, family);

        tmpCalendar = calendar;
        calendarEvent = new CalendarEvent();
        calendarEvent.setName(this.context.getString(R.string.events_vaccinations));
        calendarEvent.setDescription(this.context.getString(R.string.events_vaccinations_4));
        tmpCalendar.add(Calendar.MONTH, 4);
        calendarEvent.setCalendar(tmpCalendar.getTime());
        this.insertOrUpdateEvent(calendarEvent, family);

        tmpCalendar = calendar;
        calendarEvent = new CalendarEvent();
        calendarEvent.setName(this.context.getString(R.string.events_vaccinations));
        calendarEvent.setDescription(this.context.getString(R.string.events_vaccinations_5));
        tmpCalendar.add(Calendar.MONTH, 11);
        calendarEvent.setCalendar(tmpCalendar.getTime());
        this.insertOrUpdateEvent(calendarEvent, family);

        tmpCalendar = calendar;
        calendarEvent = new CalendarEvent();
        calendarEvent.setName(this.context.getString(R.string.events_vaccinations));
        calendarEvent.setDescription(this.context.getString(R.string.events_vaccinations_6));
        tmpCalendar.add(Calendar.MONTH, 15);
        calendarEvent.setCalendar(tmpCalendar.getTime());
        this.insertOrUpdateEvent(calendarEvent, family);
    }

    public List<Family> getFamily(String where) throws Exception {
        List<Family> families = new LinkedList<>();
        Cursor cursor = this.getCursor(new Family(), where);
        while (cursor.moveToNext()) {
            Family family = new Family();
            family.setID(cursor.getInt(cursor.getColumnIndex("ID")));
            family.setFirstName(cursor.getString(cursor.getColumnIndex("firstName")));
            family.setLastName(cursor.getString(cursor.getColumnIndex("lastName")));
            family.setBirthDate(Converter.convertStringToDate(cursor.getString(cursor.getColumnIndex("birthDate")), Global.getDateFormat()));
            family.setGender(cursor.getString(cursor.getColumnIndex("gender")));
            family.setProfilePicture(cursor.getBlob(cursor.getColumnIndex("profilePicture")));
            family.setColor(cursor.getInt(cursor.getColumnIndex("color")));
            family.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timeStamp")));
            families.add(family);
        }
        cursor.close();
        return families;
    }

    public void insertOrUpdateEvent(CalendarEvent event, Family family) {
        SQLiteStatement sqLiteStatement = this.getStatement(event, Arrays.asList("name", "description", "family", "start", "end"));
        sqLiteStatement.bindString(1, event.getName());
        sqLiteStatement.bindString(2, event.getDescription());
        if(family == null) {
            sqLiteStatement.bindLong(3, 0);
        } else {
            sqLiteStatement.bindLong(3, family.getID());
        }
        sqLiteStatement.bindLong(4, event.getCalendar().getTime().getTime());
        if(event.getEnd()!=null) {
            sqLiteStatement.bindLong(5, event.getEnd().getTime().getTime());
        } else {
            sqLiteStatement.bindNull(5);
        }
        CalendarEvent calendarEvent = (CalendarEvent) this.execute(sqLiteStatement, event);
        this.deleteItem(new Notification(), "event=" + calendarEvent.getID());
        for(Notification notification : calendarEvent.getNotifications()) {
            this.insertOrUpdateNotification(notification, calendarEvent);
        }
    }

    public List<CalendarEvent> getEvents(String where) throws Exception {
        List<CalendarEvent> events = new LinkedList<>();
        Cursor cursor = this.getCursor(new CalendarEvent(), where);
        while (cursor.moveToNext()) {
            CalendarEvent calendarEvent = new CalendarEvent();
            calendarEvent.setID(cursor.getInt(cursor.getColumnIndex("ID")));
            calendarEvent.setName(cursor.getString(cursor.getColumnIndex("name")));
            calendarEvent.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            long deadline = cursor.getLong(cursor.getColumnIndex("start"));
            Date date = new Date();
            date.setTime(deadline);
            calendarEvent.setCalendar(date);

            long end = cursor.getLong(cursor.getColumnIndex("end"));
            if(end != 0) {
                Date endDate = new Date();
                endDate.setTime(end);
                calendarEvent.setEnd(endDate);
            }

            calendarEvent.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timeStamp")));
            int familyID = cursor.getInt(cursor.getColumnIndex("family"));
            if(familyID != 0) {
                Family family = this.getFamily("ID=" + familyID).get(0);
                calendarEvent.setFamily(family);
                calendarEvent.setColor(family.getColor());
            }
            calendarEvent.setNotifications(this.getNotifications("event=" + calendarEvent.getID()));
            events.add(calendarEvent);
        }
        return events;
    }

    private void insertOrUpdateNotification(Notification notification, CalendarEvent event) {
        SQLiteStatement sqLiteStatement = this.getStatement(notification, Arrays.asList("months", "days", "hours", "event"));
        sqLiteStatement.bindLong(1, notification.getMonths());
        sqLiteStatement.bindLong(2, notification.getDays());
        sqLiteStatement.bindLong(3, notification.getHours());
        sqLiteStatement.bindLong(4, event.getID());
        this.execute(sqLiteStatement, notification);
    }

    public List<Notification> getNotifications(String where) {
        List<Notification> notifications = new LinkedList<>();
        Cursor cursor = this.getCursor(new Notification(), where);
        while (cursor.moveToNext()) {
            Notification notification = new Notification();
            notification.setID(cursor.getLong(cursor.getColumnIndex("ID")));
            notification.setMonths(cursor.getInt(cursor.getColumnIndex("months")));
            notification.setDays(cursor.getInt(cursor.getColumnIndex("days")));
            notification.setHours(cursor.getInt(cursor.getColumnIndex("hours")));
            notification.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timeStamp")));
            notifications.add(notification);
        }
        return notifications;
    }

    public void deleteItem(IDatabaseObject iDatabaseObject) {
        this.getWritableDatabase().execSQL("DELETE FROM " + iDatabaseObject.getTable() + " WHERE ID=" + iDatabaseObject.getID());
    }

    public void deleteItem(IDatabaseObject iDatabaseObject, String where) {
        this.getWritableDatabase().execSQL("DELETE FROM " + iDatabaseObject.getTable() + " WHERE " + where);
    }

    private SQLiteStatement getStatement(IDatabaseObject iDatabaseObject, List<String> columns) {
        iDatabaseObject.setTimeStamp(new Date().getTime());

        SQLiteStatement sqLiteStatement;
        if(iDatabaseObject.getID() != 0) {
            String query = "UPDATE " + iDatabaseObject.getTable() + " SET " + this.join("=?, ", columns) + "=?, timeStamp=? WHERE ID=?";
            sqLiteStatement = this.getWritableDatabase().compileStatement(query);
            sqLiteStatement.bindLong(columns.size() + 2, iDatabaseObject.getID());
            sqLiteStatement.bindLong(columns.size() + 1, iDatabaseObject.getTimeStamp());
        } else {
            List<String> values = new LinkedList<>();
            for(int i = 0; i<=columns.size()-1; i++) {
                values.add("?");
            }
            String query = "INSERT INTO " + iDatabaseObject.getTable() + "(" + this.join(", ", columns) + ", timeStamp) VALUES(" + this.join(", ", values) + ", ?)";
            sqLiteStatement = this.getWritableDatabase().compileStatement(query);
            sqLiteStatement.bindLong(columns.size() + 1, iDatabaseObject.getTimeStamp());
        }
        return sqLiteStatement;
    }

    private Cursor getCursor(IDatabaseObject iDatabaseObject, String where) {
        return this.getReadableDatabase().rawQuery("SELECT * FROM " + iDatabaseObject.getTable() + (where.isEmpty() ? where : " WHERE " + where), null);
    }

    private IDatabaseObject execute(SQLiteStatement sqLiteStatement, IDatabaseObject iDatabaseObject) {
        if(iDatabaseObject.getID()==0) {
            iDatabaseObject.setID(sqLiteStatement.executeInsert());
        } else {
            sqLiteStatement.execute();
        }
        sqLiteStatement.close();
        return iDatabaseObject;
    }


    private String join(String splitter, List<String> items) {
        StringBuilder stringBuilder = new StringBuilder();
        for(String item : items) {
            stringBuilder.append(item);
            stringBuilder.append(splitter);
        }

        return stringBuilder.substring(0, stringBuilder.lastIndexOf(splitter));
    }
}