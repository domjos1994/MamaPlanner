package de.domjos.mamaplanner.activities;

import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.Date;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.calendar.Event;
import de.domjos.customwidgets.widgets.calendar.WidgetCalendar;
import de.domjos.mamaplanner.R;

public final class MainActivity extends AbstractActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Animation fabOpen, fabClose, fabClock, fabAntiClock;
    private FloatingActionButton fabAppAdd, fabAppEvent, fabAppToDo;
    private TextView lblAppEvent, lblAppToDo;
    private DrawerLayout drawerLayout;
    private WidgetCalendar calApp;

    private boolean isOpen = false;

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

    }

    public void initControls() {
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

        this.calApp = this.findViewById(R.id.calApp);
        this.calApp.addEvent(new Event(new Date(), "test", "this is a test!", R.color.colorPrimary));
        this.calApp.addEvent(new Event(new Date(), "test 2cxbxcbdfhg", "this is a test 2!", android.R.color.holo_red_dark));
        this.calApp.setOnClick(new WidgetCalendar.ClickListener() {
            @Override
            public void onClick(Event event) {
                MessageHelper.printMessage(event.getDescription(), MainActivity.this);
            }
        });
        this.calApp.reload();
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
