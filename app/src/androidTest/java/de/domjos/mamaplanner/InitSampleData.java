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

package de.domjos.mamaplanner;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import de.domjos.customwidgets.utils.Converter;
import de.domjos.mamaplanner.helper.SQLite;
import de.domjos.mamaplanner.model.family.Family;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class InitSampleData {

    @Test
    public void intSampleData() throws Exception {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SQLite sqLite = new SQLite(appContext);

        sqLite.deleteItem(new Family(), "");

        Family family = new Family();
        family.setFirstName("Max");
        family.setLastName("MusterMan");
        family.setBirthDate(Converter.convertStringToDate("1976-23-07", "yyyy-MM-dd"));
        family.setGender("M");
        family.setColor(appContext.getColor(android.R.color.holo_blue_bright));
        sqLite.insertOrUpdateFamily(family);

        family = new Family();
        family.setFirstName("Anna");
        family.setLastName("MusterMan");
        family.setBirthDate(Converter.convertStringToDate("1978-07-11", "yyyy-MM-dd"));
        family.setGender("W");
        family.setColor(appContext.getColor(android.R.color.holo_red_dark));
        sqLite.insertOrUpdateFamily(family);

        family = new Family();
        family.setFirstName("Sarah");
        family.setLastName("MusterMan");
        family.setBirthDate(Converter.convertStringToDate("1998-02-01", "yyyy-MM-dd"));
        family.setGender("W");
        family.setColor(appContext.getColor(android.R.color.holo_purple));
        sqLite.insertOrUpdateFamily(family);

        family = new Family();
        family.setFirstName("Tom");
        family.setLastName("MusterMan");
        family.setBirthDate(Converter.convertStringToDate("2001-28-04", "yyyy-MM-dd"));
        family.setGender("M");
        family.setColor(appContext.getColor(android.R.color.holo_green_dark));
        sqLite.insertOrUpdateFamily(family);
    }
}
