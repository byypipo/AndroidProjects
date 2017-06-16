package com.sebatmedikal.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.sebatmedikal.R;

/**
 * Created by orhan on 16.06.2017.
 */
public class SettingsActivity extends BaseActivity {
    private Switch notifications;
    private AutoCompleteTextView serverIp;
    private Button settingsFormat;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareSettingsActivity();
    }

    private void prepareSettingsActivity() {
        View inflatedView = inflate(R.layout.layout_settings);

        notifications = (Switch) inflatedView.findViewById(R.id.layout_settings_notofications);
        serverIp = (AutoCompleteTextView) inflatedView.findViewById(R.id.layout_settings_serverIp);
        settingsFormat = (Button) inflatedView.findViewById(R.id.layout_settings_settingsFormat);
        save = (Button) inflatedView.findViewById(R.id.layout_settings_save);

        notifications.setChecked(isNotificationEnable());
        serverIp.setText(getServerIp());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });

        settingsFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formatSettings();
            }
        });
    }

    private void saveSettings() {
        updateFormat();
        finish();
    }

    private void formatSettings() {
        notifications.setChecked(true);
        serverIp.setText(getString(R.string.serverURL));

        updateFormat();
    }

    private void updateFormat() {
        setEditor("notificationEnable", notifications.isChecked() + "");
        setEditor("serverIp", serverIp.getText().toString());
    }
}
