package com.project.sean.stockmanagement;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import com.project.sean.stockmanagement.Database.AndroidPOSDBHelper;
import com.project.sean.stockmanagement.Database.StockInfo;

import java.math.BigDecimal;

/**
 *
 * Created by Sean on 01/04/2016.
 */
public class AddStockFragment extends Fragment implements View.OnClickListener {

    private AndroidPOSDBHelper dbHelper;
    //Button to scan stock items
    private Button scanBtn;
    //Button to add stock item to the DB
    private Button addBtn;

    //EditText for user entry
    private EditText editStockID;
    private EditText editStockName;
    private EditText editSalePrice;
    private EditText editStockCost;
    private EditText editStockQuantity;
    private EditText editCategory;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AddStockFragment newInstance(int sectionNumber) {
        AddStockFragment fragment = new AddStockFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AddStockFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_stock, container, false);

        //Get instance of the DB
        dbHelper = AndroidPOSDBHelper.getInstance(getActivity());

        //TextView textView = (TextView) rootView.findViewById(R.id.add_stock_lable);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        //Initialise Button
        scanBtn = (Button)rootView.findViewById(R.id.scan_button);
        addBtn = (Button)rootView.findViewById(R.id.button_add);

        //Initialise EditText
        editStockID = (EditText)rootView.findViewById(R.id.editStockID);
        editStockName = (EditText)rootView.findViewById(R.id.editStockName);
        editSalePrice = (EditText)rootView.findViewById(R.id.editSalePrice);
        editStockCost = (EditText)rootView.findViewById(R.id.editStockCost);
        editStockQuantity = (EditText)rootView.findViewById(R.id.editStockQuantity);
        editCategory = (EditText)rootView.findViewById(R.id.editCategory);

        //Set button action listener
        scanBtn.setOnClickListener(this);
        addBtn.setOnClickListener(this);


        return rootView;
    }

    public void onClick(View v){
        if(v.getId()==R.id.scan_button) {
            IntentIntegrator.forSupportFragment(this).initiateScan();
        }
        if(v.getId()==R.id.button_add) {
            addData();
        }
    }


    /**
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            if(scanningResult.getContents() == null) {
                Toast toast = Toast.makeText(getActivity(),
                        "Cancelled from fragment!", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                String scanContent = scanningResult.getContents();
                editStockID.setText(scanContent);
                Toast toast = Toast.makeText(getActivity(),
                        "Scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(getActivity(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void addData(){
        if(isEmpty(editStockID) != true &&
                isEmpty(editStockName) != true &&
                isEmpty(editSalePrice) != true &&
                isEmpty(editStockCost) != true &&
                isEmpty(editStockQuantity) != true &&
                isEmpty(editCategory) != true) {

            if(dbHelper.exsists(editStockID.getText().toString()) != true) {

                StockInfo stockInfo = new StockInfo();

                stockInfo.setStockId(editStockID.getText().toString());
                stockInfo.setStockName(editStockName.getText().toString());
                stockInfo.setSalePrice(currencyIn(editSalePrice.getText().toString()));
                stockInfo.setCostPrice(currencyIn(editStockCost.getText().toString()));
                stockInfo.setStockQty(Integer.parseInt(editStockQuantity.getText().toString()));
                stockInfo.setCategory(editCategory.getText().toString());

                boolean isInserted = dbHelper.insertStockData(stockInfo);

                if(isInserted == true)  {
                    Toast.makeText(getActivity(), "Data inserted successfully!", Toast.LENGTH_LONG).show();
                    editStockID.getText().clear();
                    editStockName.getText().clear();
                    editSalePrice.getText().clear();
                    editStockCost.getText().clear();
                    editStockQuantity.getText().clear();
                    editCategory.getText().clear();
                } else {
                    Toast.makeText(getActivity(), "Error, data not inserted.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "Entry already exists.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "All fields must be filled.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Checks if an EditText field is empty.
     * @param etText
     * @return true if empty, false if not
     */
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }


    /**
     * Converts the currency from pounds into pence.
     * @param currency - pound
     * @return currencyInt - pence
     */
    public int currencyIn(String currency) {
        BigDecimal currencyBD = new BigDecimal(currency);
        currencyBD = currencyBD.multiply(new BigDecimal("100"));
        int currencyInt = currencyBD.intValueExact();
        return currencyInt;
    }

    /**
     * Converts the currency from pence into pounds.
     * @param currency
     * @return
     */
    public BigDecimal currencyOut(int currency) {
        BigDecimal currencyBD = new BigDecimal(currency);
        currencyBD = currencyBD.divide(new BigDecimal("100"));

        return currencyBD;
    }


//    public void viewData(){
//
//            Cursor result = dbHelper.getallStockData();
//            if (result.getCount() == 0) {
//                //Show error message
//                showMessage("Error", "No data found.");
//                return;
//            }
//
//            StringBuffer buffer = new StringBuffer();
//            //Moves cursor to the next result
//            //index 0 - ID
//            //index 1 - Name
//            //index 2 - Surname
//            //index 3 - Marks
//            while (result.moveToNext()) {
//                buffer.append("Stock ID: " + result.getString(0) + "\n");
//                buffer.append("Stock Name: " + result.getString(1) + "\n");
//                buffer.append("Sale Price: " + currencyOut(result.getInt(2)) + "\n");
//                buffer.append("Cost Price: " + currencyOut(result.getInt(3)) + "\n");
//                buffer.append("Stock Qty: " + result.getInt(4) + "\n");
//                buffer.append("Category: " + result.getString(5) + "\n");
//            }
//
//            //Show all data
//            showMessage("Product Information", buffer.toString());
//    }

    /**
     * Used to create alert dialogue.
     */
    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true); //Cancel after is use
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show(); //Will show the AlertDialog
    }
}
