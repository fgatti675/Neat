package com.neat.util;

import android.content.Context;

/**
 * Created by f.gatti.gomez on 29/01/2017.
 */

public class ViewUtils {

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
