package utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

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

    /**
     * method used to apply expand animation to view
     */
    public static void expand(final View view, Animation.AnimationListener listener) {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / view.getContext().getResources().getDisplayMetrics().density));
        if (listener != null)
            a.setAnimationListener(listener);
        view.startAnimation(a);
    }

    /**
     * method used to apply collapse animation to view
     */
    public static void collapse(final View view, Animation.AnimationListener listener) {
        final int initialHeight = view.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / view.getContext().getResources().getDisplayMetrics().density));
        if (listener != null)
            a.setAnimationListener(listener);
        view.startAnimation(a);
    }
}
