/*
 * Copyright (C)  2019 Domjos
 * This file is part of UniTrackerMobile <https://github.com/domjos1994/UniTrackerMobile>.
 *
 * UniTrackerMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UniTrackerMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UniTrackerMobile. If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.customwidgets.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import de.domjos.customwidgets.utils.Converter;

public class ListObject {
    private byte[] icon;
    private Drawable drawable;
    private Context context;
    private String title, subTitle;
    private boolean selected;

    public ListObject(Context context, int resID) {
        super();

        this.context = context;
        this.icon = null;
        this.drawable = Converter.convertResourcesToDrawable(context, resID);
    }

    public ListObject(Context context, byte[] img) {
        super();

        this.context = context;
        this.icon = img;

        if (this.icon != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(this.icon, 0, this.icon.length);
            this.drawable = new BitmapDrawable(this.context.getResources(), bitmap);
        } else {
            this.drawable = null;
        }
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
        if (this.icon != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(this.icon, 0, this.icon.length);
            this.drawable = new BitmapDrawable(this.context.getResources(), bitmap);
        } else {
            this.drawable = null;
        }
    }

    public Drawable getIcon() {
        return this.drawable;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return this.subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
