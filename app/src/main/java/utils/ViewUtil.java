package utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class ViewUtil {
    /**
     * method used to show or hide view slide_in default duration 250
     */
    public static void showView(final View view, boolean show) {
        fadeView(view, show, 250);
    }

    /**
     * method used to show or hide view slide_in passed duration
     */
    public static void showView(final View view, boolean show, int animDuration) {
        fadeView(view, show, animDuration);
    }

    /**
     * method used to shows or hides a view with a smooth animation slide_in specific duration
     */
    private static void fadeView(final View view, final boolean show, int animDuration) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
        view.animate().setDuration(animDuration).alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }

    /**
     * method used to shows first view and hide other views
     */
    public static void showView(View viewToShow, View... viewsToHide) {
        showView(viewToShow, true);
        for (View view : viewsToHide) {
            showView(view, false);
        }
    }
}
