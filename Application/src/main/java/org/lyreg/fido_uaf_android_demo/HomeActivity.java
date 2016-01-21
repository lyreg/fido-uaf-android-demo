package org.lyreg.fido_uaf_android_demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class HomeActivity extends BaseActivity {

    private View mHomeFormView;
    private ProgressBar mHomeProgressBar;
    private TextView mUserTextView;
    private TextView mLastLogginTextView;
    private Button mTransactionsButton;
    private Button mDeregistrationButton;
    private Button mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHomeFormView = findViewById(R.id.home_form);
        mHomeProgressBar = (ProgressBar) findViewById(R.id.home_progress);
        mUserTextView = (TextView) findViewById(R.id.textView_user);
        mLastLogginTextView = (TextView) findViewById(R.id.textView_last_logged_in_date);
        mTransactionsButton = (Button) findViewById(R.id.transactions_button);
        mDeregistrationButton = (Button) findViewById(R.id.deregistration_button);
        mLogoutButton = (Button) findViewById(R.id.logout_button);

        String username = getIntent().getExtras().getString("USERNAME");
        String lastLoggedIn = getIntent().getExtras().getString("LAST_LOGGED_IN");
        mUserTextView.setText(username);
        mLastLogginTextView.setText(lastLoggedIn);

        mTransactionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mDeregistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptDeregistration();
            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogout();
            }
        });
    }

    private void attemptDeregistration() {

    }

    /**
     * Logout
     */
    private void attemptLogout() {

        finish();
    }


    /***
     * This is a default implementation of the FIDO activity callback when the FIDO client
     * calls back to this app and the response has been unsuccessful.
     *
     * @param errorMsg
     */
    protected void onActivityResultFailure(String errorMsg) {}

    /***
     * This is a default implementation of the FIDO activity callback when the FIDO client
     * calls back to this app and the response has been successful.
     *
     * @param uafResponseJson
     */
    protected void processUafClientResponse(String uafResponseJson) {}

    /**
     * Shows the progress UI and hides the home form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mHomeFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mHomeFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mHomeFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mHomeProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mHomeProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mHomeProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mHomeProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mHomeFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}
