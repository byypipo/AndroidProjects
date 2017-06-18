package com.sebatmedikal.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.sebatmedikal.R;
import com.sebatmedikal.adapter.BrandListAdapter;
import com.sebatmedikal.mapper.Mapper;
import com.sebatmedikal.remote.domain.Brand;
import com.sebatmedikal.remote.domain.Product;
import com.sebatmedikal.remote.model.request.RequestModel;
import com.sebatmedikal.remote.model.request.RequestModelGenerator;
import com.sebatmedikal.task.BaseTask;
import com.sebatmedikal.task.Performer;
import com.sebatmedikal.util.CompareUtil;
import com.sebatmedikal.util.ImageUtil;
import com.sebatmedikal.util.LogUtil;
import com.sebatmedikal.util.NullUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by orhan on 26.05.2017.
 */
public class BrandsActivity extends BaseActivity {
    private BrandListAdapter brandListAdapter;

    private List<Product> brandProducts;

    private TextView selectedProductName;
    private TextView selectedProductNote;
    private TextView selectedProductTypeCount;
    private TextView selectedProductCreatedBy;

    private ImageView brandImage;
    private AutoCompleteTextView newBrandName;
    private AutoCompleteTextView newBrandNote;

    private boolean brandImageAdded = false;
    private Brand selectedBrand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareBrandsActivity();
    }

    private void prepareBrandsActivity() {
        if (NullUtil.isNotNull(baseTask)) {
            return;
        }

        showProgress(true);

        String URL = getServerIp() + getString(R.string.serviceTagBrand);
        RequestModel requestModel = RequestModelGenerator.findAll(getAccessToken());

        baseTask = new BaseTask(URL, requestModel, new Performer() {
            @Override
            public void perform(boolean success) {
                List<Brand> brandList = Mapper.brandListMapper(baseTask.getContent());
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
                    View inflatedView = inflate(R.layout.layout_brands);

                    ListView listView = (ListView) inflatedView.findViewById(R.id.layout_brands_listview);
                    SearchView searchView = (SearchView) inflatedView.findViewById(R.id.layout_brands_searchview);

                    long readedBrandsDate = preferences.getLong("readedBrandsDate", 0);

                    brandListAdapter = new BrandListAdapter(getActivity(), brandList, new Date(readedBrandsDate));
                    listView.setAdapter(brandListAdapter);

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String s) {
                            brandListAdapter.getFilter().filter(s);
                            return true;
                        }
                    });

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Brand brand = (Brand) brandListAdapter.getItem(i);
                            brandListClick(brand);
                        }
                    });

                    if (CompareUtil.equal(preferences.getString("roleid", null), getString(R.string.roleAdmin))) {
                        ImageButton addNewBrand = (ImageButton) findViewById(R.id.layout_brands_new_brand);
                        addNewBrand.setVisibility(View.VISIBLE);

                        addNewBrand.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadNewBrandLayout();
                            }
                        });
                    }

                    setEditor("readedBrandsDate", new Date().getTime());
                } else {
                    showToast("Brands success: " + success + "\nErrorMessage:" + errorMessage);
                }
            }
        });

        baseTask.execute((Void) null);
    }

    private void loadNewBrandLayout() {
        showProgress(true);

        View view = inflate(R.layout.layout_brands_new_brand);

        brandImage = (ImageView) view.findViewById(R.id.layout_brands_new_brand_image);
        newBrandName = (AutoCompleteTextView) view.findViewById(R.id.layout_brands_new_brand_name);
        newBrandName.addTextChangedListener(defaultTextWatcher);
        newBrandNote = (AutoCompleteTextView) view.findViewById(R.id.layout_brands_new_brand_note);
        newBrandNote.addTextChangedListener(defaultTextWatcher);
        save = (ImageButton) view.findViewById(R.id.layout_brands_new_brand_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewBrandProcess();
            }
        });

        brandImage.setOnClickListener(imageClickListenerWithPermission);

        showProgress(false);
    }

    private void addNewBrandProcess() {
        if (NullUtil.isNotNull(baseTask)) {
            return;
        }

        baseTask = null;
        showProgress(true);

        Brand newBrand = new Brand();
        newBrand.setBrandName(newBrandName.getText().toString());
        newBrand.setNote(newBrandNote.getText().toString());

        if (brandImageAdded) {
            Bitmap newImageBitmap = ((BitmapDrawable) brandImage.getDrawable()).getBitmap();
            byte[] newImage = ImageUtil.converBitmapToByteArray(newImageBitmap, (float) 0.1);
            newBrand.setImage(newImage);
            brandImageAdded = false;
        }

        String URL = getServerIp() + getString(R.string.serviceTagBrand);
        RequestModel requestModel = RequestModelGenerator.brandCreate(getAccessToken(), newBrand);

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
                    change(false);
                    showToast("Brand added");
                } else {
                    showToast("Brand added success: " + success + "\nErrorMessage:" + errorMessage);
                }

                prepareBrandsActivity();
            }
        });

        baseTask.execute((Void) null);
    }

    private void brandListClick(final Brand brand) {
        if (NullUtil.isNull(brand)) {
            return;
        }

        showProgress(true);

        View view = inflate(R.layout.layout_brands_brand);

        brandImage = (ImageView) view.findViewById(R.id.layout_brands_brand_image);
        selectedProductName = (TextView) view.findViewById(R.id.layout_brands_brand_brandName);
        selectedProductNote = (TextView) view.findViewById(R.id.layout_brands_brand_note);
        selectedProductTypeCount = (TextView) view.findViewById(R.id.layout_brands_brand_productTypeCount);
        selectedProductCreatedBy = (TextView) view.findViewById(R.id.layout_brands_brand_createdBy);
        save = (ImageButton) view.findViewById(R.id.layout_brands_brand_save);
        ImageButton gotoProducts = (ImageButton) view.findViewById(R.id.layout_brands_brand_gotoproduct);

        selectedProductName.setText(brand.getBrandName());
        selectedProductNote.setText(brand.getNote());
        selectedProductCreatedBy.setText(brand.getCreatedBy());
        selectedProductTypeCount.setText(getString(R.string.unknown));

        if (NullUtil.isNotNull(brand.getImage())) {
            Bitmap imageBMP = BitmapFactory.decodeByteArray(brand.getImage(), 0, brand.getImage().length);
            brandImage.setImageBitmap(imageBMP);
        }

        gotoProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NullUtil.isNotNull(baseTask)) {
                    return;
                }

                showProgress(true);

                String URL = getServerIp() + getString(R.string.serviceTagBrand);
                RequestModel requestModel = RequestModelGenerator.brandProducts(getAccessToken(), brand.getId() + "");

                baseTask = new BaseTask(URL, requestModel, new Performer() {
                    @Override
                    public void perform(boolean success) {
                        brandProducts = Mapper.productListMapper(baseTask.getContent());
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
                            selectedProductTypeCount.setText(brandProducts.size() + "");
                        } else {
                            showToast("Products success: " + success + "\nErrorMessage:" + errorMessage);
                        }
                    }
                });

                baseTask.execute((Void) null);

                Intent intent = new Intent(getActivity(), ProductsActivity.class);
                intent.putExtra("brandFilter", brand.getBrandName());
                startActivity(intent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBrand();
            }
        });

        brandImage.setOnClickListener(imageClickListenerWithPermission);

        selectedBrand = brand;
        showProgress(false);
    }

    private void updateBrand() {
        if (NullUtil.isNotNull(baseTask)) {
            return;
        }

        if (!isChange()) {
            LogUtil.logMessage(getClass(), "IMAGE NOT CHANGED");
            return;
        }

        LogUtil.logMessage(getClass(), "IMAGE CHANGED");

        Bitmap newImageBitmap = ((BitmapDrawable) brandImage.getDrawable()).getBitmap();
        byte[] newImage = ImageUtil.converBitmapToByteArray(newImageBitmap, (float) 0.1);
        selectedBrand.setImage(newImage);
        brandImageAdded = false;

        String URL = getServerIp() + getString(R.string.serviceTagBrand);
        RequestModel requestModel = RequestModelGenerator.brandUpdate(getAccessToken(), selectedBrand);

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
                    change(false);
                    showToast("Brand updated");
                } else {
                    showToast("Brand success: " + success + "\nErrorMessage:" + errorMessage);
                }
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

            brandImage.setImageBitmap(bitmap);
            brandImageAdded = true;
            change(true);
        }
    }
}