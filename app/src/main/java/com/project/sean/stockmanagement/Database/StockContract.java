package com.project.sean.stockmanagement.Database;

import android.provider.BaseColumns;

/**
 * Created by Sean on 01/04/2016.
 */
public class StockContract {

    //Empty constructor
    public StockContract() {}

    public static abstract class StockTable implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "STOCK_INFO";
        //Column names
        public static final String COL_STOCKID = "STOCK_ID";
        public static final String COL_STOCKNAME = "STOCK_NAME";
        public static final String COL_SALEPRICE = "SALE_PRICE";
        public static final String COL_COSTPRICE = "COST_PRICE";
        public static final String COL_STOCKQTY = "STOCK_QTY";
        public static final String COL_CATEGORY = "CATEGORY";
    }
}
