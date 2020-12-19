package com.absket.in.utils;

import android.content.Context;
import android.media.RingtoneManager;

import com.webengage.sdk.android.actions.render.PushNotificationData;
import com.webengage.sdk.android.callbacks.PushNotificationCallbacks;

/**
 * Created by KhajaPeer on 06-09-2017.
 */
@SuppressWarnings("deprecation")

public class PushNotificationCallbacksImpl implements PushNotificationCallbacks {

    @Override
    public PushNotificationData onPushNotificationReceived(Context context, PushNotificationData notificationData) {
        notificationData.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        notificationData.setVibrateFlag(true);
        return notificationData;
    }

    @Override
    public void onPushNotificationShown(Context context, PushNotificationData pushNotificationData) {

    }

    @Override
    public boolean onPushNotificationClicked(Context context, PushNotificationData pushNotificationData) {
        return false;
    }

    @Override
    public void onPushNotificationDismissed(Context context, PushNotificationData pushNotificationData) {

    }

    @Override
    public boolean onPushNotificationActionClicked(Context context, PushNotificationData pushNotificationData, String s) {
        return false;
    }
}
