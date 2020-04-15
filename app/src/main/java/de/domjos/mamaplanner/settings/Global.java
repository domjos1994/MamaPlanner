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

package de.domjos.mamaplanner.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import de.domjos.mamaplanner.R;
import de.domjos.mamaplanner.helper.SQLite;

import static android.content.Context.MODE_PRIVATE;

public class Global {
    private SQLite sqLite;
    private static final String DATE = "txtSettingsFormatDate", TIME = "txtSettingsFormatTime";

    public static int getVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException ignored) {}
        return 0;
    }

    public static String getDateTimeFormat(Context context) {
        return getDateFormat(context) + " " + getTimeFormat(context);
    }

    private static String getTimeFormat(Context context) {
        return Global.getSettingFromPreference(Global.TIME, context.getString(R.string.app_settings_format_time_default), context);
    }

    public static String getDateFormat(Context context) {
        return Global.getSettingFromPreference(Global.DATE, context.getString(R.string.app_settings_format_date_default), context);
    }

    public SQLite getSqLite() {
        return this.sqLite;
    }

    public void setSqLite(SQLite sqLite) {
        this.sqLite = sqLite;
    }

    public static void setSettingToPreference(String key, String value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSettingFromPreference(String key, String def, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        return sharedPreferences.getString(key, def);
    }
}
