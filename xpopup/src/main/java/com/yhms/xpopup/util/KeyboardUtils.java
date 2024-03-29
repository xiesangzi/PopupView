package com.yhms.xpopup.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.yhms.xpopup.core.BasePopupView;

import java.util.HashMap;

/**
 * Description:
 * Create by dance, at 2018/12/17
 */
public final class KeyboardUtils {

    public static int sDecorViewInvisibleHeightPre;
    private static ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private static HashMap<View, OnSoftInputChangedListener> listenerMap = new HashMap<>();

    private KeyboardUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    private static int sDecorViewDelta = 0;

    private static int getDecorViewInvisibleHeight(final Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        if (decorView == null) {
            return sDecorViewInvisibleHeightPre;
        }
        final Rect outRect = new Rect();
        decorView.getWindowVisibleDisplayFrame(outRect);
        int delta = Math.abs(decorView.getBottom() - outRect.bottom);
        if (delta <= getNavBarHeight()) {
            sDecorViewDelta = delta;
            return 0;
        }
        return delta - sDecorViewDelta;
    }

    /**
     * Register soft input changed listener.
     *
     * @param activity The activity.
     * @param listener The soft input changed listener.
     */
    public static void registerSoftInputChangedListener(final Activity activity, final BasePopupView popupView, final OnSoftInputChangedListener listener) {
        final int flags = activity.getWindow().getAttributes().flags;
        if ((flags & WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) != 0) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        final FrameLayout contentView = activity.findViewById(android.R.id.content);
        sDecorViewInvisibleHeightPre = getDecorViewInvisibleHeight(activity);
        listenerMap.put(popupView, listener);
        ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = () -> {
            int height = getDecorViewInvisibleHeight(activity);
            if (sDecorViewInvisibleHeightPre != height) {
                //通知所有弹窗的监听器输入法高度变化了
                for (OnSoftInputChangedListener changedListener : listenerMap.values()) {
                    changedListener.onSoftInputChanged(height);
                }
                sDecorViewInvisibleHeightPre = height;
            }
        };
        contentView.getViewTreeObserver()
                .addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    public static void removeLayoutChangeListener(View decorView, BasePopupView popupView) {
        View contentView = decorView.findViewById(android.R.id.content);
        contentView.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
        onGlobalLayoutListener = null;
        listenerMap.remove(popupView);
    }

    private static int getNavBarHeight() {
        Resources res = Resources.getSystem();
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId != 0) {
            return res.getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    public static void showSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface OnSoftInputChangedListener {
        void onSoftInputChanged(int height);
    }
}