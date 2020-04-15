package de.domjos.mamaplanner.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class SQLiteUpdate {

    /**
     * Update to Version 2 of Database
     * @param version the old Version
     * @return the updated Version
     */
    public static int update1(int version, SQLiteDatabase db) {
        if(version==1) {

            version++;
        }
        return version;
    }

    /**
     * Update to Version 3 of Database
     * @param version the old Version
     * @return the updated Version
     */
    public static int update2(int version, SQLiteDatabase db) throws Exception {
        if(version==2) {
            addColumnIfNotExists(db, "events", "system", Types.TINYINT, 1, "0");
            version++;
        }
        return version;
    }

    public static int update13(int version, SQLiteDatabase db) throws Exception {
        if(version==13) {
            addColumnIfNotExists(db, "family", "alias", Types.VARCHAR, 5, "");
            version++;
        }
        return version;
    }

    private static void addColumnIfNotExists(SQLiteDatabase db, String table, String column, int type, int length, String defaultValue) throws Exception {
        if(columnNotExists(db, table, column)) {
            Map<Integer, String> types = getAllJDBCTypeNames();
            String typeString = types.get(type);
            if(typeString!=null) {
                if(typeString.toLowerCase().equals("varchar")) {
                    typeString += "(" + length + ")";
                }
            } else {
                return;
            }
            if(!defaultValue.equals("")) {
                typeString += " DEFAULT " + defaultValue;
            }

            db.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s %s", table, column, typeString));
        }
    }

    private static boolean columnNotExists(SQLiteDatabase db, String table, String column) {
        boolean exists = false;
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null);
        while (cursor.moveToNext()) {
            if(cursor.getString(1).equals(column)) {
                exists = true;
                break;
            }
        }
        cursor.close();
        return !exists;
    }

    private static Map<Integer, String> getAllJDBCTypeNames() throws  Exception {

        Map<Integer, String> result = new LinkedHashMap<>();

        for (Field field : Types.class.getFields()) {
            result.put(field.getInt(null), field.getName());
        }

        return result;
    }
}
