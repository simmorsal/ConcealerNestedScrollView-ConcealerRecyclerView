package com.simmorsal.library;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

public class ConcealerRecyclerView extends RecyclerView {
    public ConcealerRecyclerView(Context context) { super(context); }
    public ConcealerRecyclerView(Context context, @Nullable AttributeSet attrs) { super(context, attrs); }
    public ConcealerRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }


    private int y = 0, oldy = 0;

    private View headerView, footerView;
    private boolean isThereTouch = false;

    private int deltaY;
    private int t = 0;
    private int headerViewHeight = 0;
    private int headerTranslateY = 0;
    private int headerMaxTranslate;
    private int headerMinTranslate;
    private int footerViewHeight = 0;
    private int footerTranslateY = 0;
    private int footerMaxTranslate;
    private int footerMinTranslate;
    private boolean isHeaderFastHide = false, isFooterFastHide = false;

    private boolean shouldHeaderAutoHide = true, shouldFooterAutoHide = true;
    private int headerAutoHideSpeed = 200, footerAutoHideSpeed = 200;
    private int headerPercentageToHide = 40, footerPercentageToHide = 40;
    private Handler handlerHeader = new Handler(), handlerFooter = new Handler();
    private boolean hasHeaderRunnableRun = false, hasFooterRunnableRun = false;

    private boolean isHeaderConcealable = true, isFooterConcealable = true;


    public void setFooterView(final View view, final int marginBottomInDp) {
        if (view.getHeight() == 0) {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    footerViewHeight = view.getHeight();
                    footerMinTranslate = -footerViewHeight;
                }
            });
        } else {
            footerViewHeight = view.getHeight();
            footerMinTranslate = -footerViewHeight;
        }
        this.footerView = view;

        footerMaxTranslate = dpToPx(marginBottomInDp);
        footerTranslateY = footerMaxTranslate;

        view.setTranslationY(-footerMaxTranslate);
    }

    public void setHeaderView(final View view, final int marginTopInDp) {
        if (view.getHeight() == 0) {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    headerViewHeight = view.getHeight();
                    headerMinTranslate = -headerViewHeight;
                }
            });
        } else {
            headerViewHeight = view.getHeight();
            headerMinTranslate = -headerViewHeight;
        }
        this.headerView = view;

        headerMaxTranslate = dpToPx(marginTopInDp);
        headerTranslateY = headerMaxTranslate;

        view.setTranslationY(headerMaxTranslate);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        y = computeVerticalScrollOffset();

        this.t = y;
        deltaY = oldy - y;

        if (headerView != null && isHeaderConcealable)
            doHeaderTransition();
        if (footerView != null && isFooterConcealable)
            doFooterTransition();

        oldy = y;
    }
    private void doHeaderTransition() {

        if (shouldHeaderAutoHide)
            handlerHeader.removeCallbacks(headerAutoHideRunnable);

        int headerDelta = deltaY;

        // Going up
        if (headerDelta < 0 && headerTranslateY > headerMinTranslate) {
            if (isHeaderFastHide && t > headerMaxTranslate + headerViewHeight)
                headerDelta *= 2;
            headerTranslateY = headerTranslateY + headerDelta;
            if (headerTranslateY < headerMinTranslate)
                headerTranslateY = headerMinTranslate;
            headerView.setTranslationY(headerTranslateY);
        }

        // Going down
        else if (headerDelta > 0 && headerTranslateY < headerMaxTranslate) {
            if (t < headerMaxTranslate)
                headerDelta *= 3;
            headerTranslateY = headerTranslateY + headerDelta;
            if (t < 5)
                headerTranslateY = headerMaxTranslate;
            if (headerTranslateY > headerMaxTranslate)
                headerTranslateY = headerMaxTranslate;
            headerView.setTranslationY(headerTranslateY);
        }

        if (shouldHeaderAutoHide) {
            handlerHeader.postDelayed(headerAutoHideRunnable, 70);
            hasHeaderRunnableRun = false;
        }
    }

    private void doFooterTransition() {
        if (shouldFooterAutoHide)
            handlerFooter.removeCallbacks(footerAutoHideRunnable);

        int footerDelta = deltaY;

        // Going down
        if (footerDelta < 0 && footerTranslateY > footerMinTranslate) {
            if (isFooterFastHide && t > footerMaxTranslate + footerViewHeight)
                footerDelta *= 2;
            footerTranslateY = footerTranslateY + footerDelta;
            if (footerTranslateY < footerMinTranslate)
                footerTranslateY = footerMinTranslate;
            footerView.setTranslationY(-footerTranslateY);
        }

        // Going up
        else if (footerDelta > 0 && footerTranslateY < footerMaxTranslate) {
            if (t < footerMaxTranslate)
                footerDelta *= 3;
            footerTranslateY = footerTranslateY + footerDelta;
            if (t < 5)
                footerTranslateY = footerMaxTranslate;
            if (footerTranslateY > footerMaxTranslate)
                footerTranslateY = footerMaxTranslate;
            footerView.setTranslationY(-footerTranslateY);
        }

        if (shouldFooterAutoHide) {
            handlerFooter.postDelayed(footerAutoHideRunnable, 70);
            hasFooterRunnableRun = false;
        }
    }

    Runnable headerAutoHideRunnable = new Runnable() {
        @Override
        public void run() {
            hasHeaderRunnableRun = true;
            if (!isThereTouch) {
                if (headerTranslateY > (headerMinTranslate * (headerPercentageToHide / 100f))) {// should become visible
                    headerTranslateY = headerMaxTranslate;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        headerView.animate().translationY(headerMaxTranslate).setDuration(headerAutoHideSpeed).withLayer();
                    else
                        headerView.setTranslationY(headerMaxTranslate);
                } else { // should hide
                    if (t > headerMaxTranslate + headerViewHeight) {// can hide
                        headerTranslateY = headerMinTranslate;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            headerView.animate().translationY(headerMinTranslate).setDuration(headerAutoHideSpeed).withLayer();
                        else
                            headerView.setTranslationY(headerMinTranslate);
                    } else { // cant hide
                        headerTranslateY = headerMaxTranslate;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            headerView.animate().translationY(headerMaxTranslate).setDuration(headerAutoHideSpeed).withLayer();
                        else
                            headerView.setTranslationY(headerMaxTranslate);
                    }
                }
            }
        }
    };

    Runnable footerAutoHideRunnable = new Runnable() {
        @Override
        public void run() {
            hasFooterRunnableRun = true;
            if (!isThereTouch) {
                if (footerTranslateY > (footerMinTranslate * (footerPercentageToHide / 100f))) {// should become visible
                    footerTranslateY = -footerMaxTranslate;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        footerView.animate().translationY(-footerMaxTranslate).setDuration(footerAutoHideSpeed).withLayer();
                    else
                        footerView.setTranslationY(-footerMaxTranslate);
                } else { // should hide
                    if (t > headerMaxTranslate + headerViewHeight) {// can hide
                        footerTranslateY = footerMinTranslate;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            footerView.animate().translationY(-footerMinTranslate).setDuration(footerAutoHideSpeed).withLayer();
                        else
                            footerView.setTranslationY(-footerMinTranslate);
                    } else { // cant hide
                        footerTranslateY = -footerMaxTranslate;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            footerView.animate().translationY(-footerMaxTranslate).setDuration(footerAutoHideSpeed).withLayer();
                        else
                            footerView.setTranslationY(-footerMaxTranslate);
                    }
                }
            }
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN)
            isThereTouch = true;
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            isThereTouch = false;

            if (shouldHeaderAutoHide && hasHeaderRunnableRun && isHeaderConcealable)
                handlerHeader.post(headerAutoHideRunnable);

            if (shouldFooterAutoHide && hasFooterRunnableRun && isFooterConcealable)
                handlerFooter.post(footerAutoHideRunnable);
        }

        return super.onTouchEvent(ev);
    }

    public void resetHeaderHeight() {
        if (headerView != null)
            headerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        headerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    headerViewHeight = headerView.getHeight();
                    headerMinTranslate = -headerViewHeight;
                }
            });
    }

    public void resetFooterHeight() {
        if (footerView != null)
            footerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        footerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    footerViewHeight = footerView.getHeight();
                    footerMinTranslate = -footerViewHeight;
                }
            });
    }


    public void setHeaderConcealable(boolean headerConcealable, boolean shouldHeaderBeVisible) {
        isHeaderConcealable = headerConcealable;

        if (shouldHeaderBeVisible) {
            headerTranslateY = headerMaxTranslate;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                headerView.animate().translationY(headerMaxTranslate).setDuration(headerAutoHideSpeed).withLayer();
            else
                headerView.setTranslationY(headerMaxTranslate);
        } else {
            headerTranslateY = headerMinTranslate;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                headerView.animate().translationY(headerMinTranslate).setDuration(headerAutoHideSpeed).withLayer();
            else
                headerView.setTranslationY(headerMinTranslate);
        }
    }

    public void setFooterConcealable(boolean footerConcealable, boolean shouldFooterBeVisible) {
        isFooterConcealable = footerConcealable;

        if (shouldFooterBeVisible) {
            footerTranslateY = -footerMaxTranslate;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                footerView.animate().translationY(-footerMaxTranslate).setDuration(footerAutoHideSpeed).withLayer();
            else
                footerView.setTranslationY(-footerMaxTranslate);
        } else {
            footerTranslateY = footerMinTranslate;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                footerView.animate().translationY(-footerMinTranslate).setDuration(footerAutoHideSpeed).withLayer();
            else
                footerView.setTranslationY(-footerMinTranslate);
        }
    }

    public void setHeaderFastHide(boolean isHeaderFastHide) {
        this.isHeaderFastHide = isHeaderFastHide;
    }

    public void setFooterFastHide(boolean isFooterFastHide) {
        this.isFooterFastHide = isFooterFastHide;
    }

    public void setHeaderAutoHide(boolean shouldHeaderAutoHide) {
        this.shouldHeaderAutoHide = shouldHeaderAutoHide;
    }

    public void setFooterAutoHide(boolean shouldFooterAutoHide) {
        this.shouldFooterAutoHide = shouldFooterAutoHide;
    }

    public void setHeaderAutoHideSpeed(int headerAutoHideSpeed) {
        this.headerAutoHideSpeed = headerAutoHideSpeed;
    }

    public void setFooterAutoHideSpeed(int footerAutoHideSpeed) {
        this.footerAutoHideSpeed = footerAutoHideSpeed;
    }

    public void setHeaderPercentageToHide(int headerPercentageToHide) {
        this.headerPercentageToHide = headerPercentageToHide;
    }

    public void setFooterPercentageToHide(int footerPercentageToHide) {
        this.footerPercentageToHide = footerPercentageToHide;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}
