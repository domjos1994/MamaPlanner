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

import android.app.Activity;
import android.widget.*;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.Date;
import java.util.List;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.Converter;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.WidgetUtils;
import de.domjos.customwidgets.widgets.calendar.Event;
import de.domjos.customwidgets.widgets.calendar.WidgetCalendar;
import de.domjos.mamaplanner.R;
import de.domjos.mamaplanner.helper.SQLite;
import de.domjos.mamaplanner.helper.Helper;
import de.domjos.mamaplanner.model.calendar.CalendarEvent;
import de.domjos.mamaplanner.model.calendar.CalendarToDoList;
import de.domjos.mamaplanner.model.family.Family;
import de.domjos.mamaplanner.model.objects.IDatabaseObject;
import de.domjos.mamaplanner.services.NotificationService;
import de.domjos.mamaplanner.settings.Global;

public final class MainActivity extends AbstractActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Animation fabOpen, fabClose, fabClock, fabAntiClock;
    private FloatingActionButton fabAppAdd, fabAppEvent, fabAppToDo;
    private TextView lblAppEvent, lblAppToDo;
    private DrawerLayout drawerLayout;
    private WidgetCalendar calApp;

    private ImageView ivAppHeaderProfile;
    private TextView lblAppHeaderName;
    private Spinner spAppHeaderFamily;
    private ArrayAdapter<Family> familyArrayAdapter;

    public final static Global GLOBAL = new Global();

    private boolean isOpen = false;
    private final static int RELOAD_FAMILY = 99;
    private final static int RELOAD_CALENDAR_EVENT = 100;
    private final static int RELOAD_AFTER_SETTINGS = 101;

    static final String ID = "ID";
    static final String DATE = "DT";
    static final String FAMILY = "FAMILY";
    static final String WHOLE_DAY = "wholeDay";

    public MainActivity() {
        super(R.layout.main_activity);
    }

    @SuppressWarnings("deprecation")
    public void initActions() {
        this.fabAppAdd.setOnClickListener(view -> {
            this.showAnimation(this.lblAppEvent, this.fabAppEvent);
            this.showAnimation(this.lblAppToDo, this.fabAppToDo);
            if(this.isOpen) {
                this.fabAppAdd.startAnimation(this.fabAntiClock);
            } else {
                this.fabAppAdd.startAnimation(this.fabClock);
            }
            this.isOpen = !this.isOpen;
        });

        this.fabAppEvent.setOnClickListener(view -> {
            try {
                Event event = this.calApp.getCurrentEvent();
                this.startActivity(event, EventActivity.class);
            } catch (Exception ex) {
                MessageHelper.printException(ex, MainActivity.this);
            }
        });

        this.fabAppToDo.setOnClickListener(view -> {
            try {
                Event event = this.calApp.getCurrentEvent();
                this.startActivity(event, ToDoActivity.class);
            } catch (Exception ex) {
                MessageHelper.printException(ex, MainActivity.this);
            }
        });

        this.spAppHeaderFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Family family = familyArrayAdapter.getItem(i);

                if(family != null) {
                    if(family.getID() == 0L && family.getProfilePicture()==null) {
                        ivAppHeaderProfile.setImageDrawable(WidgetUtils.getDrawable(MainActivity.this, R.mipmap.ic_launcher_round));
                        lblAppHeaderName.setText(R.string.app_family_all);
                        lblAppHeaderName.setTextColor(getResources().getColor(R.color.colorPrimary));
                    } else if(family.getProfilePicture() == null) {
                        String name = family.getFirstName() + " " + family.getLastName();
                        lblAppHeaderName.setText(name);
                        if(family.getColor() != 0) {
                            lblAppHeaderName.setTextColor(family.getColor());
                        }
                    } else {
                        ivAppHeaderProfile.setImageBitmap(Converter.convertByteArrayToBitmap(family.getProfilePicture()));
                        String name = family.getFirstName() + " " + family.getLastName();
                        lblAppHeaderName.setText(name);
                        if(family.getColor() != 0) {
                            lblAppHeaderName.setTextColor(family.getColor());
                        }
                    }
                    reloadEvents();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });
    }

    public void initControls() {
        MainActivity.GLOBAL.setSqLite(new SQLite(this));
        Helper.initRepeatingService(MainActivity.this, NotificationService.class, 120000);

        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        this.fabAppAdd = this.findViewById(R.id.fabAppAdd);
        this.fabAppEvent = this.findViewById(R.id.fabAppEvent);
        this.lblAppEvent = this.findViewById(R.id.lblAppEvent);
        this.fabAppToDo = this.findViewById(R.id.fabAppToDo);
        this.lblAppToDo = this.findViewById(R.id.lblAppToDo);

        this.fabOpen = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.fab_open);
        this.fabClose = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.fab_close);
        this.fabClock = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.fab_rotate_clock);
        this.fabAntiClock = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.fab_rotate_anticlock);

        this.drawerLayout = this.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        this.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = this.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MenuItem itemMonth = navigationView.getMenu().findItem(R.id.navAppViewMonth);
        Switch swtMonth = (Switch) itemMonth.getActionView();
        swtMonth.setChecked(true);
        swtMonth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            itemMonth.setChecked(isChecked);
            this.calApp.showMonth(isChecked);
        });
        MenuItem itemDay = navigationView.getMenu().findItem(R.id.navAppViewDay);
        Switch swtDay = (Switch) itemDay.getActionView();
        swtDay.setChecked(true);
        swtDay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            itemDay.setChecked(isChecked);
            this.calApp.showDay(isChecked);
        });

        this.ivAppHeaderProfile = navigationView.getHeaderView(0).findViewById(R.id.ivAppProfilePicture);
        this.lblAppHeaderName = navigationView.getHeaderView(0).findViewById(R.id.lblAppFamilyName);
        this.spAppHeaderFamily = navigationView.getHeaderView(0).findViewById(R.id.spAppFamily);
        this.familyArrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.spinner_item);
        this.spAppHeaderFamily.setAdapter(this.familyArrayAdapter);
        this.familyArrayAdapter.notifyDataSetChanged();

        this.calApp = this.findViewById(R.id.calApp);
        this.calApp.setOnClick(new WidgetCalendar.ClickListener() {
            @Override
            public void onClick(Event event) {
                try {
                    if(event instanceof CalendarEvent) {
                        MainActivity.this.startActivity(event, null, true, EventActivity.class);
                    } else if(event instanceof CalendarToDoList) {
                        MainActivity.this.startActivity(event, null, true, ToDoActivity.class);
                    }
                } catch (Exception ex) {
                    MessageHelper.printException(ex, MainActivity.this);
                }
            }
        });
        this.calApp.setOnLongClick(new WidgetCalendar.ClickListener() {
            @Override
            public void onClick(Event event) {
                try {
                    if(event instanceof CalendarEvent) {
                        if(!((CalendarEvent) event).isSystem()) {
                            MainActivity.GLOBAL.getSqLite().deleteItem(((CalendarEvent) event));
                            reloadEvents();
                        }
                    } else if(event instanceof CalendarToDoList) {
                        MainActivity.GLOBAL.getSqLite().deleteItem(((CalendarToDoList) event));
                        reloadEvents();
                    }
                } catch (Exception ex) {
                    MessageHelper.printException(ex, MainActivity.this);
                }
            }
        });
        this.calApp.setOnHourHeaderClick(new WidgetCalendar.ClickListener() {
            @Override
            public void onClick(Event event) {
                try {
                    if(event instanceof CalendarEvent) {
                        MainActivity.this.startActivity(event, null, false, EventActivity.class);
                    } else if(event instanceof CalendarToDoList) {
                        MainActivity.this.startActivity(event, null, false, ToDoActivity.class);
                    } {
                        Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                        intent.putExtra("ID", 0);
                        Family family = (Family) spAppHeaderFamily.getSelectedItem();
                        if(family!=null) {
                            intent.putExtra("FAMILY", family.getID());
                        } else {
                            intent.putExtra("FAMILY", 0);
                        }
                        intent.putExtra("DT", Converter.convertDateToString(event.getCalendar().getTime(), Global.getDateFormat()));
                        intent.putExtra("wholeDay", false);
                        startActivityForResult(intent, RELOAD_CALENDAR_EVENT);
                    }
                } catch (Exception ex) {
                    MessageHelper.printException(ex, MainActivity.this);
                }
            }
        });
        this.calApp.setOnHourGroupClick(new WidgetCalendar.ClickListener() {
            @Override
            public void onClick(Event event) {
                try {
                    if(event instanceof  CalendarEvent) {
                        List<Family> familyList = MainActivity.GLOBAL.getSqLite().getFamily("color=" + event.getColor());
                        if(familyList.size()>=1) {
                            MainActivity.this.startActivity(event, familyList.get(0), false, EventActivity.class);
                        } else {
                            MainActivity.this.startActivity(event, null, false, EventActivity.class);
                        }
                    } else if(event instanceof CalendarToDoList) {
                        List<Family> familyList = MainActivity.GLOBAL.getSqLite().getFamily("color=" + event.getColor());
                        if(familyList.size()>=1) {
                            MainActivity.this.startActivity(event, familyList.get(0), false, ToDoActivity.class);
                        } else {
                            MainActivity.this.startActivity(event, null, false, ToDoActivity.class);
                        }
                    } {
                        Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                        intent.putExtra("ID", 0);
                        Family family;
                        List<Family> familyList = MainActivity.GLOBAL.getSqLite().getFamily("color=" + event.getColor());
                        if(familyList.size()>=1) {
                            family = familyList.get(0);
                        } else {
                            family = (Family) spAppHeaderFamily.getSelectedItem();
                        }
                        if(family!=null) {
                            intent.putExtra("FAMILY", family.getID());
                        } else {
                            intent.putExtra("FAMILY", 0);
                        }
                        intent.putExtra("DT", Converter.convertDateToString(event.getCalendar().getTime(), Global.getDateFormat()));
                        intent.putExtra("wholeDay", false);
                        startActivityForResult(intent, RELOAD_CALENDAR_EVENT);
                    }
                } catch (Exception ex) {
                    MessageHelper.printException(ex, MainActivity.this);
                }
            }
        });
        this.calApp.reload();
    }

    @Override
    public void reload() {
        try {
            this.calApp.getGroups().clear();
            this.familyArrayAdapter.clear();
            this.familyArrayAdapter.add(new Family());
            this.calApp.addGroup(this.getString(R.string.app_family_all).split(" ")[0], android.R.color.transparent);
            for(Family family : MainActivity.GLOBAL.getSqLite().getFamily("")) {
                this.familyArrayAdapter.add(family);
                if(family.getColor() != -1 && family.getColor() != 0) {
                    this.calApp.addGroup(family.getFirstName(), family.getColor());
                }
            }
            this.spAppHeaderFamily.setSelection(0);
            this.reloadEvents();
        } catch (Exception ex) {
            MessageHelper.printException(ex, MainActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        if (id == R.id.menMainSettings) {
            intent = new Intent(this.getApplicationContext(), SettingsActivity.class);
        }

        if(intent!=null) {
            startActivityForResult(intent, MainActivity.RELOAD_AFTER_SETTINGS);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navAppFamily) {
            Intent intent = new Intent(MainActivity.this, FamilyActivity.class);
            this.startActivityForResult(intent, MainActivity.RELOAD_FAMILY);
        }

        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RELOAD_FAMILY && resultCode == RESULT_OK) {
            this.reload();
        }
        if(requestCode == RELOAD_CALENDAR_EVENT && resultCode == RESULT_OK) {
            this.reloadEvents();
        }
    }

    private void startActivity(Event event, Class<? extends Activity> activity) throws Exception {
        Intent intent = new Intent(this.getApplicationContext(), activity);
        intent.putExtra(MainActivity.ID, 0);
        Family family = (Family) this.spAppHeaderFamily.getSelectedItem();
        if(family!=null) {
            intent.putExtra(MainActivity.FAMILY, family.getID());
        } else {
            intent.putExtra(MainActivity.FAMILY, 0);
        }
        if(event!=null) {
            intent.putExtra(MainActivity.DATE, Converter.convertDateToString(event.getCalendar().getTime(), Global.getDateFormat()));
        } else {
            intent.putExtra(MainActivity.DATE, Converter.convertDateToString(new Date(), Global.getDateFormat()));
        }
        startActivityForResult(intent, RELOAD_CALENDAR_EVENT);
    }

    private void startActivity(Event event, Family family, boolean wholeDay, Class<? extends Activity> activity) throws Exception {
        Intent intent = new Intent(getApplicationContext(), activity);
        intent.putExtra(MainActivity.ID, ((IDatabaseObject) event).getID());
        if(family == null) {
            family = (Family) spAppHeaderFamily.getSelectedItem();
        }
        if(family!=null) {
            intent.putExtra(MainActivity.FAMILY, family.getID());
            if(family.getID() == 0) {
                if(event instanceof  CalendarEvent) {
                    if (((CalendarEvent) event).getFamily() != null) {
                        intent.putExtra(MainActivity.FAMILY, ((CalendarEvent) event).getFamily().getID());
                    } else {
                        intent.putExtra(MainActivity.FAMILY, 0);
                    }
                } else {
                    if (((CalendarToDoList) event).getFamily() != null) {
                        intent.putExtra(MainActivity.FAMILY, ((CalendarToDoList) event).getFamily().getID());
                    } else {
                        intent.putExtra(MainActivity.FAMILY, 0);
                    }
                }
            }
        } else {
            if(event instanceof  CalendarEvent) {
                if (((CalendarEvent) event).getFamily() != null) {
                    intent.putExtra(MainActivity.FAMILY, ((CalendarEvent) event).getFamily().getID());
                } else {
                    intent.putExtra(MainActivity.FAMILY, 0);
                }
            } else {
                if (((CalendarToDoList) event).getFamily() != null) {
                    intent.putExtra(MainActivity.FAMILY, ((CalendarToDoList) event).getFamily().getID());
                } else {
                    intent.putExtra(MainActivity.FAMILY, 0);
                }
            }
        }
        intent.putExtra(MainActivity.DATE, Converter.convertDateToString(event.getCalendar().getTime(), Global.getDateFormat()));
        intent.putExtra(MainActivity.WHOLE_DAY, wholeDay);
        startActivityForResult(intent, RELOAD_CALENDAR_EVENT);
    }

    private void showAnimation(TextView lbl, FloatingActionButton fab) {
        if(this.isOpen) {
            lbl.setVisibility(View.INVISIBLE);
            fab.startAnimation(this.fabClose);
            fab.setClickable(false);
        } else {
            lbl.setVisibility(View.VISIBLE);
            fab.startAnimation(this.fabOpen);
            fab.setClickable(true);
        }
    }

    private void reloadEvents() {
        try {
            this.calApp.getEvents().clear();
            long id = ((Family) this.spAppHeaderFamily.getSelectedItem()).getID();
            String query = id==0L ? "" : "family=" + id;

            for(CalendarEvent calendarEvent : MainActivity.GLOBAL.getSqLite().getEvents(query)) {
                if(calendarEvent.getFamily()==null) {
                    Family family = new Family();
                    family.setColor(android.R.color.transparent);
                    calendarEvent.setFamily(family);
                }
                if(calendarEvent.getColor()==-1) {
                    calendarEvent.setColor(android.R.color.transparent);
                }
                this.calApp.addEvent(calendarEvent);
            }
            for(CalendarToDoList calendarToDoList : MainActivity.GLOBAL.getSqLite().getToDoLists(query)) {
                if(calendarToDoList.getFamily()==null) {
                    Family family = new Family();
                    family.setColor(android.R.color.transparent);
                    calendarToDoList.setFamily(family);
                }
                if(calendarToDoList.getColor()==-1) {
                    calendarToDoList.setColor(android.R.color.transparent);
                }
                this.calApp.addEvent(calendarToDoList);
            }

            this.calApp.reload();
        } catch (Exception ex) {
            MessageHelper.printException(ex, MainActivity.this);
        }
    }
}
