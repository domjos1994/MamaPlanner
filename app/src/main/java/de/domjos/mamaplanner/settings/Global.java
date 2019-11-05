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
