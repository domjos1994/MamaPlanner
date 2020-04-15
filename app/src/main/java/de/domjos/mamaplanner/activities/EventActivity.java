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

package de.domjos.mamaplanner.activities;

import android.os.Build;
import android.view.View;
import android.widget.*;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.Objects;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.utils.WidgetUtils;
import de.domjos.mamaplanner.R;
import de.domjos.mamaplanner.custom.CustomSpinnerAdapter;
import de.domjos.mamaplanner.model.calendar.CalendarEvent;
import de.domjos.mamaplanner.model.calendar.Notification;
import de.domjos.mamaplanner.model.family.Family;
import de.domjos.mamaplanner.settings.Global;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public final class EventActivity extends AbstractActivity {
    private Spinner spEventAlias;
    private CustomSpinnerAdapter<String> aliasAdapter;
    private TextView lblEventDate;
    private EditText txtEventName;
    private EditText txtEventDescription;
    private TimePicker tpEventStart, tpEventEnd;
    private CheckBox chkEventWholeDay;
    private TableLayout tblNotifications;

    private CalendarEvent event;
    private Family currentFamily;

    private Validator eventValidator;

    public EventActivity() {
        super(R.layout.event_activity);
    }

    @Override
    protected void initActions() {

        this.chkEventWholeDay.setOnCheckedChangeListener((compoundButton, b) -> {
            int state = b ? GONE : VISIBLE;
            this.tpEventStart.setVisibility(state);
            this.tpEventEnd.setVisibility(state);
        });

        this.spEventAlias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String alias = aliasAdapter.getItem(i);
                   if(alias != null) {
                       if(alias.trim().isEmpty()) {
                           currentFamily = null;
                           lblEventDate.setBackgroundColor(WidgetUtils.getColor(getApplicationContext(), android.R.color.transparent));
                       } else {
                           currentFamily = MainActivity.GLOBAL.getSqLite().getFamily("alias='" + alias + "'").get(0);
                           lblEventDate.setBackgroundColor(currentFamily.getColor());
                       }
                   }
                } catch (Exception ignored) {}
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    @Override
    protected void initControls() {
        try {
            long id = this.getIntent().getLongExtra(MainActivity.ID, 0);
            String date = this.getIntent().getStringExtra(MainActivity.DATE);
            if(id==0L) {
                this.event = new CalendarEvent();
                this.event.setCalendar(Objects.requireNonNull(ConvertHelper.convertStringToDate(date, Global.getDateFormat(getApplicationContext()))));
                if(!this.getIntent().getBooleanExtra(MainActivity.WHOLE_DAY, true)) {
                    Calendar end = (Calendar) this.event.getCalendar().clone();
                    end.add(Calendar.HOUR_OF_DAY, 1);
                    this.event.setEnd(end.getTime());
                }
            } else {
                this.event = MainActivity.GLOBAL.getSqLite().getEvents("ID=" + id).get(0);
            }
            this.initTabHost();

            BottomNavigationView navigation = this.findViewById(R.id.navigation);
            navigation.getMenu().findItem(R.id.navSysAdd).setVisible(false);
            navigation.getMenu().findItem(R.id.navSysEdit).setVisible(false);
            navigation.setOnNavigationItemSelectedListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.navSysCancel:
                        setResult(RESULT_OK);
                        finish();
                        break;
                    case R.id.navSysSave:
                        if(this.eventValidator.getState()) {
                            this.fieldsToObject();
                            MainActivity.GLOBAL.getSqLite().insertOrUpdateEvent(this.event, this.currentFamily);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            MessageHelper.printMessage(this.eventValidator.getResult(), R.mipmap.ic_launcher_round, EventActivity.this);
                        }
                        break;
                }
                return false;
            });

            this.lblEventDate = this.findViewById(R.id.lblEventDate);
            this.lblEventDate.setText(date);
            if(this.currentFamily != null) {
                this.lblEventDate.setBackgroundColor(this.currentFamily.getColor());
            }

            this.spEventAlias = this.findViewById(R.id.spEventAlias);
            this.aliasAdapter = new CustomSpinnerAdapter<>(this.getApplicationContext());
            this.spEventAlias.setAdapter(this.aliasAdapter);
            this.aliasAdapter.notifyDataSetChanged();
            try {
                this.reloadFamilyMember();
            } catch (Exception ignored) {}
            long family = this.getIntent().getLongExtra(MainActivity.FAMILY, 0L);
            if(family==0L) {
                this.currentFamily = null;
                this.spEventAlias.setSelection(this.aliasAdapter.getPosition(""));
            } else {
                this.currentFamily = MainActivity.GLOBAL.getSqLite().getFamily("ID=" + family).get(0);
                this.spEventAlias.setSelection(this.aliasAdapter.getPosition(currentFamily.getAlias()));
            }

            this.chkEventWholeDay = this.findViewById(R.id.chkEventWholeDay);
            this.tpEventStart = this.findViewById(R.id.tpEventStart);
            this.tpEventEnd = this.findViewById(R.id.tpEventEnd);

            this.txtEventName = this.findViewById(R.id.txtEventName);
            this.txtEventDescription = this.findViewById(R.id.txtEventDescription);
            this.tblNotifications = this.findViewById(R.id.tblNotifications);
            this.objectToFields();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, EventActivity.this);
        }
    }

    private void initTabHost() {
        TabHost tabHost = this.findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec tabSpec1 = tabHost.newTabSpec(this.getString(R.string.app_event));
        tabSpec1.setIndicator(this.getString(R.string.app_event));
        tabSpec1.setContent(R.id.tab1);
        tabHost.addTab(tabSpec1);

        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec(this.getString(R.string.notifications));
        tabSpec2.setIndicator(this.getString(R.string.notifications));
        tabSpec2.setContent(R.id.tab2);
        tabHost.addTab(tabSpec2 );

        tabHost.setCurrentTab(0);
    }

    @Override
    public void initValidator() {
        this.eventValidator = new Validator(this.getApplicationContext(), R.mipmap.ic_launcher_round);
        this.eventValidator.addEmptyValidator(this.txtEventName);
        this.eventValidator.addEmptyValidator(this.txtEventDescription);
    }

    private void objectToFields() {
        if(this.event == null) {
            this.event = new CalendarEvent();
        }

        if(this.event.getEnd()==null) {
            this.chkEventWholeDay.setChecked(true);
        } else {
            this.chkEventWholeDay.setChecked(false);
            this.tpEventStart.setVisibility(VISIBLE);
            this.tpEventEnd.setVisibility(VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.tpEventStart.setHour(this.event.getCalendar().get(Calendar.HOUR_OF_DAY));
                this.tpEventStart.setMinute(this.event.getCalendar().get(Calendar.MINUTE));
                this.tpEventEnd.setHour(this.event.getEnd().get(Calendar.HOUR_OF_DAY));
                this.tpEventEnd.setMinute(this.event.getEnd().get(Calendar.MINUTE));
            } else {
                this.tpEventStart.setCurrentHour(this.event.getCalendar().get(Calendar.HOUR_OF_DAY));
                this.tpEventStart.setCurrentMinute(this.event.getCalendar().get(Calendar.MINUTE));
                this.tpEventEnd.setCurrentHour(this.event.getEnd().get(Calendar.HOUR_OF_DAY));
                this.tpEventEnd.setCurrentMinute(this.event.getEnd().get(Calendar.MINUTE));
            }
        }
        this.txtEventName.setText(this.event.getName());
        this.txtEventDescription.setText(this.event.getDescription());

        int i = 1;
        for(Notification notification : this.event.getNotifications()) {
            if(this.tblNotifications.getChildCount()>=i) {
                TableRow tableRow = (TableRow) this.tblNotifications.getChildAt(i - 1);
                ((EditText) tableRow.getChildAt(0)).setText(String.valueOf(notification.getMonths()));
                ((EditText) tableRow.getChildAt(1)).setText(String.valueOf(notification.getDays()));
                ((EditText) tableRow.getChildAt(2)).setText(String.valueOf(notification.getHours()));
            } else {
                break;
            }
            i++;
        }
    }

    private void fieldsToObject() {
        try {
            if(this.event==null) {
                this.event = new CalendarEvent();
            }
            this.event.setCalendar(ConvertHelper.convertStringToDate(this.lblEventDate.getText().toString(), Global.getDateTimeFormat(getApplicationContext())));
            if(this.currentFamily != null) {
                this.event.setColor(this.currentFamily.getColor());
            }
            if(this.chkEventWholeDay.isChecked()) {
                this.event.setEnd(null);
            } else {
                Calendar calendar = this.event.getCalendar();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    calendar.set(Calendar.HOUR_OF_DAY, this.tpEventStart.getHour());
                    calendar.set(Calendar.MINUTE, this.tpEventStart.getMinute());
                    this.event.setCalendar(calendar.getTime());
                    calendar.set(Calendar.HOUR_OF_DAY, this.tpEventEnd.getHour());
                    calendar.set(Calendar.MINUTE, this.tpEventEnd.getMinute());
                    this.event.setEnd(calendar.getTime());
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, this.tpEventStart.getCurrentHour());
                    calendar.set(Calendar.MINUTE, this.tpEventStart.getCurrentMinute());
                    this.event.setCalendar(calendar.getTime());
                    calendar.set(Calendar.HOUR_OF_DAY, this.tpEventEnd.getCurrentHour());
                    calendar.set(Calendar.MINUTE, this.tpEventEnd.getCurrentMinute());
                    this.event.setEnd(calendar.getTime());
                }
            }
            this.event.setName(this.txtEventName.getText().toString());
            this.event.setDescription(this.txtEventDescription.getText().toString());

            this.event.getNotifications().clear();
            for(int i = 0; i<=this.tblNotifications.getChildCount()-1; i++) {
                TableRow tableRow = (TableRow) this.tblNotifications.getChildAt(i);

                String months = ((EditText) tableRow.getChildAt(0)).getText().toString();
                String days = ((EditText) tableRow.getChildAt(1)).getText().toString();
                String hours = ((EditText) tableRow.getChildAt(2)).getText().toString();

                if(!months.trim().isEmpty() || !days.trim().isEmpty() || !hours.trim().isEmpty()) {
                    Notification notification = new Notification();
                    notification.setMonths(months.trim().isEmpty() ? 0 : Integer.parseInt(months.trim()));
                    notification.setDays(days.trim().isEmpty() ? 0 : Integer.parseInt(days.trim()));
                    notification.setHours(hours.trim().isEmpty() ? 0 : Integer.parseInt(hours.trim()));
                    this.event.getNotifications().add(notification);
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, EventActivity.this);
        }
    }

    private void reloadFamilyMember() throws Exception {
        this.aliasAdapter.clear();
        this.aliasAdapter.add("");
        for(Family family : MainActivity.GLOBAL.getSqLite().getFamily("")) {
            this.aliasAdapter.add(family.getAlias());
        }
    }
}
