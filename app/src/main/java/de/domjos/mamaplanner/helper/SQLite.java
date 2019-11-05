package de.domjos.mamaplanner.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.utils.Converter;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.WidgetUtils;
import de.domjos.mamaplanner.R;
import de.domjos.mamaplanner.model.calendar.CalendarEvent;
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

    public long insertOrUpdateFamily(Family family) throws Exception {
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
        return this.execute(sqLiteStatement, family).getID();
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

    public long insertOrUpdateEvent(CalendarEvent event, Family family) {
        SQLiteStatement sqLiteStatement = this.getStatement(event, Arrays.asList("name", "description", "family", "deadLine"));
        sqLiteStatement.bindString(1, event.getName());
        sqLiteStatement.bindString(2, event.getDescription());
        if(family == null) {
            sqLiteStatement.bindLong(3, 0);
        } else {
            sqLiteStatement.bindLong(3, family.getID());
        }
        sqLiteStatement.bindLong(4, event.getCalendar().getTime().getTime());
        return this.execute(sqLiteStatement, event).getID();
    }

    public List<CalendarEvent> getEvents(String where) throws Exception {
        List<CalendarEvent> events = new LinkedList<>();
        Cursor cursor = this.getCursor(new CalendarEvent(), where);
        while (cursor.moveToNext()) {
            CalendarEvent calendarEvent = new CalendarEvent();
            calendarEvent.setID(cursor.getInt(cursor.getColumnIndex("ID")));
            calendarEvent.setName(cursor.getString(cursor.getColumnIndex("name")));
            calendarEvent.setName(cursor.getString(cursor.getColumnIndex("description")));
            long deadline = cursor.getLong(cursor.getColumnIndex("deadLine"));
            Date date = new Date();
            date.setTime(deadline);
            calendarEvent.setCalendar(date);
            calendarEvent.setTimeStamp(cursor.getLong(cursor.getColumnIndex("timeStamp")));
            int familyID = cursor.getInt(cursor.getColumnIndex("family"));
            if(familyID != 0) {
                Family family = this.getFamily("ID=" + familyID).get(0);
                calendarEvent.setFamily(family);
                calendarEvent.setColor(family.getColor());
            }
            events.add(calendarEvent);
        }
        return events;
    }

    public void deleteItem(IDatabaseObject iDatabaseObject) {
        this.getWritableDatabase().execSQL("DELETE FROM " + iDatabaseObject.getTable() + " WHERE ID=" + iDatabaseObject.getID());
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
