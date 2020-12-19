package com.absket.in;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

class ReverseInterpolator implements Interpolator {

    private Interpolator interpolator;

    public ReverseInterpolator(Interpolator interpolator) {

        this.interpolator = interpolator;
    }

    public ReverseInterpolator() {
        this(new DecelerateInterpolator());
    }

    @Override
    public float getInterpolation(float input) {
        return 1 - interpolator.getInterpolation(input);
    }
}
