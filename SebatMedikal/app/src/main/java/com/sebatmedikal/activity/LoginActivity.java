package com.sebatmedikal.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sebatmedikal.R;
import com.sebatmedikal.remote.domain.User;
import com.sebatmedikal.remote.model.request.RequestModel;
import com.sebatmedikal.remote.model.request.RequestModelGenerator;
import com.sebatmedikal.task.BaseTask;
import com.sebatmedikal.task.Performer;
import com.sebatmedikal.util.LogUtil;
import com.sebatmedikal.util.NullUtil;

import java.util.logging.Level;

public class LoginActivity extends BaseActivity {
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private CheckBox mRememberMe;

    private String fcmRegistrationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fcmRegistrationId = FirebaseInstanceId.getInstance().getToken();
        LogUtil.logMessage(getClass(), "fcmRegistrationId: " + fcmRegistrationId);

        FirebaseMessaging.getInstance().subscribeToTopic("news");

        disabledNavigationView();
        prepareLoginActivity();
    }

    private void prepareLoginActivity() {
        View inflatedView = inflate(R.layout.layout_login);

        mUsernameView = (AutoCompleteTextView) inflatedView.findViewById(R.id.login_username);
        mPasswordView = (EditText) inflatedView.findViewById(R.id.login_password);
        mRememberMe = (CheckBox) inflatedView.findViewById(R.id.login_rememberMe);

        Button loginButton = (Button) inflatedView.findViewById(R.id.login_loginButton);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        String sharedUserNameString = preferences.getString("username", null);
        String sharedPasswordString = preferences.getString("password", null);
        boolean sharedRememberMe = preferences.getBoolean("rememberMe", false);
        boolean login = preferences.getBoolean("login", false);

        if (sharedRememberMe) {
            mUsernameView.setText(sharedUserNameString);
            mPasswordView.setText(sharedPasswordString);
            mRememberMe.setChecked(true);

            if (NullUtil.isNotNull(sharedUserNameString)) {
                mPasswordView.requestFocus();
            }
        }

        if (login) {
            attemptLogin();
        }
    }

    private void attemptLogin() {
        if (NullUtil.isNotNull(baseTask)) {
            return;
        }

        mUsernameView.setError(null);
        mPasswordView.setError(null);

        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.empty_username));
            focusView = mUsernameView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.empty_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            String URL = getServerIp() + getString(R.string.serviceTagUser);
            LogUtil.logMessage(getClass(), "URL: " + URL);
            RequestModel requestModel = RequestModelGenerator.userLogin(username, password, fcmRegistrationId);

            baseTask = new BaseTask(URL, requestModel, new Performer() {
                @Override
                public void perform(boolean success) {
                    boolean isServerUnreachable = baseTask.isServerUnreachable();
                    User user = (User) baseTask.getContent();
                    String accessToken = baseTask.getAccessToken();
                    String errorMessage = baseTask.getErrorMessage();

                    baseTask = null;
                    showProgress(false);

                    if (isServerUnreachable) {
                        showToast(getActivityString(R.string.serverUnreachable));
                        return;
                    }

                    if (success) {
                        preparedSharedPreferences(user, accessToken);

                        if (mRememberMe.isChecked()) {
                            editor.putString("password", mPasswordView.getText().toString());
                            editor.putBoolean("rememberMe", true);
                            editor.putBoolean("login", true);
                        } else {
                            editor.remove("password");
                            editor.remove("rememberMe");
                            editor.remove("login");
                        }
                        editor.commit();

                        LogUtil.logMessage(LoginActivity.class, Level.INFO, "Login successfully");
                        goToActivity(OperationsActivity.class);
                        mPasswordView.setText("");
                    } else {
                        showToast("Operation success: " + success + "\nErrorMessage:" + errorMessage);

                        mPasswordView.setError(getString(R.string.invalid_password));
                        mPasswordView.requestFocus();
                    }
                }
            });
            baseTask.execute((Void) null);
        }
    }
}

