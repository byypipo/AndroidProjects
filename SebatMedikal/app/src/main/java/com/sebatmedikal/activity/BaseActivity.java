package com.sebatmedikal.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageButton;
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
import com.sebatmedikal.remote.domain.User;
import com.sebatmedikal.remote.model.request.RequestModel;
import com.sebatmedikal.remote.model.request.RequestModelGenerator;
import com.sebatmedikal.task.BaseTask;
import com.sebatmedikal.task.Performer;
import com.sebatmedikal.util.CompareUtil;
import com.sebatmedikal.util.LogUtil;
import com.sebatmedikal.util.NullUtil;

import java.util.Date;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected static SharedPreferences preferences;
    protected static SharedPreferences.Editor editor;

    protected RelativeLayout mProgressView;
    protected LinearLayout mEmptyView;
    protected View currentView;

    private DrawerLayout drawer;
    protected BaseTask baseTask = null;

    protected ViewQueue viewQueue = new ViewQueue();

    protected static int RESULT_LOAD_IMAGE = 1;

    protected ImageButton save;

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

        if (NullUtil.isNull(preferences)) {
            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            editor = preferences.edit();
        }

        prepareNavigationView();

        mProgressView = (RelativeLayout) findViewById(R.id.content_main_progress_layout);
        mEmptyView = (LinearLayout) findViewById(R.id.content_main_empty_layout);
    }

    protected void change(boolean changed) {
        if (NullUtil.isNull(save)) {
            return;
        }

        int drawable;
        if (changed) {
            drawable = R.drawable.save_black;
        } else {
            drawable = R.drawable.save_white;
        }

        save.setBackgroundResource(drawable);
        save.setTag(drawable);
    }

    protected boolean isChange() {
        if (NullUtil.isNull(save)) {
            return false;
        }

        if (CompareUtil.equal(save.getTag(), R.drawable.save_black)) {
            return true;
        }

        return false;
    }

    protected TextWatcher defaultTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            change(true);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    protected View.OnClickListener imageClickListenerWithPermission = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            inflateSelectImageLayout(true);
        }
    };

    protected View.OnClickListener imageClickListenerWithoutPermission = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            inflateSelectImageLayout(false);
        }
    };

    private void prepareNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);

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

        CircleImageView imageView = (CircleImageView) hView.findViewById(R.id.nav_bar_image);
        if (NullUtil.isNotNull(imageString)) {
            byte[] image = Base64.decode(imageString, Base64.DEFAULT);
            Bitmap imageBMP = BitmapFactory.decodeByteArray(image, 0, image.length);
            imageView.setImageBitmap(imageBMP);
        } else {
            imageView.setImageBitmap(null);
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
            return;
        }

        View previousView = viewQueue.getPreviousView();
        if (NullUtil.isNotNull(previousView)) {
            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.content_main_empty_layout);
            viewGroup.removeAllViews();
            viewGroup.addView(previousView);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            goToActivity(SettingsActivity.class);
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
        } else if (id == R.id.nav_brands) {
            goToActivity(BrandsActivity.class);
        } else if (id == R.id.nav_exit) {
            logout();

            finish();
        }

        return true;
    }

    protected void logout() {
        if (NullUtil.isNotNull(baseTask)) {
            return;
        }

        String URL = getServerIp() + getString(R.string.serviceTagUser);
        LogUtil.logMessage(getClass(), "URL: " + URL);
        RequestModel requestModel = RequestModelGenerator.userLogout(preferences.getString("accessToken", null), preferences.getString("username", null));
        LogUtil.logMessage(getClass(), "CONTENT: " + requestModel.toString());

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

        goToActivity(LoginActivity.class);
    }

    protected View inflate(int layout) {
        LayoutInflater layoutInflater = getLayoutInflater();

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.content_main_empty_layout);
        View oldView = viewGroup.getChildAt(0);
        if (NullUtil.isNotNull(oldView)) {
            viewQueue.addView(oldView);
        }

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

    protected String getAccessToken() {
        return preferences.getString("accessToken", null);
    }

    protected void goToActivity(Class<?> clazz) {
        LogUtil.logMessage(getClass(), "Loading " + clazz.getName());
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    protected void preparedSharedPreferences(User user) {
        preparedSharedPreferences(user, null);
    }

    protected void preparedSharedPreferences(User user, String accessToken) {
        editor.putString("username", user.getUsername());
        editor.putString("firstname", user.getFirstName());
        editor.putString("lastname", user.getLastName());
        editor.putString("fullname", user.getFirstName() + " " + user.getLastName());
        editor.putString("role", user.getRole().getRoleName());
        editor.putString("roleid", user.getRole().getId() + "");
        editor.putString("userid", user.getId() + "");

        if (NullUtil.isNotNull(user.getReadedBrandsDate())) {
            editor.putLong("readedBrandsDate", user.getReadedBrandsDate().getTime());
        }
        if (NullUtil.isNotNull(user.getReadedOperationsDate())) {
            editor.putLong("readedOperationsDate", user.getReadedOperationsDate().getTime());
        }

        if (NullUtil.isNotNull(user.getReadedProductsDate())) {
            editor.putLong("readedProductsDate", user.getReadedProductsDate().getTime());
        }

        if (NullUtil.isNotNull(accessToken)) {
            editor.putString("accessToken", accessToken);
        }

        if (NullUtil.isNotNull(user.getImage())) {
            String image = Base64.encodeToString(user.getImage(), Base64.DEFAULT);
            editor.putString("image", image);
        }

        editor.commit();
        prepareNavigationView();
    }

    protected void setEditor(String key, Object value) {
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        editor.commit();
    }

    protected boolean isNotificationEnable() {
        //TODO: IGNORED
        return CompareUtil.isTrue(preferences.getString("notificationEnable", "true"));
    }

    protected String getServerIp() {
        String serverIp = preferences.getString("serverIp", null);

        LogUtil.logMessage(getClass(), "Preferences serverIp: " + serverIp);
        if (NullUtil.isNull(serverIp)) {
            serverIp = getString(R.string.serverURL);
            setEditor("serverIp", serverIp);
            LogUtil.logMessage(getClass(), "Locale serverIp: " + serverIp);
        }

        LogUtil.logMessage(getClass(), "Return serverIp: " + serverIp);
        return serverIp;
    }

    //camera
    protected void inflateSelectImageLayout(boolean checkPermission) {
        if (checkPermission) {
            if (!CompareUtil.equal(preferences.getString("roleid", null), getString(R.string.roleAdmin))) {
                showToast(getString(R.string.imagePermission) + " " + getString(R.string.onlyAdmin));
                return;
            }
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.imageSource));
        alertDialogBuilder
                .setMessage(getString(R.string.pleaseSelectImageSource))
                .setPositiveButton(R.string.gallery, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(
                                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    }
                })
                .setNegativeButton(R.string.camera, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
