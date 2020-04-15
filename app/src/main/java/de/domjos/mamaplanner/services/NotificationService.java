package de.domjos.mamaplanner.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.mamaplanner.R;
import de.domjos.mamaplanner.activities.MainActivity;
import de.domjos.mamaplanner.model.calendar.CalendarEvent;
import de.domjos.mamaplanner.model.calendar.Notification;

public class NotificationService extends IntentService {

    public NotificationService() {
        super(NotificationService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH), day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            List<CalendarEvent> events = MainActivity.GLOBAL.getSqLite().getEvents("");
            List<CalendarEvent> todayEvents = new LinkedList<>();
            for(CalendarEvent calendarEvent : events) {
                for(Notification notification : calendarEvent.getNotifications()) {
                    Calendar not = this.calculateNotification(calendarEvent, notification);
                    int     eventYear = not.get(Calendar.YEAR),
                            eventMonth = not.get(Calendar.MONTH),
                            eventDay = not.get(Calendar.DAY_OF_MONTH),
                            eventHour = not.get(Calendar.HOUR_OF_DAY);

                    if(eventYear == year && eventMonth == month && eventDay == day) {
                        calendarEvent.getCalendar().set(Calendar.HOUR_OF_DAY, eventHour);
                        todayEvents.add(calendarEvent);
                    }
                    MainActivity.GLOBAL.getSqLite().deleteItem(notification);
                }
            }

            for(CalendarEvent todayEvent : todayEvents) {
                if(hour == todayEvent.getCalendar().get(Calendar.HOUR_OF_DAY)) {
                    MessageHelper.showNotification(getApplicationContext(), todayEvent.getName(), todayEvent.getDescription(), R.drawable.ic_event);
                }
            }
        } catch (Exception ex){
            Log.v("Exception", ex.toString());
        }
    }

    private Calendar calculateNotification(CalendarEvent calendarEvent, Notification notification) {
        Calendar calendar = calendarEvent.getCalendar();

        calendar.add(Calendar.MONTH, -notification.getMonths());
        calendar.add(Calendar.DAY_OF_YEAR, -notification.getDays());
        calendar.add(Calendar.HOUR_OF_DAY, -notification.getHours());
        return calendar;
    }
}
