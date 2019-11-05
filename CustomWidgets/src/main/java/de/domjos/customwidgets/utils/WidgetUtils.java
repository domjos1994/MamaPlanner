package de.domjos.customwidgets.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.io.InputStream;

public class WidgetUtils {

    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(Context context, int resource) {
        if(resource == -1) {
            return null;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return context.getDrawable(resource);
            } else {
                return context.getResources().getDrawable(resource);
            }
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

    public static String getRaw(Context context, int rawID) throws Exception {
        Resources res = context.getResources();
        InputStream in_s = res.openRawResource(rawID);

        byte[] b = new byte[in_s.available()];
        in_s.read(b);
        return new String(b);
    }
}
