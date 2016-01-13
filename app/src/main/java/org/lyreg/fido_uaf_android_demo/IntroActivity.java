package org.lyreg.fido_uaf_android_demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.*;
import android.widget.Button;
import android.widget.ProgressBar;

public class IntroActivity extends AppCompatActivity {

    // UI Components
    private View mIntroView;
    private ProgressBar mIntroProgressBar;
    private Button mDiscoveryButton;
    private Button mFidoRegiterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mIntroView = findViewById(R.id.intro_form);
        mIntroProgressBar = (ProgressBar) findViewById(R.id.intro_progress);
        mDiscoveryButton = (Button) findViewById(R.id.discovery_button);
        mFidoRegiterButton = (Button) findViewById(R.id.register_fido_button);

        mDiscoveryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
            }
        });

        mIntroView.setVisibility(View.VISIBLE);
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mIntroView.setVisibility(show ? View.GONE : View.VISIBLE);
            mIntroView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mIntroView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mIntroProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mIntroProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mIntroProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mIntroProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mIntroView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
