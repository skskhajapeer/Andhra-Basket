package com.absket.in.bottomactivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.google.common.base.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;




import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class PrettyAnimator {

    private static final String STATE_VIEW_EXPANDED = "state_view_expanded";

    private Context mContext;
    private float mScaledTouchSlop;

    private Object mOnSizeListener;
    private InterceptableFrameLayout mContainer;
    private View mChild;

    private boolean mViewExpanded;

    private float mStartY;
    private float mCurrentY;
    public float mInterceptStartY;

    private OnPrettyAnimatorListener mListener;
    private Optional<View> mNotificationsView;



    public interface OnPrettyAnimatorListener {
        boolean childAtTheTop();
        void closeView(View gridView);
    }

    public PrettyAnimator(Context mContext){
        this.mContext=mContext;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_VIEW_EXPANDED, mViewExpanded);
    }

    public void onViewCreated(@Nullable Bundle savedInstanceState,
                              @Nonnull InterceptableFrameLayout container,
                              @Nonnull View child,
                              @Nonnull Optional<View> notificationsView,
                              @Nonnull OnPrettyAnimatorListener listener) {
        checkState(mListener == null);

        mContainer = checkNotNull(container);
        mChild = checkNotNull(child);
        mNotificationsView = checkNotNull(notificationsView);
        mListener = checkNotNull(listener);
        mScaledTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();

        final boolean withAnimation;
        if (savedInstanceState == null) {
            mChild.setVisibility(View.INVISIBLE);
            mViewExpanded = false;
            withAnimation = true;
        } else {
            checkState(savedInstanceState.containsKey(STATE_VIEW_EXPANDED),
                    "onSaveInstanceState did not called");
            mViewExpanded = savedInstanceState.getBoolean(STATE_VIEW_EXPANDED);
            withAnimation = false;
        }

        mOnSizeListener = ObserverCompat.invokeOnSizeListener(container, new ObserverCompat.OnSizeListener() {

            @Override
            public void onSize(View view, int width, int height) {
                final int containerHeight = getContainerHeight();
                if (mNotificationsView.isPresent()) {
                    mNotificationsView.get().setPadding(0, 0, 0, getGridTopSpaceHeight());
                }
                final int destinationHeight = mViewExpanded ? 0 : getGridTopSpaceHeight();
                mChild.setVisibility(View.VISIBLE);
                if (withAnimation) {
                    mChild.setAlpha(0.0f);
                    mChild.setTranslationY(containerHeight);
                    mChild.animate()
                            .translationY(destinationHeight)
                            .alpha(1.0f)
                            .start();
                } else {
                    mChild.setAlpha(1.0f);
                    mChild.setTranslationY(destinationHeight);
                }
            }
        });
        mContainer.setOnInterceptTouchEventListener(new InterceptableFrameLayout.OnInterceptTouchEventListener() {

            @Override
            public boolean onInterceptTouchEvent(MotionEvent event) {
                return onContainerInterceptTouchEvent(event);
            }
        });
        mContainer.setOnTouchListener(
                new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return onContainerTouch(event);
                    }
                }
        );
    }

    protected boolean onContainerInterceptTouchEvent(MotionEvent event) {
        checkState(mListener != null, "Called after removing listener");
        if (mViewExpanded) {
            final boolean gridAtTheTop = mListener.childAtTheTop();
            if (!gridAtTheTop) {
                return false;
            }
            final int action = event.getAction();
            switch (action) {

                case MotionEvent.ACTION_DOWN:
                    onContainerTouch(event);
                    mInterceptStartY = event.getY();
                    return false;
                case MotionEvent.ACTION_MOVE: {
                    final boolean scrollDown = mInterceptStartY < event.getY();
                    if (scrollDown) {
                        final boolean scrollDownSloop = mInterceptStartY + mScaledTouchSlop < event.getY();
                        if (scrollDownSloop) {
                            onContainerTouch(event);
                            return true;
                        }
                    }
                    return false;
                }
                case MotionEvent.ACTION_UP:
                    onContainerTouch(event);
                    return false;
                default:
                    return false;
            }
        } else {
            final int action = event.getAction();
            switch (action) {

                case MotionEvent.ACTION_DOWN:
                    onContainerTouch(event);
                    mInterceptStartY = event.getY();
                    return false;
                case MotionEvent.ACTION_MOVE: {
                    final boolean scrollDownSloop = mInterceptStartY + mScaledTouchSlop < event.getY();
                    final boolean scrollUpSloop = mInterceptStartY - mScaledTouchSlop > event.getY();
                    final boolean capture = scrollDownSloop || scrollUpSloop;
                    if (capture) {
                        onContainerTouch(event);
                        return true;
                    }
                    return false;
                }
                case MotionEvent.ACTION_UP:
                    onContainerTouch(event);
                    return false;
                default:
                    return false;
            }
        }
    }

    protected boolean onContainerTouch(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_CANCEL:
                if (mViewExpanded) {
                    goToExpanded();
                } else {
                    goToCollapsed();
                }
                return true;
            case MotionEvent.ACTION_UP: {
                final float moveDiff = mCurrentY - mStartY;

                if (mViewExpanded) {
                    if (moveDiff > getGridTopSpaceHeight() + (getCollapsedGridHeight() / 2)) {
                        close();
                    } else if (moveDiff > mScaledTouchSlop) {
                        goToCollapsed();
                    } else {
                        goToExpanded();
                    }
                } else {
                    if (moveDiff < -mScaledTouchSlop) {
                        goToExpanded();
                    } else if (moveDiff > getCollapsedGridHeight() / 2) {
                        close();
                    } else {
                        goToCollapsed();
                    }
                }
                return true;
            }
            case MotionEvent.ACTION_DOWN:
                mStartY = event.getY();
                mCurrentY = mStartY;
                return true;
            case MotionEvent.ACTION_MOVE: {
                mCurrentY = event.getY();
                final float moveDiff = mCurrentY - mStartY;

                if (mViewExpanded) {
                    final int gridTopSpaceHeight = getGridTopSpaceHeight();
                    move(gridTopSpaceHeight, moveDiff);
                } else {
                    final int gridTopSpaceHeight = getGridTopSpaceHeight();
                    move(gridTopSpaceHeight, gridTopSpaceHeight + moveDiff);
                }

                return true;
            }
            default:
                return false;
        }
    }

    private void move(int gridTopSpaceHeight, float move) {
        final float translationY = constrain(move, 0.f, getContainerHeight());

        // moveDiff == getGridTopSpaceHeight() => alpha == 0.0f
        // moveDiff == 0 => alpha == 1.0f
        float alphaNotConstrained = 1.0f - ((move - gridTopSpaceHeight) / getCollapsedGridHeight());
        float alpha = constrain(alphaNotConstrained, 0.f, 1.f);

        mChild.setAlpha(alpha);
        mChild.setTranslationY(translationY);
        if (mNotificationsView.isPresent()) {
            mNotificationsView.get().setTranslationY((gridTopSpaceHeight - translationY) / 2);
        }
    }

    public void onDestroyView() {
        checkState(mListener != null);
        mListener = null;
        mContainer.setOnInterceptTouchEventListener(null);
        mContainer.setOnTouchListener(null);
        ObserverCompat.removeOnSizeListener(mContainer, mOnSizeListener);
    }

    private float constrain(float value, float min, float max) {
        checkArgument(min < max);
        return Math.max(min, Math.min(max, value));
    }

    public void doClose(Runnable runnable) {
        close(runnable);
    }

    public void doClose() {
        close();
    }
    public void showFull() {
        goToExpanded();
    }

    private void close(final Runnable runnable) {
        if (mNotificationsView.isPresent()) {
            mNotificationsView.get().animate()
                    .translationY(0.0f)
                    .start();
        }
        mChild.animate()
                .translationY(getContainerHeight())
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        runnable.run();
                    }

                })
                .start();
    }

    private void close() {
        close(new Runnable() {
            @Override
            public void run() {
                if (mListener == null) {
                    return;
                }
                mListener.closeView(mChild);
            }
        });
    }

    public void goToExpanded() {
        mViewExpanded = true;
        mChild.animate()
                .translationY(0.f)
                .alpha(1.0f)
                .start();
        if (mNotificationsView.isPresent()) {
            mNotificationsView.get().animate()
                    .translationY(getGridTopSpaceHeight() / 2)
                    .alpha(1.0f)
                    .start();
        }
    }

    protected void goToCollapsed() {
        mViewExpanded = false;
        mChild.animate()
                .translationY(getGridTopSpaceHeight())
                .alpha(1.0f)
                .start();
        if (mNotificationsView.isPresent()) {
            mNotificationsView.get().animate()
                    .translationY(0.0f)
                    .start();
        }
    }


    private int getGridTopSpaceHeight() {
        return getContainerHeight() - getCollapsedGridHeight();
    }

    private int getCollapsedGridHeight() {
        return (int)(getContainerHeight() * .7f);
    }

    private int getContainerHeight() {
        return mContainer.getHeight();
    }
}
