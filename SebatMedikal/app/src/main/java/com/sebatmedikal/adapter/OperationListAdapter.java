package com.sebatmedikal.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sebatmedikal.R;
import com.sebatmedikal.activity.OperationsActivity;
import com.sebatmedikal.remote.domain.Operation;
import com.sebatmedikal.util.CompareUtil;
import com.sebatmedikal.util.LogUtil;
import com.sebatmedikal.util.NullUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by orhan on 20.05.2017.
 */
public class OperationListAdapter extends BaseAdapter implements Filterable {

    private List<Operation> mOriginalValues;
    private List<Operation> mDisplayedValues;
    LayoutInflater inflater;

    public OperationListAdapter(Context context, List<Operation> mOperationArrayList) {
        this.mOriginalValues = mOperationArrayList;
        this.mDisplayedValues = mOperationArrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (mDisplayedValues == null) {
            return 0;
        }
        return mDisplayedValues.size();
    }

    public void addListItems(List<Operation> addedArrayList) {
        mOriginalValues.addAll(addedArrayList);
        mDisplayedValues = mOriginalValues;
        notifyDataSetChanged();
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
        TextView productName;
        TextView operationCount;
        TextView operationType;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_operations_list_item, null);
            holder.productName = (TextView) convertView.findViewById(R.id.list_item_operations_productname);
            holder.operationCount = (TextView) convertView.findViewById(R.id.list_item_operations_count);
            holder.operationType = (TextView) convertView.findViewById(R.id.list_item_operations_operationtype);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            String productName = mDisplayedValues.get(position).getProduct().getProductName() + " - " + mDisplayedValues.get(position).getId();
            if (NullUtil.isNotNull(mDisplayedValues.get(position).getNote())) {
                productName += " - " + mDisplayedValues.get(position).getNote();
            }
            holder.productName.setText(productName);
            holder.operationCount.setText(mDisplayedValues.get(position).getCount() + "");
            holder.operationType.setText(mDisplayedValues.get(position).getOperationType().getOperationTypeName());

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

                mDisplayedValues = (ArrayList<Operation>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Operation> FilteredArrList = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<Operation>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                if (constraint == null || constraint.length() == 0) {

                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();

                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String productNameString = mOriginalValues.get(i).getProduct().getProductName();
                        if (checkFilter(FilteredArrList, productNameString, constraint.toString(), i)) {
                            continue;
                        }

                        String productNote = mOriginalValues.get(i).getNote();
                        if (checkFilter(FilteredArrList, productNote, constraint.toString(), i)) {
                            continue;
                        }

                        String operationTypeName = mOriginalValues.get(i).getOperationType().getOperationTypeName();
                        if (checkFilter(FilteredArrList, operationTypeName, constraint.toString(), i)) {
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

    private boolean checkFilter(ArrayList<Operation> FilteredArrList, String text, String constraintText, int index) {
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