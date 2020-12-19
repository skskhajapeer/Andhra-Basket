package com.absket.in.bottomactivity;

import android.os.Build;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class ObserverCompat {
    public static void addOnGlobalLayoutListener(ViewTreeObserver viewTreeObserver,
                                                 ViewTreeObserver.OnGlobalLayoutListener listener) {
        viewTreeObserver.addOnGlobalLayoutListener(listener);

    }

    public static void removeOnGlobalLayoutListener(ViewTreeObserver viewTreeObserver,
                                                    ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            viewTreeObserver.removeOnGlobalLayoutListener(listener);
        } else {
            viewTreeObserver.removeGlobalOnLayoutListener(listener);
        }
    }

    public static void addOnPreDrawListener(ViewTreeObserver viewTreeObserver,
                                            ViewTreeObserver.OnPreDrawListener listener) {
        viewTreeObserver.addOnPreDrawListener(listener);
    }

    public static void removeOnPreDrawListener(ViewTreeObserver viewTreeObserver,
                                               ViewTreeObserver.OnPreDrawListener listener) {
        viewTreeObserver.removeOnPreDrawListener(listener);
    }

    public interface OnSizeListener {

        void onSize(View view, int width, int height);
    }

    @Nullable
    public static Object invokeOnSizeListener(@Nonnull final View view,
                                              @Nonnull final OnSizeListener listener) {
        checkNotNull(view);
        checkNotNull(listener);

        if (!view.isLayoutRequested() && view.getWidth() > 0 && view.getHeight() > 0) {
            listener.onSize(view, view.getWidth(), view.getHeight());
            return null;
        } else {
            final ViewTreeObserver.OnPreDrawListener subListener = new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (view.getWidth() > 0 && view.getHeight() > 0) {
                        listener.onSize(view, view.getWidth(), view.getHeight());
                        removeOnPreDrawListener(view.getViewTreeObserver(), this);
                    }
                    return true;
                }
            };
            addOnPreDrawListener(view.getViewTreeObserver(), subListener);
            return subListener;
        }
    }

    public static void removeOnSizeListener(@Nonnull final View view,
                                            @Nullable final Object listener) {
        checkNotNull(view);
        if (listener != null) {
            checkState(listener instanceof ViewTreeObserver.OnPreDrawListener);
            //noinspection ConstantConditions
            removeOnPreDrawListener(view.getViewTreeObserver(), (ViewTreeObserver.OnPreDrawListener) listener);
        }
    }

}
