package com.project.sean.stockmanagement;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.project.sean.stockmanagement.Database.AndroidPOSDBHelper;
import com.project.sean.stockmanagement.Database.StockInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sean on 01/04/2016.
 */
public class ViewStockFragment extends Fragment {

    private AndroidPOSDBHelper dbHelper;
    private StockViewAdapter dataAdapter;


    ListView listView;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ViewStockFragment newInstance(int sectionNumber) {
        ViewStockFragment fragment = new ViewStockFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewStockFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_stock, container, false);

        dbHelper = AndroidPOSDBHelper.getInstance(getActivity());

        ArrayList<StockInfo> stockInfoList = new ArrayList<StockInfo>();
        stockInfoList.addAll(dbHelper.getAllStockInfo());

        dataAdapter = new StockViewAdapter(getActivity(), R.layout.listview_stock_info, stockInfoList);

        listView = (ListView) rootView.findViewById(R.id.listView1);

        listView.setAdapter(dataAdapter);

        EditText myFilter = (EditText) rootView.findViewById(R.id.myFilter);

        myFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onTextChanged", "" + s);
                dataAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //TextView textView = (TextView) rootView.findViewById(R.id.view_stock_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        //Tells the parent activity that this fragment has its own menu
        setHasOptionsMenu(true);
        return rootView;
    }

    /**
     * Create the unique menu bar for ViewStockFragment.
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_view_stock, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
    }

    //    @Override
//    public void onResume() {
//        super.onResume();
//        Log.d("onResume", "The onResume method has been called.");
//        ArrayList<StockInfo> stockInfoList = new ArrayList<StockInfo>();
//        stockInfoList.addAll(dbHelper.getAllStockInfo());
//        dataAdapter = new StockViewAdapter(getActivity(), R.layout.listview_stock_info, stockInfoList);
//        listView.setAdapter(dataAdapter);
//    }

    //    public void displayListView() {
//
//        Cursor cursor = dbHelper.getallStockData();
//
//    }

    /**
     * Refresh the ListView, to be implemented in-case update on swipe or select tab
     * cannot be completed.
     */
    public void refreshList() {

    }

    public class StockViewAdapter extends ArrayAdapter<StockInfo> {

        private ArrayList<StockInfo> originalList;
        private ArrayList<StockInfo> stockList;
        private StockFilter filter;

        public StockViewAdapter(Context context, int resource, ArrayList<StockInfo> stockList) {
            super(context, resource, stockList);
            this.stockList = new ArrayList<StockInfo>();
            this.stockList.addAll(stockList);

            this.originalList = new ArrayList<StockInfo>();
            this.stockList.addAll(stockList);
        }

        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new StockFilter();
            }
            return filter;
        }

        private class ViewHolder {
            TextView tvStockID;
            TextView tvStockName;
            TextView tvCategory;
            TextView tvSalePrice;
            TextView tvCostPrice;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            //Print to log the position number
            Log.v("ConvertView", String.valueOf(position));

            if(convertView == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.listview_stock_info, null);

                holder = new ViewHolder();
                holder.tvStockID = (TextView) convertView.findViewById(R.id.tvStockID);
                holder.tvStockName = (TextView) convertView.findViewById(R.id.tvStockName);
                holder.tvCategory = (TextView) convertView.findViewById(R.id.tvCategory);
                holder.tvSalePrice = (TextView) convertView.findViewById(R.id.tvSalePrice);
                holder.tvCostPrice = (TextView) convertView.findViewById(R.id.tvCostPrice);

                convertView.setTag(holder);
            } else {
                holder =(ViewHolder) convertView.getTag();
            }

            StockInfo stockInfo = stockList.get(position);
            holder.tvStockID.setText(stockInfo.getStockId());
            holder.tvStockName.setText(stockInfo.getStockName());
            holder.tvCategory.setText(stockInfo.getCategory());
            holder.tvSalePrice.setText("" + stockInfo.getSalePrice());
            holder.tvCostPrice.setText("" + stockInfo.getCostPrice());

            return convertView;
        }


        public class StockFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();
                if(constraint != null && constraint.toString().length() > 0) {
                    ArrayList<StockInfo> filteredItems = new ArrayList<StockInfo>();
                    for(int i = 0, l = originalList.size(); i < l; i++) {
                        StockInfo stockInfo = originalList.get(i);
                        if(stockInfo.toString().toLowerCase().contains(constraint)) {
                            filteredItems.add(stockInfo);
                        }
                        result.count = filteredItems.size();
                        result.values = filteredItems;
                    }
                } else {
                    synchronized (this) {
                        result.values = originalList;
                        result.count = originalList.size();
                    }
                }
                return result;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count == 0) {
                    notifyDataSetInvalidated();
                    Log.d("publishResults", "IF");
                } else {
                    stockList = (ArrayList<StockInfo>)results.values;
                    notifyDataSetChanged();
                    Log.d("publishResults", "ELSE");
                }
//                stockList = (ArrayList<StockInfo>)results.values;
//                notifyDataSetChanged();
//                clear();
////                for(StockInfo stockInf : stockList) {
////
////                }
//                for(int i = 0, l = stockList.size(); i < l; i++) {
//                    add(stockList.get(i));
//                    notifyDataSetInvalidated();;
//                }
            }
        }

    }


}
