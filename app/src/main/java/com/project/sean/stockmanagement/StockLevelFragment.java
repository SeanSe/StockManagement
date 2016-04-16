package com.project.sean.stockmanagement;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.project.sean.stockmanagement.Database.AndroidPOSDBHelper;
import com.project.sean.stockmanagement.Database.StockInfo;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Sean on 01/04/2016.
 */
public class StockLevelFragment extends Fragment implements SearchView.OnQueryTextListener {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView mRecyclerView;
    private AndroidPOSDBHelper dbHelper;
    private StockLevelAdapter mAdapter;
    private List<StockInfo> stockInfoList;

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

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stock_level, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvStockLevel);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        dbHelper = AndroidPOSDBHelper.getInstance(getActivity());

        stockInfoList = new ArrayList<StockInfo>();
        stockInfoList.addAll(dbHelper.getAllStockInfo());

        mAdapter = new StockLevelAdapter(getActivity(), stockInfoList);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Create the unique menu bar for StockLevelFragment.
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_stock_levels, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    /**
     * Adjust the RecyclerView based on the query input into the search view.
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextChange(String query) {
        final List<StockInfo> filteredStockList = filter(stockInfoList, query);
        mAdapter.animateTo(filteredStockList);
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    /**
     * On query submit do nothing.
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * Produce a filtered list using the list of stock information and the query
     * entered into the search view.
     * @param stockInfos
     * @param query
     * @return
     */
    public List<StockInfo> filter(List<StockInfo> stockInfos, String query) {
        query = query.toLowerCase();

        final List<StockInfo> filteredModelList = new ArrayList<>();
        for (StockInfo stockInfo : stockInfos) {
            final String text = stockInfo.getStockName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(stockInfo);
            }
        }
        return filteredModelList;
    }

    /**
     * Creates the view used to populate each row of the RecyclerView.
     */
    public class StockLevelViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        //Add the variables here
        private final TextView tvStockIDLevel;
        private final TextView tvStockNameLevel;
        private final TextView tvStockQtyLevel;

        public StockLevelViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            //Add the UI elements here
            tvStockIDLevel = (TextView) itemView.findViewById(R.id.tvStockIDLevel);
            tvStockNameLevel = (TextView) itemView.findViewById(R.id.tvStockNameLevel);
            tvStockQtyLevel = (TextView) itemView.findViewById(R.id.tvStockQtyLevel);
        }

        public void bind(StockInfo model) {
            //Bind to the UI elements here
            tvStockIDLevel.setText(model.getStockId());
            tvStockNameLevel.setText(model.getStockName());
            tvStockQtyLevel.setText(Integer.toString(model.getStockQty()));
        }

        @Override
        public void onClick(View v) {
            Log.d("Toast...", tvStockIDLevel.getText().toString() + tvStockNameLevel.getText().toString());
        }
    }

    /**
     * StockLevelAdapter inner class to create the RecyclerView
     */
    public class StockLevelAdapter extends RecyclerView.Adapter<StockLevelViewHolder> {

        private final LayoutInflater mInflater;
        private final List<StockInfo> stockInfoList;

        public StockLevelAdapter(Context context, List<StockInfo> models) {
            mInflater = LayoutInflater.from(context);
            stockInfoList = new ArrayList<>(models);
        }

        @Override
        public StockLevelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = mInflater.inflate(R.layout.listview_stock_level, parent, false);
            return new StockLevelViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(StockLevelViewHolder holder, int position) {
            final StockInfo stockInfo = stockInfoList.get(position);
            holder.bind(stockInfo);
        }

        /**
         * Get the current size of the stockInfoList.
         * @return
         */
        @Override
        public int getItemCount() {
            return stockInfoList.size();
        }

        public void animateTo(List<StockInfo> models) {
            applyAndAnimateRemovals(models);
            applyAndAnimateAdditions(models);
            applyAndAnimateMovedItems(models);
        }

        /**
         * Adjust the list when an item is removed.
         * @param newModels
         */
        private void applyAndAnimateRemovals(List<StockInfo> newModels) {
            for (int i = stockInfoList.size() - 1; i >= 0; i--) {
                final StockInfo stockInfo = stockInfoList.get(i);
                if (!newModels.contains(stockInfo)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<StockInfo> newModels) {
            for (int i = 0, count = newModels.size(); i < count; i++) {
                final StockInfo stockInfo = newModels.get(i);
                if (!stockInfoList.contains(stockInfo)) {
                    addItem(i, stockInfo);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<StockInfo> newModels) {
            for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
                final StockInfo stockInfo = newModels.get(toPosition);
                final int fromPosition = stockInfoList.indexOf(stockInfo);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }

        /**
         * Remove an item from the stock info list and notify the adapter of the
         * change.
         * @param position
         * @return
         */
        public StockInfo removeItem(int position) {
            final StockInfo stockInfo = stockInfoList.remove(position);
            notifyItemRemoved(position);
            return stockInfo;
        }

        /**
         * Add an item to the stock info list and notify the adapter of the
         * change.
         * @param position
         * @param stockInfo
         */
        public void addItem(int position, StockInfo stockInfo) {
            stockInfoList.add(position, stockInfo);
            notifyItemInserted(position);
        }

        /**
         * Move an item in the stock info list and notify the adapter of the
         * change.
         * @param fromPosition
         * @param toPosition
         */
        public void moveItem(int fromPosition, int toPosition) {
            final StockInfo stockInfo = stockInfoList.remove(fromPosition);
            stockInfoList.add(toPosition, stockInfo);
            notifyItemMoved(fromPosition, toPosition);
        }
    }

}
