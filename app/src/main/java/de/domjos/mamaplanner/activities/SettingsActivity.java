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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.mamaplanner.R;
import de.domjos.mamaplanner.fragments.PreferenceFragment;

public final class SettingsActivity extends AbstractActivity {

    public SettingsActivity() {
        super(R.layout.settings_activity);
    }

    @Override
    protected void initActions() {

    }

    @Override
    protected void initControls() {
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new PreferenceFragment()).commit();
    }
}
