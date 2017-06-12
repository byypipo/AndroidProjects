package com.sebatmedikal.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sebatmedikal.R;
import com.sebatmedikal.mapper.Mapper;
import com.sebatmedikal.remote.domain.Product;
import com.sebatmedikal.remote.domain.User;
import com.sebatmedikal.remote.model.request.RequestModel;
import com.sebatmedikal.remote.model.request.RequestModelGenerator;
import com.sebatmedikal.task.BaseTask;
import com.sebatmedikal.task.Performer;
import com.sebatmedikal.util.CompareUtil;
import com.sebatmedikal.util.NullUtil;

import java.util.List;

public class AccountSettingsActivity extends BaseActivity {
    private User me;

    private ImageView image;
    private TextView usernameTW;
    private EditText usernameET;
    private TextView passwordTW;
    private EditText passwordET;
    private TextView firstnameTW;
    private EditText firstnameET;
    private TextView lastnameTW;
    private EditText lastnameET;
    private TextView emailTW;
    private EditText emailET;

    private EditText visibledET;
    private TextView gonedTW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareAccountSettingsActivity();
        setUserDetails();
    }

    private void prepareAccountSettingsActivity() {
        currentView = inflate(R.layout.layout_account_settings);

        image = (ImageView) currentView.findViewById(R.id.layout_account_settings_image);
        usernameTW = (TextView) currentView.findViewById(R.id.layout_account_settings_userNameTW);
        usernameET = (EditText) currentView.findViewById(R.id.layout_account_settings_userNameET);
        passwordTW = (TextView) currentView.findViewById(R.id.layout_account_settings_passwordTW);
        passwordET = (EditText) currentView.findViewById(R.id.layout_account_settings_passwordET);
        firstnameTW = (TextView) currentView.findViewById(R.id.layout_account_settings_firstNameTW);
        firstnameET = (EditText) currentView.findViewById(R.id.layout_account_settings_firstNameET);
        lastnameTW = (TextView) currentView.findViewById(R.id.layout_account_settings_lastNameTW);
        lastnameET = (EditText) currentView.findViewById(R.id.layout_account_settings_lastNameET);
        emailTW = (TextView) currentView.findViewById(R.id.layout_account_settings_emailTW);
        emailET = (EditText) currentView.findViewById(R.id.layout_account_settings_emailET);

        Button save = (Button) currentView.findViewById(R.id.layout_account_settings_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTableRow();
                updateAccount();
            }
        });

        usernameTW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTableRow();
                changeTableRow(usernameTW, usernameET);
            }
        });

        passwordTW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTableRow();
                changeTableRow(passwordTW, passwordET);
            }
        });

        firstnameTW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTableRow();
                changeTableRow(firstnameTW, firstnameET);
            }
        });

        lastnameTW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTableRow();
                changeTableRow(lastnameTW, lastnameET);
            }
        });

        emailTW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTableRow();
                changeTableRow(emailTW, emailET);
            }
        });
    }

    private void refreshTableRow() {
        if (NullUtil.isAnyNull(gonedTW, visibledET)) {
            return;
        }

        visibledET.setVisibility(View.GONE);

        gonedTW.setText(visibledET.getText().toString());
        gonedTW.setVisibility(View.VISIBLE);
    }

    private void changeTableRow(TextView goneTW, EditText visibleET) {
        if (NullUtil.isAnyNull(goneTW, visibleET)) {
            return;
        }

        goneTW.setVisibility(View.GONE);
        visibleET.setText(goneTW.getText().toString());
        visibleET.setVisibility(View.VISIBLE);

        gonedTW = goneTW;
        visibledET = visibleET;
    }

    private void setUserDetails() {
        if (NullUtil.isNotNull(baseTask)) {
            return;
        }

        String userid = preferences.getString("userid", null);

        showProgress(true);

        String URL = getString(R.string.serverURL) + getString(R.string.serviceTagUser);
        RequestModel requestModel = RequestModelGenerator.findOne(getAccessToken(), userid);

        baseTask = new BaseTask(URL, requestModel, new Performer() {
            @Override
            public void perform(boolean success) {
                me = Mapper.userMapper(baseTask.getContent());
                String errorMessage = baseTask.getErrorMessage();
                boolean isServerUnreachable = baseTask.isServerUnreachable();

                baseTask = null;
                showProgress(false);

                if (isServerUnreachable) {
                    showToast(getActivityString(R.string.serverUnreachable));
                    return;
                }

                if (success) {
                    prepareContent();
                } else {
                    showToast("Operation success: " + success + "\nErrorMessage:" + errorMessage);
                }
            }
        });

        baseTask.execute((Void) null);
    }

    private void prepareContent() {
        if (NullUtil.isNull(me)) {
            return;
        }

        usernameTW.setText(me.getUsername());
        passwordTW.setText(me.getPassword());
        firstnameTW.setText(me.getFirstName());
        lastnameTW.setText(me.getLastName());

        if (NullUtil.isNotNull(me.getImage())) {
            Bitmap imageBMP = BitmapFactory.decodeByteArray(me.getImage(), 0, me.getImage().length);
            image.setImageBitmap(imageBMP);
        }

        if (NullUtil.isNotNull(me.getEmail())) {
            emailTW.setText(me.getEmail());
        }
    }

    private void updateAccount() {
        if (NullUtil.isNotNull(baseTask)) {
            return;
        }

        boolean update = false;

        if (!CompareUtil.equal(usernameTW.getText().toString(), me.getUsername())) {
            me.setUsername(usernameTW.getText().toString());
            update = true;
        } else if (!CompareUtil.equal(passwordTW.getText().toString(), me.getPassword())) {
            me.setPassword(passwordTW.getText().toString());
            update = true;
        } else if (!CompareUtil.equal(firstnameTW.getText().toString(), me.getFirstName())) {
            me.setFirstName(firstnameTW.getText().toString());
            update = true;
        } else if (!CompareUtil.equal(lastnameTW.getText().toString(), me.getLastName())) {
            me.setLastName(lastnameTW.getText().toString());
            update = true;
        } else if (!CompareUtil.equal(emailTW.getText().toString(), me.getEmail())) {
            me.setEmail(emailTW.getText().toString());
            update = true;
        }

        if (!update) {
            showToast("Not change");
            return;
        }

        String URL = getString(R.string.serverURL) + getString(R.string.serviceTagUser);
        RequestModel requestModel = RequestModelGenerator.userUpdate(getAccessToken(), me);

        baseTask = new BaseTask(URL, requestModel, new Performer() {
            @Override
            public void perform(boolean success) {
                User user = Mapper.userMapper(baseTask.getContent());
                String errorMessage = baseTask.getErrorMessage();
                boolean isServerUnreachable = baseTask.isServerUnreachable();

                baseTask = null;
                showProgress(false);

                if (isServerUnreachable) {
                    showToast(getActivityString(R.string.serverUnreachable));
                    return;
                }

                if (success) {
                    prepareContent();
                    showToast("Account updated");
                } else {
                    showToast("Operation success: " + success + "\nErrorMessage:" + errorMessage);
                }
            }
        });
        baseTask.execute((Void) null);
    }
}