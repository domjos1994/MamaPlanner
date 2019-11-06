package de.domjos.mamaplanner.helper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class Helper {

    public static void initRepeatingService(Activity activity, Class<? extends Service> cls, long frequency) {
        // init Service
        Intent intent = new Intent(activity.getApplicationContext(), cls);
        PendingIntent pendingIntent1 = PendingIntent.getService(activity,  0, intent, 0);

        // init frequently
        AlarmManager alarmManager1 = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager1 != null;
        alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), frequency, pendingIntent1);
    }
}
