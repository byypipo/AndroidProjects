package com.sebatmedikal.activity;

import android.content.Intent;
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
import com.sebatmedikal.mapper.Mapper;
import com.sebatmedikal.remote.domain.Operation;
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
public class OperationsActivity extends BaseActivity {
    private OperationListAdapter operationListAdapter;

    private int lastInitializedPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Operation operation = (Operation) intent.getSerializableExtra("operation");
        if (NullUtil.isNotNull(operation)) {
            operationListClick(operation);
        } else {
            prepareOperationsActivity();
        }
    }

    private void prepareOperationsActivity() {
        if (NullUtil.isNotNull(baseTask)) {
            return;
        }

        LogUtil.logMessage(getClass(), "Prepared Operation List for page " + lastInitializedPageIndex);

        showProgress(true);

        String URL = getServerIp() + getString(R.string.serviceTagOperation);
        RequestModel requestModel = RequestModelGenerator.page(getAccessToken(), lastInitializedPageIndex + "");

        baseTask = new BaseTask(URL, requestModel, new Performer() {
            @Override
            public void perform(boolean success) {
                List<Operation> operationList = Mapper.operationListMapper(baseTask.getContent());
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
                    if (NullUtil.isNull(operationListAdapter)) {
                        View inflatedView = inflate(R.layout.layout_operations);

                        ListView listView = (ListView) inflatedView.findViewById(R.id.layout_operations_listview);
                        SearchView searchView = (SearchView) inflatedView.findViewById(R.id.layout_operations_searchview);

                        operationListAdapter = new OperationListAdapter(getActivity(), operationList);
                        listView.setAdapter(operationListAdapter);

                        Button footerButton = new Button(getActivity());
                        footerButton.setText(getString(R.string.more));
                        listView.addFooterView(footerButton);

                        footerButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LogUtil.logMessage(getClass(), "ONCLICK");
                                prepareOperationsActivity();
                            }
                        });

                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String s) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String s) {
                                operationListAdapter.getFilter().filter(s);
                                return true;
                            }
                        });

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Operation operation = (Operation) operationListAdapter.getItem(i);
                                operationListClick(operation);

                            }
                        });
                    } else {
                        operationListAdapter.addListItems(operationList);
                    }

                    lastInitializedPageIndex++;

                } else {
                    showToast("Operation success: " + success + "\nErrorMessage:" + errorMessage);
                }
            }
        });

        baseTask.execute((Void) null);
    }

    private void operationListClick(Operation operation) {
        if (NullUtil.isNull(operation)) {
            return;
        }

        showProgress(true);

        View view = inflate(R.layout.layout_operations_operation);

        ImageView productImage = (ImageView) view.findViewById(R.id.layout_operation_image);
        TextView productName = (TextView) view.findViewById(R.id.layout_operation_productName);
        TextView brand = (TextView) view.findViewById(R.id.layout_operation_brand);
        TextView note = (TextView) view.findViewById(R.id.layout_operation_note);
        TextView type = (TextView) view.findViewById(R.id.layout_operation_type);
        TextView count = (TextView) view.findViewById(R.id.layout_operation_count);
        TextView createdBy = (TextView) view.findViewById(R.id.layout_operation_createdBy);

        productName.setText(operation.getProduct().getProductName());
        brand.setText(operation.getProduct().getBrand().getBrandName());
        note.setText(operation.getNote());
        type.setText(operation.getOperationType().getOperationTypeName());
        count.setText(operation.getCount() + "");
        createdBy.setText(operation.getCreatedBy());


        if (NullUtil.isNotNull(operation.getProduct().getImage())) {
            Bitmap imageBMP = BitmapFactory.decodeByteArray(operation.getProduct().getImage(), 0, operation.getProduct().getImage().length);
            productImage.setImageBitmap(imageBMP);
        }

        showProgress(false);
    }
}