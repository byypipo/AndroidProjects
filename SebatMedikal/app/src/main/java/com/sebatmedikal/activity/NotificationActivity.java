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
import com.sebatmedikal.remote.domain.User;
import com.sebatmedikal.remote.model.request.RequestModel;
import com.sebatmedikal.remote.model.request.RequestModelGenerator;
import com.sebatmedikal.task.BaseTask;
import com.sebatmedikal.task.Performer;
import com.sebatmedikal.util.CompareUtil;
import com.sebatmedikal.util.NullUtil;

public class NotificationActivity extends BaseActivity {
    TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareNotificationActivity();
    }

    private void prepareNotificationActivity() {
        currentView = inflate(R.layout.layout_notifications);

        content = (TextView) currentView.findViewById(R.id.layout_notifications_content);
        content.setText("byypipo");
    }
}
