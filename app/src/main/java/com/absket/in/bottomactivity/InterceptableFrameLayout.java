package  com.absket.in.bottomactivity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import javax.annotation.Nullable;

public class InterceptableFrameLayout extends FrameLayout {

    public static interface OnInterceptTouchEventListener {

        public boolean onInterceptTouchEvent(MotionEvent ev);
    }

    public InterceptableFrameLayout(Context context) {
        super(context);
    }

    public InterceptableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptableFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Nullable
    private OnInterceptTouchEventListener mOnInterceptTouchEventListener;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean capture = super.onInterceptTouchEvent(ev);
        if (mOnInterceptTouchEventListener != null) {
            capture |= mOnInterceptTouchEventListener.onInterceptTouchEvent(ev);
        }
        return capture;
    }

    @Nullable
    public OnInterceptTouchEventListener getOnInterceptTouchEventListener() {
        return mOnInterceptTouchEventListener;
    }

    public void setOnInterceptTouchEventListener(@Nullable OnInterceptTouchEventListener onInterceptTouchEventListener) {
        mOnInterceptTouchEventListener = onInterceptTouchEventListener;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }
}
