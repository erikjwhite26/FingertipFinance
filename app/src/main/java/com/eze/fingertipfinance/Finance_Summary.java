package com.eze.fingertipfinance;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;


public class Finance_Summary extends ListActivity {

    private Logger logger = Logger.getLogger("Finance_Summary");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance__summary);

        initNewBillButton();
        initNewIncomeButton();
        initCalendarButton();
    }

    @Override
    public void onResume(){
        super.onResume();

        String sortBy = getSharedPreferences(Finance_Constants.SORT_LIST_PREFERENCES, Context.MODE_PRIVATE)
                .getString(Finance_Constants.SORTFIELD, Finance_Constants.SORT_AMOUNT);
        if(sortBy == null){
            sortBy = Finance_Constants.SORT_DATE;
        }
        sortList(sortBy);
    }

    private boolean sortList(String sortStr) {
        Finance_DataSource ds = new Finance_DataSource(this);
        try {
            ds.open();
            final ArrayList<Finance_Input> inputs = ds.getFinanceInputs(sortStr);
            Finance_Adapter inputAdapter;

            setTotalAmount(inputs);

            if(inputs.size() > 0){
                inputAdapter = new Finance_Adapter(this, inputs);
                setListAdapter(inputAdapter);
                ListView inputListView = getListView();
                inputListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                        Finance_Input selectedInput = inputs.get(position);
                        Intent intent = new Intent(Finance_Summary.this, Finance_Add_Input.class);
                        intent.putExtra("inputid", selectedInput.getId());
                        intent.putExtra("type", selectedInput.getType());
                        startActivity(intent);
                    }
                });
            }

        }
        catch(Exception e){
            logger.info("Error calling database to populate list in Finance_Summary: \n" + e);
            return false;
        }
        finally {
            ds.close();
        }
        return true;
    }

    private void setTotalAmount(ArrayList<Finance_Input> inputs) {
        float total = 0;
        Calendar todayCalendar = Calendar.getInstance();
        Time today = new Time();
        today.set(todayCalendar.getTimeInMillis());

        for(Finance_Input input : inputs){
            Time inputDate = input.getOccurs();
            boolean onetime = false;
            while((inputDate.before(today) || inputDate.equals(today)) && !onetime){
                if(input.getType().equals(Finance_Constants.INCOME)){
                    total += input.getAmount();
                }
                else{
                    total -= input.getAmount();
                }
                if(!inputDate.equals(today)) {
                    inputDate = Finance_Util.adjustDate(input.getOccurstype(), inputDate);
                }
                if(input.getOccurstype().equals(Finance_Constants.OCCURTYPE_ONETIME)){
                    onetime = true;
                }
            }
        }

        TextView totalAmount = (TextView) findViewById(R.id.summary_total_amount);
        totalAmount.setText("$ " + String.format("%.2f", total));
    }

    private void initNewIncomeButton(){
        Button addIncome = (Button) findViewById(R.id.summary_new_income);
        addIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Finance_Summary.this, Finance_Add_Input.class);
                intent.putExtra("type", Finance_Constants.INCOME);
                intent.putExtra("inputid", -1);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initNewBillButton(){
        Button addBill = (Button) findViewById(R.id.summary_new_bill);
        addBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Finance_Summary.this, Finance_Add_Input.class);
                intent.putExtra("type", Finance_Constants.BILL);
                intent.putExtra("inputid", -1);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initCalendarButton() {
        ImageButton calendarButton = (ImageButton) findViewById(R.id.summary_calendar);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Finance_Summary.this, Finance_Custom_Calendar.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.finance__summary, menu);
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
        else if (id == R.id.action_sort_amount) {
            getSharedPreferences(Finance_Constants.SORT_LIST_PREFERENCES, Context.MODE_PRIVATE).edit()
                    .putString(Finance_Constants.SORTFIELD, Finance_Constants.SORT_AMOUNT).commit();
            return sortList(Finance_Constants.SORT_AMOUNT);
        }
        else if(id == R.id.action_sort_date){
            getSharedPreferences(Finance_Constants.SORT_LIST_PREFERENCES, Context.MODE_PRIVATE).edit()
                    .putString(Finance_Constants.SORTFIELD, Finance_Constants.SORT_DATE).commit();
            return sortList(Finance_Constants.SORT_DATE);
        }
        else if (id == R.id.action_sort_name){
            getSharedPreferences(Finance_Constants.SORT_LIST_PREFERENCES, Context.MODE_PRIVATE).edit()
                    .putString(Finance_Constants.SORTFIELD, Finance_Constants.SORT_NAME).commit();
            return sortList(Finance_Constants.SORT_NAME);
        }
        else if (id == R.id.action_calendar){
            Intent intent = new Intent(Finance_Summary.this, Finance_Calendar.class);
            this.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
