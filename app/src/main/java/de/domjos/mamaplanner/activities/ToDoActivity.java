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

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.WidgetUtils;
import de.domjos.mamaplanner.R;
import de.domjos.mamaplanner.helper.Validator;
import de.domjos.mamaplanner.model.calendar.CalendarToDoList;
import de.domjos.mamaplanner.model.calendar.Notification;
import de.domjos.mamaplanner.model.calendar.ToDo;
import de.domjos.mamaplanner.model.family.Family;
import de.domjos.mamaplanner.settings.Global;

import java.util.Calendar;

public final class ToDoActivity extends AbstractActivity {
    private CalendarToDoList toDoList;
    private Family currentFamily;

    private TableLayout tblControls;
    private TableLayout tblNotifications;
    private EditText txtToDoTitle, txtToDoCategory, txtToDoDeadline, txtToDoDescription;

    private Validator toDoValidator;

    public ToDoActivity() {
        super(R.layout.todo_activity);
    }

    @Override
    protected void initActions() {}

    @Override
    protected void initControls() {
        try {
            long id = this.getIntent().getLongExtra(MainActivity.ID, 0);
            String date = this.getIntent().getStringExtra(MainActivity.DATE);
            long family = this.getIntent().getLongExtra(MainActivity.FAMILY, 0L);
            if(family==0L) {
                this.currentFamily = null;
            } else {
                this.currentFamily = MainActivity.GLOBAL.getSqLite().getFamily("ID=" + family).get(0);
            }
            if(id==0L) {
                this.toDoList = new CalendarToDoList();
                this.toDoList.setCalendar(ConvertHelper.convertStringToDate(date, Global.getDateFormat(getApplicationContext())));
                if(!this.getIntent().getBooleanExtra(MainActivity.WHOLE_DAY, true)) {
                    Calendar end = (Calendar) this.toDoList.getCalendar().clone();
                    end.add(Calendar.HOUR_OF_DAY, 1);
                    this.toDoList.setEnd(end.getTime());
                }
            } else {
                this.toDoList = MainActivity.GLOBAL.getSqLite().getToDoLists("ID=" + id).get(0);
            }
            this.initTabHost();

            this.tblControls = this.findViewById(R.id.tblControls);
            this.tblNotifications = this.findViewById(R.id.tblNotifications);
            this.txtToDoTitle = this.findViewById(R.id.txtToDoTitle);
            this.txtToDoCategory = this.findViewById(R.id.txtToDoCategory);
            this.txtToDoDeadline = this.findViewById(R.id.txtToDoDeadline);
            this.txtToDoDescription = this.findViewById(R.id.txtToDoDescription);

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
                        if(this.toDoValidator.getState()) {
                            this.fieldsToObject();
                            MainActivity.GLOBAL.getSqLite().insertOrUpdateToDoList(this.toDoList, this.currentFamily);
                            setResult(RESULT_OK);
                            finish();
                        }
                        break;
                }
                return false;
            });

            this.addTableRow();
            this.objectToFields();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ToDoActivity.this);
        }
    }

    @Override
    public void initValidator() {
        this.toDoValidator = new Validator(this.getApplicationContext());
        this.toDoValidator.addEmptyValidator(this.txtToDoTitle);
        this.toDoValidator.addEmptyValidator(this.txtToDoDeadline);
        this.toDoValidator.addValueEqualsDate(this.txtToDoDeadline);
    }

    private void initTabHost() {
        TabHost tabHost = this.findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec tabSpec1 = tabHost.newTabSpec(this.getString(R.string.app_todo_list));
        tabSpec1.setIndicator(this.getString(R.string.app_todo_list));
        tabSpec1.setContent(R.id.tab1);
        tabHost.addTab(tabSpec1);

        TabHost.TabSpec tabSpec2 = tabHost.newTabSpec(this.getString(R.string.app_todo));
        tabSpec2.setIndicator(this.getString(R.string.app_todo));
        tabSpec2.setContent(R.id.tab2);
        tabHost.addTab(tabSpec2 );

        TabHost.TabSpec tabSpec3 = tabHost.newTabSpec(this.getString(R.string.notifications));
        tabSpec3.setIndicator(this.getString(R.string.notifications));
        tabSpec3.setContent(R.id.tab3);
        tabHost.addTab(tabSpec3);

        tabHost.setCurrentTab(0);
    }

    private void addTableRow() {
        TableRow tableRow = new TableRow(this.getApplicationContext());

        CheckBox checkBox = new CheckBox(this.getApplicationContext());
        checkBox.setLayoutParams(this.getParams(2));
        checkBox.setGravity(Gravity.CENTER);
        tableRow.addView(checkBox);

        EditText txt = new EditText(this.getApplicationContext());
        txt.setLayoutParams(this.getParams(16));
        tableRow.addView(txt);

        ImageButton cmdDelete = new ImageButton(this.getApplicationContext());
        cmdDelete.setBackgroundResource(R.drawable.ic_delete_black_24dp);
        cmdDelete.setLayoutParams(this.getParams(1));
        cmdDelete.setOnClickListener(v -> {
            if(this.tblControls.getChildCount()!=1) {
                this.tblControls.removeView(tableRow);
            }
        });
        tableRow.addView(cmdDelete);

        ImageButton cmdAdd = new ImageButton(this.getApplicationContext());
        cmdAdd.setBackgroundResource(R.drawable.sys_add);
        cmdAdd.setLayoutParams(this.getParams(1));
        cmdAdd.setOnClickListener(v -> this.addTableRow());
        tableRow.addView(cmdAdd);

        this.tblControls.addView(tableRow);
    }

    private TableRow.LayoutParams getParams(int weight) {
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight);
        params.gravity = Gravity.CENTER;
        return params;
    }

    private void objectToFields() {
        try {
            if(this.toDoList == null) {
                this.toDoList = new CalendarToDoList();
            }

            this.txtToDoTitle.setText(this.toDoList.getName());
            this.txtToDoDescription.setText(this.toDoList.getDescription());
            this.txtToDoCategory.setText(this.toDoList.getCategory());
            if(this.toDoList.getCalendar() != null) {
                this.txtToDoDeadline.setText(ConvertHelper.convertDateToString(this.toDoList.getCalendar().getTime(), Global.getDateFormat(getApplicationContext()).split(" ")[0]));
            }

            for(int i = 0; i<=this.toDoList.getToDos().size() - 1; i++) {
                ((CheckBox) ((TableRow)this.tblControls.getChildAt(i)).getChildAt(0)).setChecked(this.toDoList.getToDos().get(i).isChecked());
                ((EditText) ((TableRow)this.tblControls.getChildAt(i)).getChildAt(1)).setText(this.toDoList.getToDos().get(i).getContent());
                this.addTableRow();
            }

            int i = 1;
            for(Notification notification : this.toDoList.getNotifications()) {
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

            if(this.currentFamily == null) {
                this.findViewById(android.R.id.tabs).setBackgroundColor(WidgetUtils.getColor(this.getApplicationContext(), R.color.colorAccent));
            } else {
                this.findViewById(android.R.id.tabs).setBackgroundColor(this.currentFamily.getColor());
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ToDoActivity.this);
        }
    }

    private void fieldsToObject() {
        try {
            if (this.toDoList == null) {
                this.toDoList = new CalendarToDoList();
            }

            this.toDoList.setName(this.txtToDoTitle.getText().toString());
            this.toDoList.setDescription(this.txtToDoDescription.getText().toString());
            this.toDoList.setCategory(this.txtToDoCategory.getText().toString());
            if(!this.txtToDoDeadline.getText().toString().isEmpty()) {
                this.toDoList.setCalendar(ConvertHelper.convertStringToDate(this.txtToDoDeadline.getText().toString(), Global.getDateFormat(getApplicationContext()).split(" ")[0]));
            }

            this.toDoList.getToDos().clear();
            for(int i = 0; i<=this.tblControls.getChildCount() - 1; i++) {
                ToDo toDo = new ToDo();
                toDo.setChecked(((CheckBox) ((TableRow)this.tblControls.getChildAt(i)).getChildAt(0)).isChecked());
                toDo.setContent(((EditText) ((TableRow)this.tblControls.getChildAt(i)).getChildAt(1)).getText().toString());
                if(!toDo.getContent().trim().isEmpty()) {
                    this.toDoList.getToDos().add(toDo);
                }
            }

            this.toDoList.getNotifications().clear();
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
                    this.toDoList.getNotifications().add(notification);
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, ToDoActivity.this);
        }
    }
}
