package com.neat.view.util;

import com.neat.model.SessionManager;

import java.util.Locale;

/**
 * Created by f.gatti.gomez on 19/02/2017.
 */

public class PriceUtil {

    public static String getFormattedPrice(SessionManager sessionManager) {
        return String.format(Locale.getDefault(), "%.2f %s", sessionManager.getSession().getTotalSum(), sessionManager.getSession().currency);
    }

}
