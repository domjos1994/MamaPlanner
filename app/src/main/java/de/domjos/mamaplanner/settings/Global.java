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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import de.domjos.mamaplanner.helper.SQLite;

public class Global {
    private SQLite sqLite;


    public static int getVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException ignored) {}
        return 0;
    }

    public static String getDateFormat() {
        return "yyyy-MM-dd HH:mm:ss";
    }

    public SQLite getSqLite() {
        return this.sqLite;
    }

    public void setSqLite(SQLite sqLite) {
        this.sqLite = sqLite;
    }
}
