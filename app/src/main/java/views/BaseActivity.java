package views;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageButton;

import net.tundigital.carrefourdemo.R;

public abstract class BaseActivity extends ActionBarActivity {
    private ImageButton buttonIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        // customize actionbar view
        buttonIcon = (ImageButton) findViewById(R.id.button_icon);
    }

    /**
     * abstract method to pass layout resource id from children
     */
    protected abstract int getLayoutResource();

    /**
     * method used to set actionbar icon from children
     */
    protected void setActionBarIcon(int iconRes) {
        buttonIcon.setImageResource(iconRes);
    }

    /**
     * method used to set actionbar icon click listener
     */
    protected void setActionBarIconClickListener(View.OnClickListener onClickListener) {
        buttonIcon.setOnClickListener(onClickListener);
    }
}