package de.domjos.customwidgets.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class WidgetUtils {

    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(Context context, int resource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(resource);
        } else {
            return context.getResources().getDrawable(resource);
        }
    }

    @SuppressWarnings("deprecation")
    public static int getColor(Context context, int resource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(resource);
        } else {
            return context.getResources().getColor(resource);
        }
    }
}
