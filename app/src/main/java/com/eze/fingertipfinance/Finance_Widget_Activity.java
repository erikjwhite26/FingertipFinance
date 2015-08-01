package com.eze.fingertipfinance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Finance_Widget_Activity extends Activity implements AdapterView.OnItemSelectedListener {

    private Finance_Input currentInput;
    private Finance_DataSource ds = new Finance_DataSource(Finance_Widget_Activity.this);
    private List<Finance_Input> inputs = new ArrayList<Finance_Input>();
    private List<String> inputNames = new ArrayList<String>();
    private float amount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance__widget);

        try {
            ds.open();
            inputs = ds.getFinanceInputs(Finance_Constants.SORT_NAME);
        }
        catch(Exception e){}
        finally {
            ds.close();
        }

        Spinner spinner = (Spinner) findViewById(R.id.widget_spinner);
        for(Finance_Input input : inputs){
            inputNames.add(input.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, inputNames);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        initCancelButton();
        initSaveButton();
        initTextChangedEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_finance__widget, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String name = String.valueOf(adapterView.getItemAtPosition(i));
        for(Finance_Input input : inputs){
            if(input.getName().equals(name)){
                currentInput = input;
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void initSaveButton(){
        Button save = (Button) findViewById(R.id.widget_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean wasSuccessful = false;
                try {
                    ds.open();
                    currentInput.setAmount(currentInput.getAmount() + amount);

                    wasSuccessful = ds.updateFinance(currentInput);

                    Finance_Past_Amounts pastAmount = new Finance_Past_Amounts();
                    pastAmount.setFinanceId(currentInput.getId());
                    pastAmount.setPastAmount(amount);
                    Time t = new Time();
                    Calendar c = Calendar.getInstance();
                    t.set(0,0,0,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH),c.get(Calendar.YEAR));
                    pastAmount.setPastAmountDate(t);
                    int id = ds.getLastPastAmountsId();
                    pastAmount.setPastAmountId(id);
                    ds.insertPastAmounts(pastAmount);
                }
                catch (Exception e){
                }
                finally{
                    ds.close();
                }
                if(wasSuccessful){
                    Intent intent = new Intent(Finance_Widget_Activity.this, Finance_Summary.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    private void initCancelButton(){
        Button cancel = (Button) findViewById(R.id.widget_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Finance_Widget_Activity.this, Finance_Summary.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initTextChangedEvents() {
        final EditText amountText = (EditText) findViewById(R.id.widget_amount);
        amountText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (amountText.getText().toString() != null && !amountText.getText().toString().equals("")) {
                    amount = Float.valueOf(amountText.getText().toString());
                }
                checkCanSave();
            }
        });
    }

    private void checkCanSave() {
        Button save = (Button) findViewById(R.id.widget_save);
        if (amount >= 0 && currentInput.getName() != null){
            save.setEnabled(true);
        } else {
            save.setEnabled(false);
        }
    }
}
