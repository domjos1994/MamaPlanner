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

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.mamaplanner.R;

public final class SettingsActivity extends AbstractActivity {

    public SettingsActivity() {
        super(R.layout.settings_activity);
    }

    @Override
    protected void initActions() {

    }

    @Override
    protected void initControls() {
        getSupportFragmentManager().beginTransaction().replace(R.id.llMain, new PreferenceFragment()).commit();
    }

    public static class PreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            this.addPreferencesFromResource(R.xml.app_preferences);
        }
    }
}
