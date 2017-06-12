package com.sebatmedikal.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.sebatmedikal.R;
import com.sebatmedikal.adapter.OperationListAdapter;
import com.sebatmedikal.adapter.UserListAdapter;
import com.sebatmedikal.mapper.Mapper;
import com.sebatmedikal.remote.domain.Operation;
import com.sebatmedikal.remote.domain.User;
import com.sebatmedikal.remote.model.request.RequestModel;
import com.sebatmedikal.remote.model.request.RequestModelGenerator;
import com.sebatmedikal.task.BaseTask;
import com.sebatmedikal.task.Performer;
import com.sebatmedikal.util.LogUtil;
import com.sebatmedikal.util.NullUtil;

import java.util.List;

/**
 * Created by orhan on 26.05.2017.
 */
public class UsersActivity extends BaseActivity {
    private UserListAdapter userListAdapter;

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

        String URL = getString(R.string.serverURL) + getString(R.string.serviceTagUser);
        RequestModel requestModel = RequestModelGenerator.findAll(getAccessToken());

        baseTask = new BaseTask(URL, requestModel, new Performer() {
            @Override
            public void perform(boolean success) {
                List<User> userList = Mapper.userListMapper(baseTask.getContent());
                String errorMessage = baseTask.getErrorMessage();
                boolean isServerUnreachable = baseTask.isServerUnreachable();

                baseTask = null;
                showProgress(false);

                if (isServerUnreachable) {
                    showToast(getActivityString(R.string.serverUnreachable));
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
                } else {
                    showToast("Operation success: " + success + "\nErrorMessage:" + errorMessage);
                }
            }
        });

        baseTask.execute((Void) null);
    }

    private void userListClick(User user) {
        if (NullUtil.isNull(user)) {
            return;
        }

        showProgress(true);

        //TODO:

        showProgress(false);
    }
}