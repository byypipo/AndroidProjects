package com.sebatmedikal.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.sebatmedikal.R;
import com.sebatmedikal.adapter.ProductListAdapter;
import com.sebatmedikal.mapper.Mapper;
import com.sebatmedikal.remote.domain.Product;
import com.sebatmedikal.remote.model.request.RequestModel;
import com.sebatmedikal.remote.model.request.RequestModelGenerator;
import com.sebatmedikal.task.BaseTask;
import com.sebatmedikal.task.Performer;
import com.sebatmedikal.util.CompareUtil;
import com.sebatmedikal.util.LogUtil;
import com.sebatmedikal.util.NullUtil;

import java.util.List;

public class ProductsActivity extends BaseActivity {
    private ProductListAdapter productListAdapter;

    private Product selectedProduct;
    private EditText operationCount;
    private EditText operationNote;
    private TextView operationTotalPrice;
    private Spinner operationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareProductsActivity();
    }

    private void prepareProductsActivity() {
        if (NullUtil.isNotNull(baseTask)) {
            return;
        }

        selectedProduct = null;
        showProgress(true);

        String URL = getString(R.string.serverURL) + getString(R.string.serviceTagProduct);
        RequestModel requestModel = RequestModelGenerator.findAll(getAccessToken());

        baseTask = new BaseTask(URL, requestModel, new Performer() {
            @Override
            public void perform(boolean success) {
                List<Product> productList = Mapper.productListMapper(baseTask.getContent());
                String errorMessage = baseTask.getErrorMessage();
                boolean isServerUnreachable = baseTask.isServerUnreachable();

                baseTask = null;
                showProgress(false);

                if (isServerUnreachable) {
                    showToast(getActivityString(R.string.serverUnreachable));
                    return;
                }

                if (success) {
                    currentView = inflate(R.layout.layout_products);

                    ListView listView = (ListView) currentView.findViewById(R.id.layout_products_listview);
                    SearchView searchView = (SearchView) currentView.findViewById(R.id.layout_products_searchview);

                    productListAdapter = new ProductListAdapter(getActivity(), productList);

                    listView.setAdapter(productListAdapter);

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String s) {
                            productListAdapter.getFilter().filter(s);
                            return true;
                        }
                    });

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Product product = (Product) productListAdapter.getItem(i);
                            productListClick(product);

                        }
                    });
                } else {
                    showToast("Operation success: " + success + "\nErrorMessage:" + errorMessage);
                }
            }
        });

        baseTask.execute((Void) null);
    }

    private void productListClick(Product product) {
        if (NullUtil.isNull(product)) {
            return;
        }

        showProgress(true);
        selectedProduct = product;

        currentView = inflate(R.layout.layout_products_product);

        ImageView productImage = (ImageView) currentView.findViewById(R.id.layout_product_image);
        TextView productName = (TextView) currentView.findViewById(R.id.layout_product_productName);
        TextView brand = (TextView) currentView.findViewById(R.id.layout_product_brand);
        TextView note = (TextView) currentView.findViewById(R.id.layout_product_note);
        TextView stock = (TextView) currentView.findViewById(R.id.layout_product_stock);
        TextView price = (TextView) currentView.findViewById(R.id.layout_product_price);
        Button newOperation = (Button) currentView.findViewById(R.id.layout_product_new_operation_button);

        productName.setText(product.getProductName());
        brand.setText(product.getBrand().getBrandName());
        note.setText(product.getNote());
        stock.setText(product.getStock().getCount() + "");

        String priceString = "0";
        if (NullUtil.isNotNull(product.getPrice())) {
            priceString = product.getPrice().longValue() + "";
        }
        price.setText(priceString);

        if (NullUtil.isNotNull(product.getImage())) {
            Bitmap imageBMP = BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length);
            productImage.setImageBitmap(imageBMP);
        }

        newOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newOperationClick();
            }
        });


        showProgress(false);
    }

    private void newOperationClick() {
        if (NullUtil.isNull(selectedProduct)) {
            return;
        }

        currentView = inflate(R.layout.layout_products_product_newoperation);
        if (NullUtil.isNotNull(selectedProduct.getImage())) {
            ImageView productImage = (ImageView) currentView.findViewById(R.id.layout_products_product_newoperation_image);
            Bitmap imageBMP = BitmapFactory.decodeByteArray(selectedProduct.getImage(), 0, selectedProduct.getImage().length);
            productImage.setImageBitmap(imageBMP);
        }

        TextView productName = (TextView) currentView.findViewById(R.id.layout_products_product_newoperation_productName);
        productName.setText(selectedProduct.getProductName());

        operationCount = (EditText) currentView.findViewById(R.id.layout_products_product_newoperation_operationcount);
        operationNote = (EditText) currentView.findViewById(R.id.layout_products_product_newoperation_note);
        operationTotalPrice = (TextView) currentView.findViewById(R.id.layout_products_product_newoperation_totalprice);
        operationType = (Spinner) currentView.findViewById(R.id.layout_products_product_newoperation_operationType);

        operationCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int productCount = Integer.parseInt(operationCount.getText().toString());
                double price = 0;
                if (NullUtil.isNotNull(selectedProduct.getPrice())) {
                    price = selectedProduct.getPrice().doubleValue();
                }

                operationTotalPrice.setText((price * productCount) + " " + getString(R.string.currency));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Button save = (Button) currentView.findViewById(R.id.layout_products_product_newoperation_saveButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NullUtil.isNotNull(baseTask)) {
                    return;
                }

                showProgress(true);

                String countPrefix = "+";
                if (CompareUtil.equalIgnoreCase("SATIM", operationType.getSelectedItem().toString())) {
                    countPrefix = "-";
                }

                LogUtil.logMessage(getClass(), "operationType.getSelectedItem().toString(): " + operationType.getSelectedItem().toString());
                LogUtil.logMessage(getClass(), "countPrefix + operationCount.getText().toString(): " + (countPrefix + operationCount.getText().toString()));

                RequestModel requestModel = RequestModelGenerator.productNewOperation(getAccessToken(), selectedProduct.getId() + "", countPrefix + operationCount.getText().toString(), operationNote.getText().toString());
                String URL = getActivity().getString(R.string.serverURL) + getActivity().getString(R.string.serviceTagProduct);

                baseTask = new BaseTask(URL, requestModel, new Performer() {
                    @Override
                    public void perform(boolean success) {
                        boolean isServerUnreachable = baseTask.isServerUnreachable();
                        String errorMessage = baseTask.getErrorMessage();

                        baseTask = null;
                        showProgress(false);

                        if (isServerUnreachable) {
                            showToast(getActivityString(R.string.serverUnreachable));
                            return;
                        }

                        if (success) {
                            showToast("Operation success: " + success);
                        } else {
                            showToast("Operation success: " + success + "\nErrorMessage:" + errorMessage);
                        }

                        prepareProductsActivity();
                    }
                });

                baseTask.execute((Void) null);
            }
        });
    }
}
