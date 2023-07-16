package com.eminor.donnez_luigloire;

import android.content.Context;

import com.facebook.appevents.AppEventsLogger;

/**
 * Created by Daniel on 2/6/16.
 */
public final class FBEventMgr {

    public static void logCustumEvent(Context context, String eventName)
    {
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        logger.logEvent(eventName);
    }

    public static void activateApp(Context context)
    {
        AppEventsLogger.activateApp(context);
    }

    public static void deactivateApp(Context context)
    {
        AppEventsLogger.deactivateApp(context);
    }
}
