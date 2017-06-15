package com.sebatmedikal.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.sebatmedikal.R;
import com.sebatmedikal.adapter.ProductListAdapter;
import com.sebatmedikal.mapper.Mapper;
import com.sebatmedikal.remote.domain.Brand;
import com.sebatmedikal.remote.domain.Operation;
import com.sebatmedikal.remote.domain.Product;
import com.sebatmedikal.remote.model.request.RequestModel;
import com.sebatmedikal.remote.model.request.RequestModelGenerator;
import com.sebatmedikal.task.BaseTask;
import com.sebatmedikal.task.Performer;
import com.sebatmedikal.util.CompareUtil;
import com.sebatmedikal.util.ImageUtil;
import com.sebatmedikal.util.LogUtil;
import com.sebatmedikal.util.NullUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends BaseActivity {
    private ProductListAdapter productListAdapter;
    private static int RESULT_LOAD_IMAGE = 1;

    private Product selectedProduct;
    private EditText operationCount;
    private EditText operationNote;
    private TextView operationTotalPrice;
    private Spinner operationType;

    private ImageView new_product_image;
    private AutoCompleteTextView new_product_name;
    private AutoCompleteTextView new_product_price;
    private Spinner new_product_brand;
    private AutoCompleteTextView new_product_note;
    private AutoCompleteTextView new_product_barcod;

    private boolean productImageAdded = false;

    private String brandFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        brandFilter = intent.getStringExtra("brandFilter");
        LogUtil.logMessage(getClass(), "brandFilter: " + brandFilter);
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
                            brandFilter = null;
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

                    if (CompareUtil.equal(preferences.getString("roleid", null), getString(R.string.roleAdmin))) {
                        Button button = (Button) findViewById(R.id.layout_product_new_product_button);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addNewProductButtonClick();
                            }
                        });
                        button.setVisibility(View.VISIBLE);
                    }

                    if (NullUtil.isNotNull(brandFilter)) {
                        searchView.setQuery(brandFilter, true);
                        LogUtil.logMessage(getClass(), "brandFilter: " + brandFilter + " filtered?");
                    }
                } else {
                    showToast("Operation success: " + success + "\nErrorMessage:" + errorMessage);
                }
            }
        });

        baseTask.execute((Void) null);
    }

    private void addNewProductButtonClick() {
        if (NullUtil.isNotNull(baseTask)) {
            return;
        }

        selectedProduct = null;
        showProgress(true);

        View layout_product_new_product = inflate(R.layout.layout_product_new_product);

        new_product_image = (ImageView) layout_product_new_product.findViewById(R.id.layout_product_new_product_image);
        new_product_name = (AutoCompleteTextView) layout_product_new_product.findViewById(R.id.layout_product_new_product_name);
        new_product_price = (AutoCompleteTextView) layout_product_new_product.findViewById(R.id.layout_product_new_product_price);
        new_product_brand = (Spinner) layout_product_new_product.findViewById(R.id.layout_product_new_product_brand);
        new_product_note = (AutoCompleteTextView) layout_product_new_product.findViewById(R.id.layout_product_new_product_note);
        new_product_barcod = (AutoCompleteTextView) layout_product_new_product.findViewById(R.id.layout_product_new_product_barcod);


        new_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

        Button save = (Button) layout_product_new_product.findViewById(R.id.layout_product_new_product_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewProductClick();
            }
        });

        RequestModel requestModel = RequestModelGenerator.findAllOnlyName(getAccessToken());
        String URL = getActivity().getString(R.string.serverURL) + getActivity().getString(R.string.serviceTagBrand);

        baseTask = new BaseTask(URL, requestModel, new Performer() {
            @Override
            public void perform(boolean success) {
                List<String> brandNames = Mapper.stringListMapper(baseTask.getContent());
                boolean isServerUnreachable = baseTask.isServerUnreachable();
                String errorMessage = baseTask.getErrorMessage();

                baseTask = null;
                showProgress(false);

                if (isServerUnreachable) {
                    showToast(getActivityString(R.string.serverUnreachable));
                    return;
                }

                if (success) {
                    showToast("Brands success: " + success);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, brandNames);
                    new_product_brand.setAdapter(adapter);
                } else {
                    showToast("Brands success: " + success + "\nErrorMessage:" + errorMessage);
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

    private void addNewProductClick() {
        if (NullUtil.isNotNull(baseTask)) {
            return;
        }

        selectedProduct = null;
        showProgress(true);

        Brand brand = new Brand();
        brand.setBrandName(new_product_brand.getSelectedItem().toString());

        Product newProduct = new Product();
        newProduct.setProductName(new_product_name.getText().toString());
        newProduct.setBarcod(new_product_name.getText().toString());
        newProduct.setNote(new_product_note.getText().toString());
        newProduct.setPrice(new BigDecimal(new_product_price.getText().toString()));
        newProduct.setBrand(brand);

        if (productImageAdded) {
            Bitmap newImageBitmap = ((BitmapDrawable) new_product_image.getDrawable()).getBitmap();
            byte[] newImage = ImageUtil.converBitmapToByteArray(newImageBitmap, (float) 0.1);
            newProduct.setImage(newImage);
        }

        RequestModel requestModel = RequestModelGenerator.productCreate(getAccessToken(), newProduct);
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
                    showToast("Product Added success: " + success);
                } else {
                    showToast("Product Added success: " + success + "\nErrorMessage:" + errorMessage);
                }

                prepareProductsActivity();
            }
        });

        baseTask.execute((Void) null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = ImageUtil.prepareBitmapOrientation(picturePath);

            new_product_image.setImageBitmap(bitmap);
            productImageAdded = true;
        }
    }
}
