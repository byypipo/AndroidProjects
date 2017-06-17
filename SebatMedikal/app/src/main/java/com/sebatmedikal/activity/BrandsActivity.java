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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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

    private ImageView selectedProductImage;
    private TextView selectedProductName;
    private TextView selectedProductNote;
    private TextView selectedProductTypeCount;
    private TextView selectedProductCreatedBy;

    private ImageView newBrandImage;
    private AutoCompleteTextView newBrandName;
    private AutoCompleteTextView newBrandNote;
    private boolean newBrandImageAdded = false;

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

                    brandListAdapter = new BrandListAdapter(getActivity(), brandList);
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
                        Button addNewBrand = (Button) findViewById(R.id.layout_brands_new_brand);
                        addNewBrand.setVisibility(View.VISIBLE);

                        addNewBrand.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadNewBrandLayout();
                            }
                        });
                    }

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

        newBrandImage = (ImageView) view.findViewById(R.id.layout_brands_new_brand_image);
        newBrandName = (AutoCompleteTextView) view.findViewById(R.id.layout_brands_new_brand_name);
        newBrandNote = (AutoCompleteTextView) view.findViewById(R.id.layout_brands_new_brand_note);
        Button save = (Button) view.findViewById(R.id.layout_brands_new_brand_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addNewBrandProcess();
            }
        });

        if (NullUtil.isNotNull(capturedPictureFile)) {
            String picturePath = capturedPictureFile.getAbsolutePath();
            Bitmap bitmap = ImageUtil.prepareBitmapOrientation(picturePath);

            newBrandImage.setImageBitmap(bitmap);
            newBrandImageAdded = true;
            capturedPictureFile = null;
        }

        newBrandImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateSelectImageLayout();
            }
        });

        showProgress(false);
    }

    @Override
    protected void capturedCamera() {
        String picturePath = capturedPictureFile.getAbsolutePath();
        Bitmap bitmap = ImageUtil.prepareBitmapOrientation(picturePath);

        newBrandImage.setImageBitmap(bitmap);
        newBrandImageAdded = true;
        loadNewBrandLayout();
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

        if (newBrandImageAdded) {
            Bitmap newImageBitmap = ((BitmapDrawable) newBrandImage.getDrawable()).getBitmap();
            byte[] newImage = ImageUtil.converBitmapToByteArray(newImageBitmap, (float) 0.1);
            newBrand.setImage(newImage);
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

        selectedProductImage = (ImageView) view.findViewById(R.id.layout_brands_brand_image);
        selectedProductName = (TextView) view.findViewById(R.id.layout_brands_brand_brandName);
        selectedProductNote = (TextView) view.findViewById(R.id.layout_brands_brand_note);
        selectedProductTypeCount = (TextView) view.findViewById(R.id.layout_brands_brand_productTypeCount);
        selectedProductCreatedBy = (TextView) view.findViewById(R.id.layout_brands_brand_createdBy);
        Button gotoProducts = (Button) view.findViewById(R.id.layout_brands_brand_gotoproduct);

        selectedProductName.setText(brand.getBrandName());
        selectedProductNote.setText(brand.getNote());
        selectedProductCreatedBy.setText(brand.getCreatedBy());
        selectedProductTypeCount.setText(getString(R.string.unknown));

        if (NullUtil.isNotNull(brand.getImage())) {
            Bitmap imageBMP = BitmapFactory.decodeByteArray(brand.getImage(), 0, brand.getImage().length);
            selectedProductImage.setImageBitmap(imageBMP);
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

        showProgress(false);
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

            newBrandImage.setImageBitmap(bitmap);
            newBrandImageAdded = true;
        }
    }
}