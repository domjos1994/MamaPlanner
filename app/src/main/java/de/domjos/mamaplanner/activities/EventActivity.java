package de.domjos.mamaplanner.activities;

import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.Converter;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.mamaplanner.R;
import de.domjos.mamaplanner.helper.Validator;
import de.domjos.mamaplanner.model.calendar.CalendarEvent;
import de.domjos.mamaplanner.model.family.Family;
import de.domjos.mamaplanner.settings.Global;

public final class EventActivity extends AbstractActivity {
    private TextView lblEventDate;
    private EditText txtEventName;
    private EditText txtEventDescription;

    private CalendarEvent event;
    private Family currentFamily;

    private Validator eventValidator;

    public EventActivity() {
        super(R.layout.event_activity);
    }

    @Override
    protected void initActions() {

    }

    @Override
    protected void initControls() {
        try {
            long id = this.getIntent().getLongExtra("ID", 0L);
            String date = this.getIntent().getStringExtra("DT");
            long family = this.getIntent().getLongExtra("FAMILY", 0L);
            if(family==0L) {
                this.currentFamily = null;
            } else {
                this.currentFamily = MainActivity.GLOBAL.getSqLite().getFamily("ID=" + family).get(0);
            }
            if(id==0L) {
                this.event = new CalendarEvent();
                this.event.setCalendar(Converter.convertStringToDate(date, Global.getDateFormat()));
            } else {
                this.event = MainActivity.GLOBAL.getSqLite().getEvents("ID=" + id).get(0);
            }

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

            this.txtEventName = this.findViewById(R.id.txtEventName);
            this.txtEventDescription = this.findViewById(R.id.txtEventDescription);
            this.objectToFields();
        } catch (Exception ex) {
            MessageHelper.printException(ex, EventActivity.this);
        }
    }

    @Override
    public void initValidators() {
        this.eventValidator = new Validator(this.getApplicationContext());
        this.eventValidator.addEmptyValidator(this.txtEventName);
        this.eventValidator.addEmptyValidator(this.txtEventDescription);
    }

    private void objectToFields() {
        if(this.event == null) {
            this.event = new CalendarEvent();
        }

        this.txtEventName.setText(this.event.getName());
        this.txtEventDescription.setText(this.event.getDescription());
    }

    private void fieldsToObject() {
        try {
            if(this.event==null) {
                this.event = new CalendarEvent();
            }
            this.event.setCalendar(Converter.convertStringToDate(this.lblEventDate.getText().toString(), Global.getDateFormat()));
            if(this.currentFamily != null) {
                this.event.setColor(this.currentFamily.getColor());
            }
            this.event.setName(this.txtEventName.getText().toString());
            this.event.setDescription(this.txtEventDescription.getText().toString());
        } catch (Exception ex) {
            MessageHelper.printException(ex, EventActivity.this);
        }
    }
}
