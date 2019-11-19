package de.domjos.mamaplanner.services.caldav;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import de.domjos.mamaplanner.settings.Global;

public class CalDavService extends IntentService {

    public CalDavService() {
        super(CalDavService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String user = Global.getSettingFromPreference(CalDavCredentials.CAL_USER, "", this.getApplicationContext());
        String pwd = Global.getSettingFromPreference(CalDavCredentials.CAL_PWD, "", this.getApplicationContext());
        String host = Global.getSettingFromPreference(CalDavCredentials.CAL_HOST, "", this.getApplicationContext());
        String base = Global.getSettingFromPreference(CalDavCredentials.CAL_BASE, "", this.getApplicationContext());

        CalDavCredentials calDavCredentials = new CalDavCredentials(user, pwd, host, base);

        CalDavSync calDavSync = new CalDavSync(calDavCredentials);
        if(calDavSync.test()) {
            calDavSync.sync();
        }
    }
}
