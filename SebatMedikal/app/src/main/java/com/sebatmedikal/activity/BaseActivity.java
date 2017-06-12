package com.sebatmedikal.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.sebatmedikal.R;
import com.sebatmedikal.external.CircleImageView;
import com.sebatmedikal.remote.model.request.RequestModel;
import com.sebatmedikal.remote.model.request.RequestModelGenerator;
import com.sebatmedikal.task.BaseTask;
import com.sebatmedikal.task.Performer;
import com.sebatmedikal.util.LogUtil;
import com.sebatmedikal.util.NotificationUtil;
import com.sebatmedikal.util.NullUtil;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected SharedPreferences preferences;
    protected SharedPreferences.Editor editor;

    protected RelativeLayout mProgressView;
    protected LinearLayout mEmptyView;
    protected View currentView;

    private int test_NotificationId = 1;

    private DrawerLayout drawer;

    protected BaseTask baseTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        prepareNavigationView();

        mProgressView = (RelativeLayout) findViewById(R.id.content_main_progress_layout);
        mEmptyView = (LinearLayout) findViewById(R.id.content_main_empty_layout);
    }

    private void prepareNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();

        String userFullName = preferences.getString("fullname", null);
        String role = preferences.getString("role", null);
        String imageString = preferences.getString("image", null);

        if (NullUtil.isNotNull(userFullName)) {
            TextView username = (TextView) hView.findViewById(R.id.nav_header_user_name);
            username.setText(userFullName);
        }

        if (NullUtil.isNotNull(role)) {
            TextView usertype = (TextView) hView.findViewById(R.id.nav_header_user_type);
            usertype.setText(role);
        }

        if (NullUtil.isNotNull(imageString)) {
            byte[] image = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap imageBMP = BitmapFactory.decodeByteArray(image, 0, image.length);

            CircleImageView imageView = (CircleImageView) hView.findViewById(R.id.nav_bar_image);
            imageView.setImageBitmap(imageBMP);
        }
    }

    protected void disabledNavigationView() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mEmptyView.setVisibility(show ? View.GONE : View.VISIBLE);
            mEmptyView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mEmptyView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mEmptyView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        int id = item.getItemId();

        if (id == R.id.nav_main) {
            goToActivity(OperationsActivity.class);
        } else if (id == R.id.nav_products) {
            goToActivity(ProductsActivity.class);
        } else if (id == R.id.nav_users) {
            goToActivity(UsersActivity.class);
        } else if (id == R.id.nav_accountSettings) {
            goToActivity(AccountSettingsActivity.class);
        } else if (id == R.id.nav_messages) {
//            NotificationUtil.test_SimpleNotification(this, test_NotificationId++, "TITLE", "CONTENT: byypipo");
//            NotificationUtil.test_ExpandedLayoutNotification(this);
            NotificationUtil.test_InlineReplyNotification(this,test_NotificationId++);
        } else if (id == R.id.nav_valueSettings) {

        } else if (id == R.id.nav_exit) {
            if (getActivity() instanceof LoginActivity) {
                String URL = getString(R.string.serverURL) + getString(R.string.serviceTagUser);
                LogUtil.logMessage(getClass(), "URL: " + URL);
                RequestModel requestModel = RequestModelGenerator.userLogout(preferences.getString("accessToken", null), preferences.getString("username", null));
                LogUtil.logMessage(getClass(), "CONTENT: " + requestModel.toString());

                if (NullUtil.isNotNull(baseTask)) {
                    return false;
                }

                baseTask = new BaseTask(URL, requestModel, new Performer() {
                    @Override
                    public void perform(boolean success) {
                        LogUtil.logMessage(BaseTask.class, "success: " + success);
                        baseTask = null;
                    }
                });

                baseTask.execute((Void) null);

                editor.remove("password");
                editor.remove("login");
                editor.remove("lastname");
                editor.remove("fullname");
                editor.remove("role");
                editor.remove("image");
                editor.commit();
            } else {
                LogUtil.logMessage(getClass(), "else " + getActivity());
            }
            LogUtil.logMessage(getClass(), "finally " + getActivity());

            finish();
        }

        return true;
    }

    protected View inflate(int layout) {
        LayoutInflater layoutInflater = getLayoutInflater();

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.content_main_empty_layout);
        viewGroup.removeAllViews();
        return layoutInflater.inflate(layout, viewGroup);
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public BaseActivity getActivity() {
        return this;
    }

    public String getActivityString(int id) {
        return getString(id);
    }

    public String getAccessToken() {
        return preferences.getString("accessToken", null);
    }

    protected void goToActivity(Class<?> clazz) {
        LogUtil.logMessage(getClass(), "Loading " + clazz.getName());
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }
}
