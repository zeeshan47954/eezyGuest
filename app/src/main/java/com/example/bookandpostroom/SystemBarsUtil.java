package com.example.bookandpostroom;

import android.app.Activity;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SystemBarsUtil {

    private Activity activity;
    private WindowInsetsCompat windowInsets;

    public SystemBarsUtil(Activity activity) {
        this.activity = activity;
    }

    // Initialize and listen for window insets
    public void initialize(final OnInsetsAvailableListener listener) {
        View decorView = activity.getWindow().getDecorView();

        ViewCompat.setOnApplyWindowInsetsListener(decorView, (v, insets) -> {
            windowInsets = insets;
            if (listener != null) {
                listener.onInsetsAvailable();
            }
            return insets;
        });

        // Request to apply insets
        ViewCompat.requestApplyInsets(decorView);
    }

    // Get status bar height using WindowInsets
    public int getStatusBarHeight() {
        if (windowInsets != null) {
            return windowInsets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
        }

        // Fallback to resources method
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return activity.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    // Get navigation bar height using WindowInsets
    public int getNavigationBarHeight() {
        if (windowInsets != null) {
            return windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
        }

        // Fallback to resources method
        int resourceId = activity.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return activity.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    // Check if device has navigation bar (inferred from non-zero height)
    public boolean hasNavigationBar() {
        return getNavigationBarHeight() > 0;
    }

    // Get actual navigation bar height (returns 0 if no nav bar)
    public int getActualNavigationBarHeight() {
        if (hasNavigationBar()) {
            return getNavigationBarHeight();
        }
        return 0;
    }

    // Interface for callback when insets are available
    public interface OnInsetsAvailableListener {
        void onInsetsAvailable();
    }
}