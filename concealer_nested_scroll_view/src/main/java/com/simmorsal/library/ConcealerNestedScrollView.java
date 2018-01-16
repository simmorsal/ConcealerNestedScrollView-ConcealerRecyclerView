package com.simmorsal.library;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

public class ConcealerNestedScrollView extends NestedScrollView {

    public ConcealerNestedScrollView(Context context) {
        super(context);
    }

    public ConcealerNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConcealerNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed){
        dispatchNestedPreScroll(dx,dy,consumed,null);
    }


    int deltaY;
    int t = 0;
    int headerViewHeight = 0;
    int headerTranslateY = 0;
    int headerMaxTranslate;
    int headerMinTranslate;
    int footerViewHeight = 0;
    int footerTranslateY = 0;
    int footerMaxTranslate;
    int footerMinTranslate;
    boolean isHeaderFastHide = false, isFooterFastHide = false;

    View headerView, footerView;

    public void setHeaderFastHide(boolean isHeaderFastHide){
        this.isHeaderFastHide = isHeaderFastHide;
    }
    public void setFooterFastHide(boolean isFooterFastHide){
        this.isFooterFastHide = isFooterFastHide;
    }

    public void setFooterView(final View view, final int marginBottom){
        if (view.getHeight() == 0){
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
        }
        else {
            footerViewHeight = view.getHeight();
            footerMinTranslate = -footerViewHeight;
        }
        this.footerView = view;

        footerMaxTranslate = dpToPx(marginBottom);
        footerTranslateY = footerMaxTranslate;

        view.setTranslationY(-footerMaxTranslate);
    }

    public void setHeaderView(final View view, final int marginTop){
        if (view.getHeight() == 0){
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
        }
        else {
            headerViewHeight = view.getHeight();
            headerMinTranslate = -headerViewHeight;
        }
        this.headerView = view;

        headerMaxTranslate = dpToPx(marginTop);
        headerTranslateY = headerMaxTranslate;

        view.setTranslationY(headerMaxTranslate);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        this.t = t;
        deltaY = oldt - t;
        if (headerView != null)
            doHeaderTransition();
        if (footerView != null)
            doFooterTransition();
    }

    private void doHeaderTransition() {

        int headerDelta = deltaY;

        // Going up
        if (headerDelta < 0 && headerTranslateY > headerMinTranslate){
            if (isHeaderFastHide && t > headerMaxTranslate + headerViewHeight)
                headerDelta *= 2;
            headerTranslateY = headerTranslateY + headerDelta;
            if (headerTranslateY < headerMinTranslate)
                headerTranslateY = headerMinTranslate;
            headerView.setTranslationY(headerTranslateY);
        }

        // Going down
        else if (headerDelta > 0 && headerTranslateY < headerMaxTranslate){
            if (t < headerMaxTranslate)
                headerDelta *= 3;
            headerTranslateY = headerTranslateY + headerDelta;
            if (t < 5)
                headerTranslateY = headerMaxTranslate;
            if (headerTranslateY > headerMaxTranslate)
                headerTranslateY = headerMaxTranslate;
            headerView.setTranslationY(headerTranslateY);
        }
    }

    private void doFooterTransition() {

        int footerDelta = deltaY;

        // Going down
        if (footerDelta < 0 && footerTranslateY > footerMinTranslate){
            if (isFooterFastHide && t > footerMaxTranslate + footerViewHeight)
                footerDelta *= 2;
            footerTranslateY = footerTranslateY + footerDelta;
            if (footerTranslateY < footerMinTranslate)
                footerTranslateY = footerMinTranslate;
            footerView.setTranslationY(-footerTranslateY);
        }

        // Going up
        else if (footerDelta > 0 && footerTranslateY < footerMaxTranslate){
            if (t < footerMaxTranslate)
                footerDelta *= 3;
            footerTranslateY = footerTranslateY + footerDelta;
            if (t < 5)
                footerTranslateY = footerMaxTranslate;
            if (footerTranslateY > footerMaxTranslate)
                footerTranslateY = footerMaxTranslate;
            footerView.setTranslationY(-footerTranslateY);
        }
    }



    public void resetHeaderHeight(){
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
    public void resetFooterHeight(){
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



    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}










