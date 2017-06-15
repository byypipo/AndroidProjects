package com.sebatmedikal.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.sebatmedikal.R;
import com.sebatmedikal.external.CircleImageView;
import com.sebatmedikal.remote.domain.Brand;
import com.sebatmedikal.remote.domain.User;
import com.sebatmedikal.util.NullUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by orhan on 20.05.2017.
 */
public class BrandListAdapter extends BaseAdapter implements Filterable {

    private List<Brand> mOriginalValues;
    private List<Brand> mDisplayedValues;
    LayoutInflater inflater;

    Drawable defaultImage;

    public BrandListAdapter(Context context, List<Brand> mUserArrayList) {
        this.mOriginalValues = mUserArrayList;
        this.mDisplayedValues = mUserArrayList;
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
        TextView name;
        TextView note;
        ImageView image;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_brands_list_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.layout_brands_list_item_name);
            holder.note = (TextView) convertView.findViewById(R.id.layout_brands_list_item_note);
            holder.image = (ImageView) convertView.findViewById(R.id.layout_brands_list_item_image);
            convertView.setTag(holder);

            if (NullUtil.isNull(defaultImage)) {
                defaultImage = holder.image.getBackground();
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            holder.name.setText(mDisplayedValues.get(position).getBrandName());
            holder.note.setText(mDisplayedValues.get(position).getNote());

            if (NullUtil.isNotNull(mDisplayedValues.get(position).getImage())) {
                Bitmap imageBMP = BitmapFactory.decodeByteArray(mDisplayedValues.get(position).getImage(), 0, mDisplayedValues.get(position).getImage().length);
                holder.image.setImageBitmap(imageBMP);
            } else {
                holder.image.setImageResource(R.drawable.medical);
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

                mDisplayedValues = (ArrayList<Brand>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Brand> FilteredArrList = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Brand>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                if (constraint == null || constraint.length() == 0) {

                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();

                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String brandName = mOriginalValues.get(i).getBrandName();
                        if (checkFilter(FilteredArrList, brandName, constraint.toString(), i)) {
                            continue;
                        }

                        String brandNote = mOriginalValues.get(i).getNote();
                        if (checkFilter(FilteredArrList, brandNote, constraint.toString(), i)) {
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

    private boolean checkFilter(ArrayList<Brand> FilteredArrList, String text, String constraintText, int index) {
        if (NullUtil.isAnyNull(text, constraintText)) {
            return false;
        }

        if (!text.toLowerCase().contains(constraintText)) {
            return false;
        }

        FilteredArrList.add(mOriginalValues.get(index));
        return true;
    }
}