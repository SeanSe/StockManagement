package com.project.sean.stockmanagement;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.project.sean.stockmanagement.Database.StockInfo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Sean on 01/04/2016.
 */
public class StockLevelFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static StockLevelFragment newInstance(int sectionNumber) {
        StockLevelFragment fragment = new StockLevelFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public StockLevelFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stock_level, container, false);



        TextView textView = (TextView) rootView.findViewById(R.id.stock_level_lable);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }

    public class StockLevelAdapter extends ArrayAdapter<StockInfo> {

        private ArrayList<StockInfo> originalList;
        private ArrayList<StockInfo> stockList;
        private StockFilter filter;

        public StockLevelAdapter(Context context, int resource, ArrayList<StockInfo> stockList) {
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
            TextView tvstockId;
        }

    }

    public class StockFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

        }
    }
}