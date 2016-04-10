package com.project.sean.stockmanagement.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;

import com.project.sean.stockmanagement.Database.StockContract.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Sean on 01/04/2016.
 */
public class AndroidPOSDBHelper extends SQLiteOpenHelper {

    private static AndroidPOSDBHelper sInstance;

    //Database name
    public static final String DATABASE_NAME = "AndroidPOS.db";
    //Database version
    public static final int DATABASE_VERSION = 1;
    //Table create query
    public static final String SQL_CREATE_TABLE_STOCK =
            "CREATE TABLE " + StockTable.TABLE_NAME + " (" +
                    StockTable.COL_STOCKID + " TEXT PRIMARY KEY, " +
                    StockTable.COL_STOCKNAME + " TEXT, " +
                    StockTable.COL_SALEPRICE + " INTEGER, " +
                    StockTable.COL_COSTPRICE + " INTEGER, " +
                    StockTable.COL_STOCKQTY + " INTEGER, " +
                    StockTable.COL_CATEGORY + " TEXT)";
    //Drop table query
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + StockTable.TABLE_NAME;

    /**
     * Checks if an instance of the database is already open returning that database if there is,
     * else creates a new one.
     * @param context
     * @return
     */
    public static synchronized AndroidPOSDBHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new AndroidPOSDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }



    public AndroidPOSDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_STOCK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }


    /**
     * Inserts a new stock item into the database, returns -1 if data is not inserted, otherwise
     * returns the row ID for the newly inserted row.
     * @param stockInfo
     * @return
     */
    public boolean insertStockData(StockInfo stockInfo) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Var1. Column name, Var2. Value
        contentValues.put(StockTable.COL_STOCKID, stockInfo.getStockId());
        contentValues.put(StockTable.COL_STOCKNAME, stockInfo.getStockName());
        contentValues.put(StockTable.COL_SALEPRICE, stockInfo.getSalePrice());
        contentValues.put(StockTable.COL_COSTPRICE, stockInfo.getCostPrice());
        contentValues.put(StockTable.COL_STOCKQTY, stockInfo.getStockQty());
        contentValues.put(StockTable.COL_CATEGORY, stockInfo.getCategory());

        //Var1. Table name, Var2. , Var3. ContentValues to be input
        //Returns -1 if data is not inserted
        //Returns row ID of newly inserted row if successful
        long result = db.insert(StockTable.TABLE_NAME, null, contentValues);
        if(result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks if a stock item exists already.
     * @param stockId
     * @return
     */
    public boolean exsists(String stockId) {
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor cursor = null;
        String exists = "SELECT STOCK_ID FROM " + StockTable.TABLE_NAME +
                " WHERE STOCK_ID = '" + stockId + "'";
        Cursor cursor = db.rawQuery(exists, null);
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns a Cursor object containing all stock information.
     * @return
     */
    public Cursor getallStockData(){
        String SQL_GETALLDATA = "SELECT * FROM " + StockTable.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery(SQL_GETALLDATA, null);
        return result;
    }

    /**
     * Retrieves all stock information from the database and adds it to an ArrayList.
     * @return
     */
    public List<StockInfo> getAllStockInfo() {
        List<StockInfo> stockList = new ArrayList<StockInfo>();
        //SELECT * QUERY
        String SQL_GETALLDATA = "SELECT * FROM " + StockTable.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery(SQL_GETALLDATA, null);

        if (result.moveToFirst()) {
            do {
                StockInfo stockInfo = new StockInfo();
                stockInfo.setStockId(result.getString(0));
                stockInfo.setStockName(result.getString(1));
                stockInfo.setSalePrice(result.getInt(2));
                stockInfo.setCostPrice(result.getInt(3));
                stockInfo.setStockQty(result.getInt(4));
                stockInfo.setCategory(result.getString(5));

                stockList.add(stockInfo);
            } while (result.moveToNext());
        }

        return stockList;
    }

    /**
     * Might not use, has potential for retrieving data
     * @param stockName
     * @return
     * @throws SQLException
     */
    public Cursor fetchStockByName(String stockName) throws SQLException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = null;
        if (stockName == null || stockName.length() == 0) {
            mCursor = db.query(StockTable.TABLE_NAME, new String[] {StockTable.COL_STOCKID,
            StockTable.COL_STOCKNAME, StockTable.COL_CATEGORY, StockTable.COL_SALEPRICE,
            StockTable.COL_COSTPRICE}, null, null, null, null, null);
        } else {

        }
        if(mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

}
