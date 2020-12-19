package com.absket.in.bottomactivity;

import android.annotation.TargetApi;
import android.os.Build;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.Window;

import javax.annotation.Nonnull;

/**
 * Created by Sudhir on 3/1/2017.
 */

public class TransitionsHelper {
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setupSlideBottomWithoutStatusBarsTransition(@Nonnull Window window) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        final Transition fade = new Slide(Gravity.BOTTOM);
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        window.setExitTransition(fade);
        window.setEnterTransition(fade);
    }
}
