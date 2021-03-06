package com.sebatmedikal.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.Filter;

import com.sebatmedikal.R;
import com.sebatmedikal.remote.domain.Product;
import com.sebatmedikal.util.LogUtil;
import com.sebatmedikal.util.NullUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by orhan on 20.05.2017.
 */
public class ProductListAdapter extends BaseAdapter implements Filterable {

    private List<Product> mOriginalValues;
    private List<Product> mDisplayedValues;
    private Date lastReadedDate;
    LayoutInflater inflater;

    public ProductListAdapter(Context context, List<Product> mProductArrayList, Date lastReadedDate) {
        this.mOriginalValues = mProductArrayList;
        this.lastReadedDate = lastReadedDate;
        this.mDisplayedValues = mProductArrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (mDisplayedValues == null) {
            return 0;
        }
        return mDisplayedValues.size();
    }

    @Override
    public Object getItem(int position) {
        return mDisplayedValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        LinearLayout list_item_Layout;
        TextView productName;
        TextView productBrand;
        ImageView productImage;
        ImageView animation;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_products_list_item, null);
            holder.list_item_Layout = (LinearLayout) convertView.findViewById(R.id.list_item_products);
            holder.productName = (TextView) convertView.findViewById(R.id.list_item_products_name);
            holder.productBrand = (TextView) convertView.findViewById(R.id.list_item_products_brand);
            holder.productImage = (ImageView) convertView.findViewById(R.id.list_item_products_image);
            holder.animation = (ImageView) convertView.findViewById(R.id.list_item_products_animation);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {

            Product product = mDisplayedValues.get(position);
            String productName = product.getProductName();
            if (NullUtil.isNotNull(product.getNote())) {
                productName += " - " + product.getNote();
            }
            holder.productName.setText(productName);
            holder.productBrand.setText(product.getBrand().getBrandName());

            if (NullUtil.isNotNull(product.getImage())) {
                Bitmap imageBMP = BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length);
                holder.productImage.setImageBitmap(imageBMP);
            }

            if (product.getCreatedDate().after(lastReadedDate)) {
                holder.animation.setBackgroundResource(R.drawable.animation_new);
                AnimationDrawable animationDrawable = (AnimationDrawable) holder.animation.getBackground();
                animationDrawable.start();
            } else {
                holder.animation.setBackgroundResource(R.color.transparent);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                mDisplayedValues = (ArrayList<Product>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Product> FilteredArrList = new ArrayList<Product>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Product>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                if (constraint == null || constraint.length() == 0) {

                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();

                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String productNameString = mOriginalValues.get(i).getProductName();
                        if (checkFilter(FilteredArrList, productNameString, constraint.toString(), i)) {
                            continue;
                        }

                        String productNote = mOriginalValues.get(i).getNote();
                        if (checkFilter(FilteredArrList, productNote, constraint.toString(), i)) {
                            continue;
                        }

                        String productBrand = mOriginalValues.get(i).getBrand().getBrandName();
                        if (checkFilter(FilteredArrList, productBrand, constraint.toString(), i)) {
                            continue;
                        }
                    }

                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

    private boolean checkFilter(ArrayList<Product> FilteredArrList, String productText, String constraintText, int productIndex) {
        if (NullUtil.isAnyNull(productText, constraintText)) {
            return false;
        }

        if (!productText.toLowerCase().contains(constraintText)) {
            return false;
        }

        FilteredArrList.add(mOriginalValues.get(productIndex));
        return true;
    }
}