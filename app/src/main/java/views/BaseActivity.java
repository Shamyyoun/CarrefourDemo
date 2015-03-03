package views;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import net.tundigital.carrefourdemo.R;

public abstract class BaseActivity extends ActionBarActivity {
    private ImageButton buttonIcon;
    private ImageView imageLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        // customize actionbar view
        buttonIcon = (ImageButton) findViewById(R.id.button_icon);
        imageLogo = (ImageView) findViewById(R.id.image_logo);
    }

    /**
     * abstract method to pass layout resource id from children
     */
    protected abstract int getLayoutResource();

    /**
     * method used to set actionbar icon from children
     */
    protected void setActionBarIcon(int iconRes) {
        if (buttonIcon != null)
            buttonIcon.setImageResource(iconRes);
    }

    /**
     * method used to set actionbar icon click listener
     */
    protected void setActionBarIconClickListener(View.OnClickListener onClickListener) {
        if (buttonIcon != null)
            buttonIcon.setOnClickListener(onClickListener);
    }

    /**
     * method used to set actionbar logo click listener
     */
    protected void setActionBarLogoClickListener(View.OnClickListener onClickListener) {
        if (imageLogo != null)
            imageLogo.setOnClickListener(onClickListener);
    }
}