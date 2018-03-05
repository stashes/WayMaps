package com.waymaps.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waymaps.R;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.Credentials;
import com.waymaps.data.responseEntity.GetGroup;
import com.waymaps.data.responseEntity.User;
import com.waymaps.fragment.AbstractFragment;
import com.waymaps.fragment.FirmListFragment;
import com.waymaps.intent.MainActivityIntent;
import com.waymaps.util.LocalPreferenceManager;
import com.waymaps.util.LocalPreferencesManagerUtil;
import com.waymaps.util.SystemUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    @BindView(R.id.input_login)
    AutoCompleteTextView mLoginView;
    @BindView(R.id.input_pass)
    EditText mPasswordView;
    @BindView(R.id.log_in_btn)
    Button mLoginButton;

    @BindView(R.id.login_progress)
    View mProgressView;
    @BindView(R.id.login_form)
    View mLoginFormView;

    @BindView(R.id.pass_save)
    CheckBox mPassSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initializeViews();
        initializeButtons();
    }

    private void initializeViews() {
        mLoginView.setText(LocalPreferenceManager.getUsername(this));
        mPasswordView.setText(LocalPreferenceManager.getPassword(this));
        mPassSave.setChecked(LocalPreferenceManager.getSavePassword(this));
    }

    private void initializeButtons() {
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }


    @SuppressLint("HardwareIds")
    private void attemptLogin() {

        mLoginView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String login = mLoginView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final boolean savePass = mPassSave.isChecked();


        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_field_pass));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_pass_null));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid login address.
        if (TextUtils.isEmpty(login)) {
            mLoginView.setError(getString(R.string.error_field_login_null));
            focusView = mLoginView;
            cancel = true;
        } else if (!isEmailValid(login)) {
            mLoginView.setError(getString(R.string.error_field_login));
            focusView = mLoginView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            Credentials credentials = getCredentials(login, password);
            RetrofitService.getWayMapsService().loginProcedure(credentials.getAction(), credentials.getPass(), credentials.getOs(),
                    credentials.getLogin(), credentials.getIdentificator(), credentials.getFormat()).enqueue(new Callback<User[]>() {
                @Override
                public void onResponse(Call<User[]> call, Response<User[]> response) {
                    User[] users = response.body();
                    if (users.length == 0) {
                        clearFields();
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Password or login is incorrect",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        showProgress(false);
                    } else {
                        User currentUser = users[0];
                        LocalPreferencesManagerUtil.saveCredentials(login, password, savePass,LoginActivity.this);

                        Toast toast = Toast.makeText(getApplicationContext(),
                                "OK",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        showProgress(false);
                        startActivity(currentUser);
                    }

                }

                @Override
                public void onFailure(Call<User[]> call, Throwable t) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Something went wrong please try again later",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    showProgress(false);
                }
            });

        }
    }

    private void startActivity(User user) {
        MainActivity.authorisedUser = user;
        FirmListFragment firmListFragment = new FirmListFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack("firmList");
        ft.replace(R.id.login_activity_contain, firmListFragment);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initializeViews();
    }

    private void clearFields() {
        mLoginView.setText("");
        mPasswordView.setText("");
    }

    @NonNull
    private Credentials getCredentials(String login, String password) {
        Credentials credentials = new Credentials(Action.LOGIN);
        credentials.setLogin(login);
        credentials.setPass(password);
        credentials.setOs(SystemUtil.getAndroidVersion());
        credentials.setIdentificator(SystemUtil.getWifiMAC(this));
        credentials.setFormat(WayMapsService.DEFAULT_FORMAT);
        return credentials;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

