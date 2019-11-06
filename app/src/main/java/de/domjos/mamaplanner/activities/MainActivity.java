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

import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;

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
import de.domjos.mamaplanner.model.family.Family;
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

    public MainActivity() {
        super(R.layout.main_activity);
    }

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

                Intent intent = new Intent(this.getApplicationContext(), EventActivity.class);
                intent.putExtra("ID", 0);
                Family family = (Family) this.spAppHeaderFamily.getSelectedItem();
                if(family!=null) {
                    intent.putExtra("FAMILY", family.getID());
                } else {
                    intent.putExtra("FAMILY", 0);
                }
                if(event!=null) {
                    intent.putExtra("DT", Converter.convertDateToString(event.getCalendar().getTime(), Global.getDateFormat()));
                } else {
                    intent.putExtra("DT", Converter.convertDateToString(new Date(), Global.getDateFormat()));
                }
                startActivityForResult(intent, RELOAD_CALENDAR_EVENT);
            } catch (Exception ex) {
                MessageHelper.printException(ex, MainActivity.this);
            }
        });

        this.ivAppHeaderProfile.setOnClickListener(view -> {
            long id = 0;
            if(this.spAppHeaderFamily.getSelectedItem()!=null) {
                Family family = (Family) this.spAppHeaderFamily.getSelectedItem();
                id = family.getID();
            }

            Intent intent = new Intent(MainActivity.this, FamilyActivity.class);
            intent.putExtra("id", id);
            this.startActivityForResult(intent, MainActivity.RELOAD_FAMILY);
        });

        this.spAppHeaderFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Family family = familyArrayAdapter.getItem(i);

                if(family != null) {
                    if(family.getID() == 0L && family.getProfilePicture()==null) {
                        ivAppHeaderProfile.setImageDrawable(WidgetUtils.getDrawable(MainActivity.this, R.mipmap.ic_launcher_round));
                        lblAppHeaderName.setText(R.string.app_family_all);
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
                        Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                        intent.putExtra("ID", ((CalendarEvent) event).getID());
                        Family family = (Family) spAppHeaderFamily.getSelectedItem();
                        if(family!=null) {
                            intent.putExtra("FAMILY", family.getID());
                            if(family.getID() == 0) {
                                if (((CalendarEvent) event).getFamily() != null) {
                                    intent.putExtra("FAMILY", ((CalendarEvent) event).getFamily().getID());
                                } else {
                                    intent.putExtra("FAMILY", 0);
                                }
                            }
                        } else {
                            if (((CalendarEvent) event).getFamily() != null) {
                                intent.putExtra("FAMILY", ((CalendarEvent) event).getFamily().getID());
                            } else {
                                intent.putExtra("FAMILY", 0);
                            }
                        }
                        intent.putExtra("DT", Converter.convertDateToString(event.getCalendar().getTime(), Global.getDateFormat()));
                        startActivityForResult(intent, RELOAD_CALENDAR_EVENT);
                    }
                } catch (Exception ex) {
                    MessageHelper.printException(ex, MainActivity.this);
                }
            }
        });
        this.calApp.setOnLongClick(new WidgetCalendar.LongClickListener() {
            @Override
            public void onLongClick(Event event) {
                try {
                    if(event instanceof CalendarEvent) {
                        MainActivity.GLOBAL.getSqLite().deleteItem(((CalendarEvent)event));
                        reloadEvents();
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

    private void reloadEvents() {
        try {
            this.calApp.getEvents().clear();
            long id = ((Family) this.spAppHeaderFamily.getSelectedItem()).getID();
            String query = id==0L ? "" : "family=" + id;

            for(CalendarEvent calendarEvent : MainActivity.GLOBAL.getSqLite().getEvents(query)) {
                this.calApp.addEvent(calendarEvent);
            }
            this.calApp.reload();
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
        if (id == R.id.menMainSettings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
}
