package com.eze.fingertipfinance;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.logging.Logger;

public class Finance_Add_Input extends FragmentActivity implements Finance_DatePicker_Fragment.SaveDateListener {

    private Logger logger = Logger.getLogger("Finance_Add_Input");

    private Finance_Input currentInput;
    private ArrayList<Finance_Past_Amounts> pastAmountList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance__add__input);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if(extras.getInt("inputid") > 0){
                initInput(extras.getInt("inputid"));
            }
            else{
                currentInput = new Finance_Input();
            }

            if(extras.getString("type") != null || extras.getString("type").equals("")){
                currentInput.setType(extras.getString("type"));
            }
        }

        initSaveButton();
        initCancelButton();
        initTextChangedEvents();
        initAddAmountButton();
        checkCanSave();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new Finance_DatePicker_Fragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.finance__add__input, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_delete){
            if(currentInput.getId() > -1) {
                deletePastAmounts();
                return deleteInput();
            }
        }
        else if(id == R.id.action_clear_past_amounts){
            if(currentInput.getId() > -1){
                deletePastAmounts();
                initInput(currentInput.getId());
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean deleteInput() {
        boolean didDelete = false;
        Finance_DataSource ds = new Finance_DataSource(Finance_Add_Input.this);
        try{
            ds.open();
            didDelete = ds.deleteInput(currentInput.getId());
        }
        catch(Exception e){
            logger.info("Error deleting input from id: " + currentInput.getId() + "\n" + e.getStackTrace()[0]);
        }
        finally{
            ds.close();
        }

        Intent intent = new Intent(Finance_Add_Input.this, Finance_Summary.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        return didDelete;
    }

    private boolean deletePastAmounts() {
        Finance_DataSource ds = new Finance_DataSource(Finance_Add_Input.this);
        try{
            ds.open();
            return ds.deletePastAmounts(currentInput.getId());
        }
        catch(Exception e){
            logger.info("Error deleting past amounts from id: " + currentInput.getId() + "\n" + e.getStackTrace()[0]);
        }
        finally{
            ds.close();
        }
        return false;
    }

    private void initInput(int id){
        Finance_DataSource ds = new Finance_DataSource(Finance_Add_Input.this);
        try{
            ds.open();
            currentInput = ds.getFinanceInput(id);
            pastAmountList = ds.getFinancePastAmounts(currentInput.getId());
        }
        catch(Exception e){
            logger.info("Error getting input from id: " + id + "\n" + e.getStackTrace()[0]);
        }
        finally{
            ds.close();
        }
        EditText name = (EditText) findViewById(R.id.add_name_edit);
        EditText amount = (EditText) findViewById(R.id.add_amount_edit);
        Button date = (Button) findViewById(R.id.add_date);
        RadioButton onetime = (RadioButton) findViewById(R.id.add_occur_onetime);
        RadioButton weekly = (RadioButton) findViewById(R.id.add_occur_weekly);
        RadioButton biweekly = (RadioButton) findViewById(R.id.add_occur_biweekly);
        RadioButton monthly = (RadioButton) findViewById(R.id.add_occur_monthly);
        TextView pastAmounts = (TextView) findViewById(R.id.add_past_amounts_list);

        name.setText(currentInput.getName());
        amount.setText(String.format("%.2f", currentInput.getAmount()));

        date.setText(DateFormat.format("MM/dd/yyyy", currentInput.getOccurs().toMillis(false)).toString());
        if(currentInput.getOccurstype().equals(Finance_Constants.OCCURTYPE_ONETIME)){
            onetime.setChecked(true);
        }
        else if(currentInput.getOccurstype().equals(Finance_Constants.OCCURTYPE_WEEKLY)){
            weekly.setChecked(true);
        }
        else if(currentInput.getOccurstype().equals(Finance_Constants.OCCURTYPE_BIWEEKLY)){
            biweekly.setChecked(true);
        }
        else {
            monthly.setChecked(true);
        }

        if(pastAmountList != null){
            int longestAmount = 0;
            for(Finance_Past_Amounts pastAmount : pastAmountList){
                if(longestAmount < String.format("%.2f", pastAmount.getPastAmount()).length()){
                    longestAmount = String.format("%.2f", pastAmount.getPastAmount()).length();
                }
            }
            String pastAmountsStr = "";
            for(Finance_Past_Amounts pastAmount : pastAmountList){
                pastAmountsStr += "$ ";
                int strLength = String.format("%.2f", pastAmount.getPastAmount()).length();
                while(strLength < longestAmount){
                    pastAmountsStr += "  ";
                    strLength++;
                }
                pastAmountsStr += String.format("%.2f", pastAmount.getPastAmount());
                int index = longestAmount;
                while(index < 10){
                    pastAmountsStr += "  ";
                    index++;
                }
                pastAmountsStr += DateFormat.format("MM/dd/yyyy", pastAmount.getPastAmountDate().toMillis(false)).toString() + "\n";
            }
            pastAmounts.setText(pastAmountsStr);
        }
    }

    private void initSaveButton(){
        Button save = (Button) findViewById(R.id.add_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Finance_DataSource ds = new Finance_DataSource(Finance_Add_Input.this);
                boolean wasSuccessful = false;
                try {
                    ds.open();

                    if(currentInput.getId() == -1){
                        wasSuccessful = ds.insertFinance(currentInput);
                        int newId = ds.getLastFinanceId();
                        currentInput.setId(newId);
                    }
                    else{
                        wasSuccessful = ds.updateFinance(currentInput);
                    }

                }
                catch (Exception e){
                    logger.info("Error while saving input:\n" + e.getStackTrace()[0]);
                }
                finally{
                    ds.close();
                }
                if(wasSuccessful){
                    Intent intent = new Intent(Finance_Add_Input.this, Finance_Summary.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    private void initCancelButton(){
        Button cancel = (Button) findViewById(R.id.add_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Finance_Add_Input.this, Finance_Summary.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initTextChangedEvents(){
        final EditText name = (EditText) findViewById(R.id.add_name_edit);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentInput.setName(name.getText().toString());
                checkCanSave();
            }
        });

        final EditText amount = (EditText) findViewById(R.id.add_amount_edit);
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(amount.getText().toString() != null && !amount.getText().toString().equals("")) {
                    float amt = Float.valueOf(amount.getText().toString());
                    currentInput.setAmount(amt);
                }else{
                    currentInput.setAmount(0);
                }
                checkCanSave();
            }
        });

        final RadioGroup occurstype = (RadioGroup) findViewById(R.id.add_occur_rb);
        occurstype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton onetime = (RadioButton) findViewById(R.id.add_occur_onetime);
                RadioButton weekly = (RadioButton) findViewById(R.id.add_occur_weekly);
                RadioButton biweekly = (RadioButton) findViewById(R.id.add_occur_biweekly);
                if(onetime.isChecked()){
                    currentInput.setOccurstype(Finance_Constants.OCCURTYPE_ONETIME);
                }
                else if(weekly.isChecked()){
                    currentInput.setOccurstype(Finance_Constants.OCCURTYPE_WEEKLY);
                }
                else if(biweekly.isChecked()){
                    currentInput.setOccurstype(Finance_Constants.OCCURTYPE_BIWEEKLY);
                }
                else{
                    currentInput.setOccurstype(Finance_Constants.OCCURTYPE_MONTHLY);
                }
                if(currentInput.getOccurstype() != null) {
                    checkCanSave();
                }
            }
        });
    }

    private void checkCanSave() {
        Button save = (Button) findViewById(R.id.add_save);
        if (currentInput.getAmount() >= 0 && currentInput.getName() != null) {
            if (!currentInput.getName().equals("") && !currentInput.getType().equals("") && currentInput.getOccurs() != null
                    && !currentInput.getOccurstype().equals("")) {
                save.setEnabled(true);
            } else {
                save.setEnabled(false);
            }
        }else{
            save.setEnabled(false);
        }
    }

    private void initAddAmountButton(){
        Button addAmount = (Button) findViewById(R.id.add_amount_button);
        final Finance_Add_Input_Dialog dialog = new Finance_Add_Input_Dialog();
        addAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                dialog.show(fm, "ShowInputs");
            }
        });
    }

    public void setAddAmountValue(float amountToAdd){
        if(amountToAdd > 0){
            currentInput.setAmount(currentInput.getAmount() + amountToAdd);

            EditText amount = (EditText) findViewById(R.id.add_amount_edit);
            amount.setText(String.format("%.2f", currentInput.getAmount()));
        }
    }

    @Override
    public void didFinishDatePickerDialog(Time selectedTime) {
        Button date = (Button) findViewById(R.id.add_date);
        date.setText(DateFormat.format("MM/dd/yyyy", selectedTime.toMillis(false)).toString());
        currentInput.setOccurs(selectedTime);
        checkCanSave();
    }

    public Finance_Input getCurrentInput() {
        return currentInput;
    }
}
