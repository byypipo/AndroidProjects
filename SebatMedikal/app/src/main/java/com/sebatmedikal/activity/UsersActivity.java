package com.sebatmedikal.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sebatmedikal.R;
import com.sebatmedikal.adapter.OperationListAdapter;
import com.sebatmedikal.adapter.UserListAdapter;
import com.sebatmedikal.mapper.Mapper;
import com.sebatmedikal.remote.domain.Operation;
import com.sebatmedikal.remote.domain.Role;
import com.sebatmedikal.remote.domain.User;
import com.sebatmedikal.remote.model.request.RequestModel;
import com.sebatmedikal.remote.model.request.RequestModelGenerator;
import com.sebatmedikal.task.BaseTask;
import com.sebatmedikal.task.Performer;
import com.sebatmedikal.util.CompareUtil;
import com.sebatmedikal.util.LogUtil;
import com.sebatmedikal.util.NullUtil;

import java.util.List;

/**
 * Created by orhan on 26.05.2017.
 */
public class UsersActivity extends BaseActivity {
    private UserListAdapter userListAdapter;

    AutoCompleteTextView new_user_username;
    AutoCompleteTextView new_user_password;
    Spinner new_user_role;
    AutoCompleteTextView new_user_firstname;
    AutoCompleteTextView new_user_lastname;
    AutoCompleteTextView new_user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareUsersActivity();
    }

    private void prepareUsersActivity() {
        if (NullUtil.isNotNull(baseTask)) {
            return;
        }

        showProgress(true);

        String URL = getServerIp() + getString(R.string.serviceTagUser);
        RequestModel requestModel = RequestModelGenerator.findAll(getAccessToken());

        baseTask = new BaseTask(URL, requestModel, new Performer() {
            @Override
            public void perform(boolean success) {
                List<User> userList = Mapper.userListMapper(baseTask.getContent());
                String errorMessage = baseTask.getErrorMessage();
                boolean isServerUnreachable = baseTask.isServerUnreachable();
                boolean isLogout = baseTask.isLogout();

                baseTask = null;
                showProgress(false);

                if (isServerUnreachable) {
                    showToast(getActivityString(R.string.serverUnreachable));
                    return;
                }

                if (isLogout) {
                    showToast(getActivityString(R.string.userLogout));
                    logout();
                    return;
                }

                if (success) {
                    View inflatedView = inflate(R.layout.layout_users);

                    ListView listView = (ListView) inflatedView.findViewById(R.id.layout_users_listview);
                    SearchView searchView = (SearchView) inflatedView.findViewById(R.id.layout_users_searchview);

                    userListAdapter = new UserListAdapter(getActivity(), userList);
                    listView.setAdapter(userListAdapter);

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String s) {
                            userListAdapter.getFilter().filter(s);
                            return true;
                        }
                    });

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            User user = (User) userListAdapter.getItem(i);
                            userListClick(user);

                        }
                    });

                    if (CompareUtil.equal(preferences.getString("roleid", null), getString(R.string.roleAdmin))) {
                        ImageButton addNewUser = (ImageButton) findViewById(R.id.layout_users_new_user);
                        addNewUser.setVisibility(View.VISIBLE);

                        addNewUser.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadNewUserLayout();
                            }
                        });
                    }

                } else {
                    showToast("Operation success: " + success + "\nErrorMessage:" + errorMessage);
                }
            }
        });

        baseTask.execute((Void) null);
    }

    private void loadNewUserLayout() {
        View layout_new_user = inflate(R.layout.layout_new_user);

        new_user_username = (AutoCompleteTextView) layout_new_user.findViewById(R.id.layout_new_user_userName);
        new_user_password = (AutoCompleteTextView) layout_new_user.findViewById(R.id.layout_new_user_password);
        new_user_role = (Spinner) layout_new_user.findViewById(R.id.layout_new_user_role);
        new_user_firstname = (AutoCompleteTextView) layout_new_user.findViewById(R.id.layout_new_user_firstName);
        new_user_lastname = (AutoCompleteTextView) layout_new_user.findViewById(R.id.layout_new_user_lastName);
        new_user_email = (AutoCompleteTextView) layout_new_user.findViewById(R.id.layout_new_user_email);

        new_user_username.addTextChangedListener(defaultTextWatcher);
        new_user_password.addTextChangedListener(defaultTextWatcher);
        new_user_firstname.addTextChangedListener(defaultTextWatcher);
        new_user_lastname.addTextChangedListener(defaultTextWatcher);
        new_user_email.addTextChangedListener(defaultTextWatcher);

        save = (ImageButton) layout_new_user.findViewById(R.id.layout_new_user_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewUserProcess();
            }
        });
    }

    private void userListClick(User user) {
        if (NullUtil.isNull(user)) {
            return;
        }

        showProgress(true);

        //TODO:

        showProgress(false);
    }

    private void addNewUserProcess() {
        if (NullUtil.isNotNull(baseTask)) {
            LogUtil.logMessage(getClass(), "Basetask not null");
            return;
        }

        showProgress(true);

        String username = new_user_username.getText().toString();
        String password = new_user_password.getText().toString();

        Role role = new Role();
        role.setId(2);
        if (CompareUtil.equalIgnoreCase("Admin", new_user_role.getSelectedItem().toString())) {
            role.setId(1);
        }
        String firstname = new_user_firstname.getText().toString();
        String lastname = new_user_lastname.getText().toString();
        String email = new_user_email.getText().toString();

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFirstName(firstname);
        newUser.setLastName(lastname);
        newUser.setEmail(email);
        newUser.setRole(role);

        String URL = getServerIp() + getString(R.string.serviceTagUser);
        RequestModel requestModel = RequestModelGenerator.userCreate(getAccessToken(), newUser);

        baseTask = new BaseTask(URL, requestModel, new Performer() {
            @Override
            public void perform(boolean success) {
                String errorMessage = baseTask.getErrorMessage();
                boolean isServerUnreachable = baseTask.isServerUnreachable();
                boolean isLogout = baseTask.isLogout();

                baseTask = null;
                showProgress(false);

                if (isServerUnreachable) {
                    showToast(getActivityString(R.string.serverUnreachable));
                    return;
                }

                if (isLogout) {
                    showToast(getActivityString(R.string.userLogout));
                    logout();
                    return;
                }

                if (success) {
                    showToast("User created");
                    change(false);
                } else {
                    showToast("User success: " + success + "\nErrorMessage:" + errorMessage);
                }

                prepareUsersActivity();
            }
        });

        baseTask.execute((Void) null);
    }
}