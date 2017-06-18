package com.sebatmedikal.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import com.sebatmedikal.R;

/**
 * Created by orhan on 16.06.2017.
 */
public class SettingsActivity extends BaseActivity {
    private Switch notifications;
    private AutoCompleteTextView serverIp;
    private ImageButton settingsFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareSettingsActivity();
    }

    private void prepareSettingsActivity() {
        View inflatedView = inflate(R.layout.layout_settings);

        notifications = (Switch) inflatedView.findViewById(R.id.layout_settings_notofications);
        serverIp = (AutoCompleteTextView) inflatedView.findViewById(R.id.layout_settings_serverIp);
        settingsFormat = (ImageButton) inflatedView.findViewById(R.id.layout_settings_settingsFormat);
        save = (ImageButton) inflatedView.findViewById(R.id.layout_settings_save);

        serverIp.setText(getServerIp());
        serverIp.addTextChangedListener(defaultTextWatcher);

        notifications.setChecked(isNotificationEnable());
        notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                change(true);
            }
        });

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
    }

    private void formatSettings() {
        notifications.setChecked(true);
        serverIp.setText(getString(R.string.serverURL));

        updateFormat();
    }

    private void updateFormat() {
        setEditor("notificationEnable", notifications.isChecked() + "");
        setEditor("serverIp", serverIp.getText().toString());
        change(false);
    }
}
